package com.sidstory.video.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sidstory.video.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.rmiri.skeleton.Master.IsCanSetAdapterListener;
import io.rmiri.skeleton.Master.SkeletonConfig;
import io.rmiri.skeleton.utils.CLog;

public abstract class AdapterSkeleton<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context context;
    protected List<T> items = Collections.emptyList();
    protected IsCanSetAdapterListener isCanSetAdapterListener;
    protected SkeletonConfig skeletonConfig = new SkeletonConfig();
    public AdapterSkeleton() {
    }

    public void measureHeightRecyclerViewAndItem(final RecyclerView recyclerView, final int idLayout, final int add) {
        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                AdapterSkeleton.this.skeletonConfig.setRecyclerViewHeight((float) recyclerView.getHeight());
                View view = ((LayoutInflater) AdapterSkeleton.this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(idLayout, (ViewGroup) null);
                view.getRootView().measure(0, 0);
                AdapterSkeleton.this.skeletonConfig.setItemHeight((float) view.getRootView().getMeasuredHeight());
                AdapterSkeleton.this.skeletonConfig.setNumberItemShow(Math.round(AdapterSkeleton.this.skeletonConfig.getRecyclerViewHeight() / AdapterSkeleton.this.skeletonConfig.getItemHeight()) * 2 + add);
                CLog.i("skeletonConfig.getItemHeight == " + AdapterSkeleton.this.skeletonConfig.getItemHeight() + "   skeletonConfig.getRecyclerViewHeight  " + AdapterSkeleton.this.skeletonConfig.getRecyclerViewHeight() + "   skeletonConfig.getNumberItemShow  " + AdapterSkeleton.this.skeletonConfig.getNumberItemShow());
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                AdapterSkeleton.this.isCanSetAdapterListener.isCanSet();
            }
        });
    }

    public int getItemCount() {
        if (this.skeletonConfig.isSkeletonIsOn()) {
            if (this.skeletonConfig.getItemHeight() == 0.0F) {
                CLog.i("getItemCount ==> getItemHeight() is zero : 1");
                return 1;
            } else {
                CLog.i("getItemCount ==> getNumberItemShow: " + this.skeletonConfig.getNumberItemShow());
                return this.skeletonConfig.getNumberItemShow();
            }
        } else {
            CLog.i("getItemCount ==> items.size(): " + this.items.size());
            return this.items.size() ;
        }
    }

    public void addMoreDataAndSkeletonFinish(ArrayList<T> dataObjects) {
        this.items = new ArrayList();
        this.items.addAll(dataObjects);
        this.skeletonConfig.setSkeletonIsOn(false);
        if (this.items == null || this.items.size() == 0) {
            this.notifyDataSetChanged();
        }

        if (this.skeletonConfig.getNumberItemShow() > this.items.size()) {
            this.notifyItemRangeRemoved(this.items.size(), this.skeletonConfig.getNumberItemShow());
        }

        this.notifyItemRangeChanged(0, this.items.size(), 1);
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public IsCanSetAdapterListener getIsCanSetAdapterListener() {
        return this.isCanSetAdapterListener;
    }

    public void setIsCanSetAdapterListener(IsCanSetAdapterListener IsCanSetAdapterListener) {
        this.isCanSetAdapterListener = IsCanSetAdapterListener;
    }

    public SkeletonConfig getSkeletonConfig() {
        return this.skeletonConfig;
    }

    public void setSkeletonConfig(SkeletonConfig skeletonConfig) {
        this.skeletonConfig = skeletonConfig;
    }

}