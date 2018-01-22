package com.passerbywhu.smallproject.data;

import com.passerbywhu.smallproject.main.entity.GiftEntity;
import com.passerbywhu.smallproject.network.Response;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by passe on 2017/5/25.
 */

public interface API {
    Observable<Response<List<GiftEntity>>> getGifts(int page, int pageSize);
}
