package com.passerbywhu.smallproject.main;

import com.passerbywhu.smallproject.data.Repository;
import com.passerbywhu.smallproject.main.entity.GiftEntity;
import com.passerbywhu.smallproject.network.Response;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivityPresenter implements MainActivityContract.Presenter<Response<List<GiftEntity>>> {
    MainActivityContract.View mView;
    Repository mRepository;

    @Inject
    public MainActivityPresenter(MainActivityContract.View view, Repository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void refresh(LifecycleTransformer<Response<List<GiftEntity>>> transformer) {
        mRepository.getGifts(1, 10).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose(transformer).
                subscribe(new Consumer<Response<List<GiftEntity>>>() {
                    @Override
                    public void accept(Response<List<GiftEntity>> listResponse) throws Exception {
                        if (!listResponse.error) {
                            List<GiftEntity> gifts = listResponse.results;
                            mView.refreshComplete(gifts);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.refreshError(throwable);
                    }
                });
    }
}
