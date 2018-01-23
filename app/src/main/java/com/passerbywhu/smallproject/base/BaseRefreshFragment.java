package com.passerbywhu.smallproject.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout;
import com.passerbywhu.smallproject.R;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.functions.Consumer;

public abstract class BaseRefreshFragment<V> extends BaseFragment implements RefreshInterface<V> {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected TriggerObservable mTriggerObservable;
    protected boolean mHasReadFromCache;
    protected LifecycleTransformer transformer;

    public abstract View onCreateRefreshView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected boolean autoRefresh() {
        return false;
    }

    protected boolean readCache() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transformer = bindUntilEvent(FragmentEvent.DESTROY_VIEW);
    }

    /**
     * 不要覆写这个方法
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Deprecated
    @Override
    public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_refresh_fragment, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        View refreshView = onCreateRefreshView(inflater, mSwipeRefreshLayout, savedInstanceState);
        mSwipeRefreshLayout.addView(refreshView);
        mTriggerObservable = new TriggerObservable();
        RxSwipeRefreshLayout.refreshes(mSwipeRefreshLayout)//.compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .mergeWith(mTriggerObservable)
                .subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (readCache() && !mHasReadFromCache) {
                    mHasReadFromCache = true;
                    refreshFromCache();
                } else {
                    refresh();
                }
            }
        });
        if (autoRefresh()) {
            triggerRefresh();
        }
        return view;
    }

    @Override
    public void refreshFromCache() {

    }

    @Override
    public void refreshComplete(V v) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshFromCacheComplete(V v) {
        refresh();
    }

    @Override
    public void refreshError(Throwable throwable) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshFromCacheError(Throwable throwable) {
        refresh();
    }

    public void triggerRefresh() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        mTriggerObservable.trigger("refresh");
    }
}
