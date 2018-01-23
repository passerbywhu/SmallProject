package com.passerbywhu.smallproject.main;

import com.trello.rxlifecycle2.LifecycleTransformer;

public class MainFragmentContract {
    interface View<T> {
        void refreshComplete(T t);
        void refreshError(Throwable throwable);
    }

    interface Presenter<T> {
        void refresh(LifecycleTransformer<T> transformer);
    }
}
