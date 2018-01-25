package com.passerbywhu.smallproject.main;

import com.passerbywhu.smallproject.data.Repository;
import com.passerbywhu.smallproject.main.entity.GiftEntity;
import com.passerbywhu.smallproject.network.Response;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.List;
import javax.inject.Inject;

import io.reactivex.Observable;

public class MainFragmentPresenter implements MainFragmentContract.Presenter {
    private MainFragmentContract.View mView;
    private Repository mRepository;

    @Inject
    public MainFragmentPresenter(MainFragmentContract.View view, Repository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void refresh(LifecycleTransformer transformer) {

    }

    public Observable<Response<List<GiftEntity>>> loadPage(int page, int pageSize, boolean fromCache) {
        return Repository.wrap(fromCache,mRepository.getGifts(page, pageSize));
    }
}
