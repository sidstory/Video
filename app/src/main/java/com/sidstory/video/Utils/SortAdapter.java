package com.sidstory.video.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sidstory.video.DetileActivity;
import com.sidstory.video.R;
import com.sidstory.video.SortDisplayActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.rmiri.skeleton.Master.IsCanSetAdapterListener;
import io.rmiri.skeleton.SkeletonGroup;;

public class SortAdapter extends AdapterSkeleton<SortData,SortAdapter.ViewHolder> {
    public SortAdapter(final Context context,List<SortData> data,final RecyclerView view,final IsCanSetAdapterListener isCanSetAdapterListener) {
        this.items=data;
        this.context=context;
        this.isCanSetAdapterListener=isCanSetAdapterListener;
        measureHeightRecyclerViewAndItem(view,R.layout.sort_recycleview,0);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sort_recycleview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (skeletonConfig.isSkeletonIsOn()) {
            return;
        } else {
            holder.skeletonGroup.setShowSkeleton(false);
            holder.skeletonGroup.finishAnimation();
        }
        final SortData data=items.get(position);
        Glide.with(holder.imageView.getContext()).load(data.getImgurl()).apply(RequestOptions.bitmapTransform(new RoundedCorners( 11)).override(220,100)).into(holder.imageView);
        holder.textView.setText(data.getTitle());
        holder.skeletonGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), SortDisplayActivity.class);
                intent.putExtra("title",data.getTitle());
                intent.putExtra("url",data.getVideourl());
                intent.putExtra("img",data.getImgurl());
                intent.putExtra("title",data.getTitle());
                context.startActivity(intent);
            }
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        private SkeletonGroup skeletonGroup;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.sort_recycle_txt);
            imageView=itemView.findViewById(R.id.sort_recycle_img);
            skeletonGroup=itemView.findViewById(R.id.sort_skeletonGroup);

        }
    }

}
