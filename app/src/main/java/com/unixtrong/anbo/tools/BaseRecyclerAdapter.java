package com.unixtrong.anbo.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Author(s): danyun
 * Date: 2017/9/3
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.Holder> {

    private static final int TYPE_HEADER = 1000;
    private static final int TYPE_NORMAL = 1001;

    private List<T> mData;
    private View mHeaderView;

    public BaseRecyclerAdapter(List<T> data) {
        this.mData = data;
    }

    protected abstract Holder onItemCreate(ViewGroup parent, int viewType);

    protected abstract void onItemBind(Holder holder, int pos, T data);

    protected abstract int getDataViewType(int position);

    public List<T> getData() {
        return mData;
    }

    protected View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void addDatas(List<T> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) return TYPE_HEADER;
        return getDataViewType(getDataPosition(position));
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        return onItemCreate(parent, viewType);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;

        final int pos = getDataPosition(position);
        final T data = mData.get(pos);
        onItemBind(holder, pos, data);
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mData.size() : mData.size() + 1;
    }

    private int getDataPosition(int position) {
        return mHeaderView == null ? position : position - 1;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }
}
