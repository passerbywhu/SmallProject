package com.passerbywhu.smallproject.data;


import com.passerbywhu.smallproject.data.source.qulifier.Local;
import com.passerbywhu.smallproject.data.source.qulifier.Remote;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.MultipartBody;
import retrofit2.http.Part;

/**
 * Created by passe on 2017/5/25.
 */
public class Repository implements API {
    private final API mRemoteAPI;
    private final API mLocalAPI;
    private static int turn = 0;
    private static final ThreadLocal<Boolean> fromCache = new ThreadLocal<>();

    public static Observable readFromLocal() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                fromCache.set(true);
                e.onNext("readFromLocal");
                e.onComplete();
            }
        });
    }

    public static Observable readFromRemote() {
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

//    @Override
//    public Observable<Response<CommonEntity>> getConfigCommon() {
//        return getAPI().flatMap(new Function<API, ObservableSource<Response<CommonEntity>>>() {
//            @Override
//            public ObservableSource<Response<CommonEntity>> apply(API api) throws Exception {
//                return api.getConfigCommon();
//            }
//        });
//    }
}
