package com.passerbywhu.smallproject.data.source.local;

import com.passerbywhu.smallproject.data.API;
import com.passerbywhu.smallproject.main.entity.GiftEntity;
import com.passerbywhu.smallproject.network.APIService;
import com.passerbywhu.smallproject.network.Response;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class LocalAPI implements API {
    @Inject
    APIService.SmallService mSmallService;

    //缓存有效期为两个星期
    private static final long CACHE_STALE_SEC = Integer.MAX_VALUE;
    //查询缓存的Cache-Control设置
    public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;

    @Inject
    public LocalAPI() {
    }

    @Override
    public Observable<Response<List<GiftEntity>>> getGifts(int page, int pageSize) {
        return mSmallService.getGifts(CACHE_CONTROL_CACHE, page, pageSize);
    }
}
