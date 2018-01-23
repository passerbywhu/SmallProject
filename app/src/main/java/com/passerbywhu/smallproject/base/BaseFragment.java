package com.passerbywhu.smallproject.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.passerbywhu.smallproject.utils.MainThreadPoster;
import com.trello.rxlifecycle2.components.support.RxFragment;

public class BaseFragment extends RxFragment {
    protected boolean mIsVisible = false;
    protected boolean mIsViewCreated = false;
    private String HANDLER_TOKEN = getClass().getName();
    private View mRetainedView;

    protected boolean retainViewWhenDestroy() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsViewCreated = true;
        if (mIsVisible) {
            onContentVisible();
        }
        if (retainViewWhenDestroy()) {
            mRetainedView = view;
        }
    }

    @Deprecated
    @Nullable
    @Override
    /**
     * 子类不应该覆写此方法，请覆写onCreateViewImpl
     */
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRetainedView != null) {
            return mRetainedView;
        } else {
            return onCreateViewImpl(inflater, container, savedInstanceState);
        }
    }

    public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onDestroyView() {
        MainThreadPoster.clear(HANDLER_TOKEN);
        mIsViewCreated = false;
        if (mRetainedView != null) {
            ViewParent parent = mRetainedView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mRetainedView);
            }
        }
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mIsVisible = true;
            if (mIsViewCreated) {
                onContentVisible();
            }
        } else {
            mIsVisible = false;
            if (mIsViewCreated) {
                onContentInvisible();
            }
        }
    }

    protected void post(Runnable runnable) {
        MainThreadPoster.postRunnable(runnable, HANDLER_TOKEN);
    }

    protected void postDelay(Runnable runnable, long delay) {
        MainThreadPoster.postRunnableDelay(runnable, HANDLER_TOKEN, delay);
    }

    protected void onContentVisible() {
    }
    protected void onContentInvisible() {
    }

    protected void clearRunnable(){
        MainThreadPoster.clear(HANDLER_TOKEN);
    }
}
