package com.passerbywhu.smallproject.base;

/**
 * Created by passe on 2017/8/22.
 */

public interface RefreshInterface<T> {
    void refresh();
    void refreshFromCache();
    void refreshComplete(T t);
    void refreshFromCacheComplete(T t);
    void refreshError(Throwable throwable);
    void refreshFromCacheError(Throwable throwable);
}
