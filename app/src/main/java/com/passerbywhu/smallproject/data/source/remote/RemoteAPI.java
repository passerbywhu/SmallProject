package com.passerbywhu.smallproject.data.source.remote;


import com.passerbywhu.smallproject.data.API;
import com.passerbywhu.smallproject.main.entity.GiftEntity;
import com.passerbywhu.smallproject.network.APIService;
import com.passerbywhu.smallproject.network.Response;

import java.util.List;
import javax.inject.Inject;

import io.reactivex.Observable;

public class RemoteAPI implements API {
    @Inject
    APIService.SmallService mSmallService;

    //查询网络的Cache-Control设置
    //FORCE_NETWORK只是设置了no_cache，no_cache的意思是要先去服务端验证cache是否过期，而并不是阻止缓存
    private static final String CACHE_CONTROL_NETWORK = "no-cache";

    @Inject
    public RemoteAPI() {
    }

    @Override
    public Observable<Response<List<GiftEntity>>> getGifts(int page, int pageSize) {
        return mSmallService.getGifts(CACHE_CONTROL_NETWORK, page, pageSize);
    }
}
