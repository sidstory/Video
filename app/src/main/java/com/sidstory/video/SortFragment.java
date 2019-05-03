package com.sidstory.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sidstory.video.Utils.ParserData;
import com.sidstory.video.Utils.SortAdapter;
import com.sidstory.video.Utils.SortData;

import java.util.ArrayList;

import io.rmiri.skeleton.Master.IsCanSetAdapterListener;

public class SortFragment extends Fragment {
        private RecyclerView recyclerView;
        ArrayList<SortData> list=new ArrayList();
        SortAdapter adapter=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_sort,container,false);
            recyclerView=view.findViewById(R.id.sort_recycle);
            StaggeredGridLayoutManager manager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            recyclerView.setHasFixedSize(true);
            adapter=new SortAdapter(getContext(), new ArrayList<SortData>(), recyclerView,new IsCanSetAdapterListener() {
            @Override
            public void isCanSet() {
                recyclerView.setAdapter(adapter);
            }
             });
            init();
            return view;
    }

    private void init() {
        SortData data1=new SortData();
        data1.setTitle("最新上传");
        data1.setVideourl(ParserData.getUrl(getContext())+"/news");
        data1.setImgurl(R.drawable.news);
        list.add(data1);
        SortData data2=new SortData();
        data2.setTitle("国产精品");
        data2.setVideourl(ParserData.getUrl(getContext())+"/guochan");
        data2.setImgurl(R.drawable.guochan);
        list.add(data2);
        SortData data3=new SortData();
        data3.setTitle("主播精选");
        data3.setVideourl(ParserData.getUrl(getContext())+"/meinvzhubo/kr");
        data3.setImgurl(R.drawable.zhubo);
        list.add(data3);
        SortData data4=new SortData();
        data4.setTitle("微拍福利");
        data4.setVideourl(ParserData.getUrl(getContext())+"/fuli/nmxz");
        data4.setImgurl(R.drawable.fuli);
        list.add(data4);
        SortData data5=new SortData();
        data5.setTitle("日韩有码");
        data5.setVideourl(ParserData.getUrl(getContext())+"/ribenyouma");
        data5.setImgurl(R.drawable.youma);
        list.add(data5);
        SortData data6=new SortData();
        data6.setTitle("日韩无码");
        data6.setVideourl(ParserData.getUrl(getContext())+"/ribenwuma");
        data6.setImgurl(R.drawable.wuma);
        list.add(data6);
        SortData data7=new SortData();
        data7.setTitle("欧美风情");
        data7.setVideourl(ParserData.getUrl(getContext())+"/oumei/jingpin");
        data7.setImgurl(R.drawable.oumei);
        list.add(data7);
        SortData data8=new SortData();
        data8.setTitle("伦理综艺");
        data8.setVideourl(ParserData.getUrl(getContext())+"/lunli/rihan");
        data8.setImgurl(R.drawable.lunli);
        list.add(data8);
        SortData data9=new SortData();
        data9.setTitle("VR影院");
        data9.setVideourl(ParserData.getUrl(getContext())+"/VR/rihan");
        data9.setImgurl(R.drawable.vr);
        list.add(data9);
        SortData data10=new SortData();
        data10.setTitle("成人动漫");
        data10.setVideourl(ParserData.getUrl(getContext())+"/dongman/wuma");
        data10.setImgurl(R.drawable.dongman);
        list.add(data10);
        adapter.addMoreDataAndSkeletonFinish(list);
    }

}
