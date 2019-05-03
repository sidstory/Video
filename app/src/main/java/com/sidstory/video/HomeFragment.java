package com.sidstory.video;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.coder.zzq.smartshow.toast.SmartToast;
import com.sidstory.video.Utils.ParserData;
import com.sidstory.video.Utils.RecycleAdapter;
import com.sidstory.video.Utils.VideoData;

import java.util.ArrayList;
import java.util.List;

import io.rmiri.skeleton.Master.IsCanSetAdapterListener;

public class HomeFragment extends Fragment {
    RecycleAdapter adapter=null;
    List<VideoData> data=new ArrayList<>();
    private RecyclerView recyclerView;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                ArrayList<VideoData> list= (ArrayList<VideoData>) msg.obj;
                adapter.addMoreDataAndSkeletonFinish(list);
            }
        }
    };
    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.home_recycle_view);
        StaggeredGridLayoutManager manager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter=new RecycleAdapter(getContext(), new ArrayList<VideoData>(), recyclerView,new IsCanSetAdapterListener() {
            @Override
            public void isCanSet() {
                recyclerView.setAdapter(adapter);
            }
        });
        if (ParserData.isNetworkConnected(getContext())){
            getData(ParserData.getUrl(getContext()));
        }else SmartToast.error("网络连接失败！");

        return view;
    }

    private void getData(String URL){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<VideoData> list = ParserData.paserHome(ParserData.getUrl(getContext()),ParserData.PARSER_URL,getContext());
                if (list!=null){
                    Message msg=Message.obtain();
                    msg.what=0;
                    msg.obj=list;
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }
    public void onButtonPressed(Uri uri) {
        }
    }


