package com.passerbywhu.smallproject.base;

public interface RefreshInterface<T> {
    void refresh();
    void refreshFromCache();
    void refreshComplete(T t);
    void refreshFromCacheComplete(T t);
    void refreshError(Throwable throwable);
    void refreshFromCacheError(Throwable throwable);
}
