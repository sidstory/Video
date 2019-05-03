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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.rmiri.skeleton.Master.IsCanSetAdapterListener;
import io.rmiri.skeleton.SkeletonGroup;;

public class DetailAdapter extends AdapterSkeleton<VideoData,DetailAdapter.ViewHolder> {
    public DetailAdapter(final Context context,List<VideoData> data,final RecyclerView view,final IsCanSetAdapterListener isCanSetAdapterListener) {
        this.items=data;
        this.context=context;
        this.isCanSetAdapterListener=isCanSetAdapterListener;
        measureHeightRecyclerViewAndItem(view,R.layout.detail_recycleview,0);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_recycleview,parent,false);
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
        final VideoData data=items.get(position);
        Glide.with(context).load(data.getImgurl()).apply(RequestOptions.bitmapTransform(new RoundedCorners( 11)).override(220,100)).into(holder.imageView);
        holder.textView.setText(data.getTitle());
        holder.time.setText(data.getTime());
        holder.watch.setText(data.getWatch());
        holder.skeletonGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), DetileActivity.class);
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
        private TextView watch;
        private TextView time;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.detail_recycle_txt);
            imageView=itemView.findViewById(R.id.detail_recycle_img);
            skeletonGroup=itemView.findViewById(R.id.detail_skeletonGroup);
            time=itemView.findViewById(R.id.detail_time);
            watch=itemView.findViewById(R.id.detail_watch);
        }
    }

}
