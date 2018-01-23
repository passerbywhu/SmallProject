package com.passerbywhu.smallproject.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView;
import com.passerbywhu.smallproject.R;
import com.passerbywhu.smallproject.recyclerview.HeaderFooterAdapter;
import com.passerbywhu.smallproject.recyclerview.RecyclerViewUtils;
import com.passerbywhu.smallproject.recyclerview.SectionAdapter;
import com.passerbywhu.smallproject.recyclerview.empty.DefaultEmptyContentView;
import com.passerbywhu.smallproject.recyclerview.empty.EmptyContentView;
import com.passerbywhu.smallproject.recyclerview.footer.DefaultLoadingView;
import com.passerbywhu.smallproject.recyclerview.footer.FooterLoadingView;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseRecyclerViewFragment<T, V> extends BaseRefreshFragment<T> {
    protected RecyclerView mPageRecyclerView;
    protected HeaderFooterAdapter<V, ?> mAdapter;
    protected FooterLoadingView mFooterLoadingView;
    protected EmptyContentView mEmptyContentView;
    private FrameLayout mContentArea;
    public static final int START_PAGE = 1;
    protected int mPage = START_PAGE, mPageSize = 20;
    private int mPrePage = Integer.MIN_VALUE;
    private boolean isLoading = false;
    private boolean hasOnceLoaded = false;
    private boolean hasMoreData = false;
    private Disposable disposable;
    private boolean isCanceled = false;

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    /**
     * 如果有需要增加HeaderView或者FooterView的操作，在此函数中完成
     * @return
     */
    protected abstract HeaderFooterAdapter<V, ?> getAdapter();

    protected abstract Observable<T> loadPageFromCache(int page, int pageSize);
    protected abstract Observable<T> loadPage(int page, int pageSize);

    protected abstract Observable<List<V>> map(Observable<T> observable);

    protected abstract void onError(Throwable throwable);

    protected FooterLoadingView getLoadingView() {
        return new DefaultLoadingView(getContext());
    }

    protected EmptyContentView getEmptyView() {
        return new DefaultEmptyContentView(getContext()) {
            @Override
            public void empty() {
                setText(R.string.nothing_you_want);
            }
        };
    }

    protected View getNavigationBar(ViewGroup parent) {
        return null;
    }

    protected View getHeadView(ViewGroup parent) {
        return null;
    }
    protected void firstViewFullPercent(double viewPercent){

    }

    protected void onItemClick(int position, View view, V item) {

    }

    protected void cancel() {
        if (mSwipeRefreshLayout == null || !mSwipeRefreshLayout.isRefreshing()) {
            return;
        }
        isCanceled = true;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void refresh() {
        mPrePage = mPage;
        mPage = START_PAGE;
        load(false);
    }

    @Override
    public void refreshFromCache() {
        mPrePage = mPage;
        mPage = START_PAGE;
        load(true);
    }

    @Override
    public void refreshError(Throwable throwable) {
        loadError(throwable);
        super.refreshError(throwable);
    }

    @Override
    public View onCreateRefreshView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_recyclerview_fragment, container, false);
        mPageRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mPageRecyclerView.setLayoutManager(getLayoutManager());
        mAdapter = getAdapter();
        if (mAdapter != null && mAdapter instanceof SectionAdapter) {
            ((SectionAdapter) mAdapter).bindToRecyclerView(mPageRecyclerView);
        } else {
            mPageRecyclerView.setAdapter(mAdapter);
        }
        mFooterLoadingView = getLoadingView();
        mAdapter.addFooterView(mFooterLoadingView);
        mAdapter.addHeaderView(getHeadView(mPageRecyclerView));
        mAdapter.setOnItemClickListener(new HeaderFooterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View itemView) {
                BaseRecyclerViewFragment.this.onItemClick(position, itemView, mAdapter.getItem(position));
            }
        });
        mEmptyContentView = getEmptyView();
        mContentArea = (FrameLayout) view.findViewById(R.id.contentArea);
        mContentArea.addView(mEmptyContentView);
        View statusBarView = getNavigationBar(mContentArea);
        if (statusBarView != null) {
            mContentArea.addView(statusBarView);
        }

        RxRecyclerView.scrollStateChanges(mPageRecyclerView).compose(transformer).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean lastItemFullVisible = RecyclerViewUtils.isLastItemFullVisible(mPageRecyclerView);
                    if (hasMoreData && !isLoading && lastItemFullVisible) {
                        mFooterLoadingView.onLoading();
                        load(false);
                    }
                }

            }
        });
        RxRecyclerView.scrollEvents(mPageRecyclerView).subscribe(new Consumer<RecyclerViewScrollEvent>() {
            @Override
            public void accept(RecyclerViewScrollEvent recyclerViewScrollEvent) throws Exception {
                int position = RecyclerViewUtils.findFirstVisiblePosition(mPageRecyclerView);
                if (position == 0) {
                    View firstView = mPageRecyclerView.getLayoutManager().getChildAt(position);
                    int viewHeight = firstView.getHeight();
                    int viewBottom = firstView.getBottom();
                    firstViewFullPercent(viewBottom / (1.0 * viewHeight));
                } else {
                    firstViewFullPercent(0);
                }
            }
        });
        return view;
    }

    private T t;

    protected void load(final boolean fromCache) {
        isLoading = true;
        if (mPage == START_PAGE && mAdapter.getData().isEmpty()) {
            mEmptyContentView.loading();
            mEmptyContentView.show();
        }
        Observable<T> pageObservable = fromCache? loadPageFromCache(mPage, mPageSize) : loadPage(mPage, mPageSize);
        pageObservable = pageObservable.subscribeOn(Schedulers.io()).compose((LifecycleTransformer<T>) transformer).map(new Function<T, T>() {
            @Override
            public T apply(T t) throws Exception {
                BaseRecyclerViewFragment.this.t = t;
                return t;
            }
        });
        Observable<List<V>> result = map(pageObservable);
        if (isCanceled) {
            isCanceled = false;
            return;
        }
        disposable = result.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<V>>() {
            @Override
            public void accept(List<V> vs) throws Exception {
                isCanceled = false;
                if (fromCache) {
                    loadComplete(vs);
                    refreshFromCacheComplete(t);
                } else {
                    loadComplete(vs);
                    refreshComplete(t);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                isCanceled = false;
                if (fromCache) {
                    refreshFromCacheError(throwable);
                    //cacheError不用去管
                } else {
                    refreshError(throwable);
                    loadError(throwable);
                }
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                isCanceled = false;
            }
        });
        if (isCanceled) {
            disposable.dispose();
        }
    }

    protected void loadComplete(List<V> result) {
        if (mPage == START_PAGE) {
            mAdapter.clear();
        }
        mAdapter.appendItems(result);

        if (result.size() < mPageSize) {
            hasMoreData = false;
            mFooterLoadingView.onComplete();
        } else {
            hasMoreData = true;
            mFooterLoadingView.onReset();
        }

        if (mAdapter.getData().isEmpty()) {
            mEmptyContentView.empty();
            mEmptyContentView.show();
        } else {
            mEmptyContentView.hide();
        }

        mPage ++;
        isLoading = false;
    }

    protected void loadError(Throwable throwable) {
        mPage = mPrePage;  //如果是刷新失败，那么mPage变为了0，加载更多页数就会出问题。所以需要恢复
        onError(throwable);
        if (mAdapter.getData().isEmpty()) {
            mEmptyContentView.empty();
            mEmptyContentView.show();
        } else {
            mEmptyContentView.hide();
        }
        mFooterLoadingView.onLoaded();
    }

    @Override
    protected void onContentVisible() {
        super.onContentVisible();
        if (!autoRefresh()) {
            if (!hasOnceLoaded) {
                hasOnceLoaded = true;
                triggerRefresh();
            }
        }
    }

    public void smoothToTop(){
        if (mPageRecyclerView != null) {
            mPageRecyclerView.scrollToPosition(0);
        }
    }
}
