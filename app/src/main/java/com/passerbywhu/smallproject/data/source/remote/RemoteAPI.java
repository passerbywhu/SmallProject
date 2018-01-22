package com.passerbywhu.smallproject.data.source.remote;


import com.passerbywhu.smallproject.data.API;
import com.passerbywhu.smallproject.network.APIService;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import retrofit2.http.Part;

/**
 * Created by passe on 2017/5/25.
 */
public class RemoteAPI implements API {
    @Inject
    APIService.SmallService mSmallService;

    //查询网络的Cache-Control设置
    //FORCE_NETWORK只是设置了no_cache，no_cache的意思是要先去服务端验证cache是否过期，而并不是阻止缓存
    private static final String CACHE_CONTROL_NETWORK = "no-cache";

    @Inject
    public RemoteAPI() {
    }
}
