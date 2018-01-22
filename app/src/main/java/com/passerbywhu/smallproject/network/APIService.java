package com.passerbywhu.smallproject.network;

import com.passerbywhu.smallproject.main.entity.GiftEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class APIService {
    public static final String CACHE_CONTROL = "Cache-Control";

    public interface SmallService {
        @GET("/api/data/福利/{pageSize}/{page}")
        Observable<Response<List<GiftEntity>>> getGifts(@Header(CACHE_CONTROL) String cacheControl, @Path("page") int page, @Path("pageSize") int pageSize);
    }
}
