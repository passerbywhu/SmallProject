package com.passerbywhu.smallproject.data;


import com.passerbywhu.smallproject.data.source.qulifier.Local;
import com.passerbywhu.smallproject.data.source.qulifier.Remote;
import com.passerbywhu.smallproject.main.entity.GiftEntity;
import com.passerbywhu.smallproject.network.Response;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class Repository implements API {
    private final API mRemoteAPI;
    private final API mLocalAPI;
    private static final ThreadLocal<Boolean> fromCache = new ThreadLocal<>();

    private static Observable readFromLocal() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                fromCache.set(true);
                e.onNext("readFromLocal");
                e.onComplete();
            }
        });
    }

    private static Observable readFromRemote() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                fromCache.set(false);
                e.onNext("readFromRemote");
                e.onComplete();
            }
        });
    }

    @Inject
    public Repository(@Local API localAPI, @Remote API remoteAPI) {
        mLocalAPI = localAPI;
        mRemoteAPI = remoteAPI;
    }

    public Observable<API> getAPI() {
        return Observable.create(new ObservableOnSubscribe<API>() {
            @Override
            public void subscribe(ObservableEmitter<API> e) throws Exception {
                if (fromCache.get() != null && fromCache.get()) {
                    e.onNext(mLocalAPI);
                } else {
                    e.onNext(mRemoteAPI);
                }
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<Response<List<GiftEntity>>> getGifts(final int page, final int pageSize) {
        return getAPI().flatMap(new Function<API, Observable<Response<List<GiftEntity>>>>() {
            @Override
            public Observable<Response<List<GiftEntity>>> apply(API api) throws Exception {
                return api.getGifts(page, pageSize);
            }
        });
    }

    public static <T> Observable<T> wrap(boolean fromCache, final Observable<T> observable) {
        Observable<String> fromCacheObservable = Repository.readFromRemote();
        if (fromCache) {
            fromCacheObservable = Repository.readFromLocal();
        }
        return fromCacheObservable.flatMap(new Function<String, ObservableSource<T>>() {
            @Override
            public ObservableSource<T> apply(String s) throws Exception {
                return observable;
            }
        });
    }
}
