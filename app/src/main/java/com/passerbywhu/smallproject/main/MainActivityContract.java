package com.passerbywhu.smallproject.main;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by passe on 2017/8/22.
 */

public class MainActivityContract {
    interface View<T> {
        void refreshComplete(T t);
        void refreshError(Throwable throwable);
    }

    interface Presenter<T> {
        void refresh(LifecycleTransformer<T> transformer);
    }
}
