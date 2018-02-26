package app.zd.androidscreenshot.adapter.delegate;

import app.zd.androidscreenshot.adapter.ViewHolder;

public interface ItemViewDelegate<T> {

    public abstract int getItemViewLayoutId();

    public abstract boolean isForViewType(T item, int position);

    public abstract void convert(ViewHolder holder, T t, int position);
}
