package com.passerbywhu.smallproject.data.source.local;

import com.passerbywhu.smallproject.data.API;
import com.passerbywhu.smallproject.network.APIService;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import retrofit2.http.Part;

/**
 * Created by passe on 2017/5/25.
 */

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
}
