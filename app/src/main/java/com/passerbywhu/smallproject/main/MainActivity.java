package com.passerbywhu.smallproject.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.passerbywhu.smallproject.MyApplication;
import com.passerbywhu.smallproject.R;
import com.passerbywhu.smallproject.base.BaseActivity;
import com.passerbywhu.smallproject.main.entity.GiftEntity;
import com.passerbywhu.smallproject.network.Response;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainActivityContract.View<List<GiftEntity>> {
    @Inject
    MainActivityPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerMainActivityComponent.builder().
                applicationComponent(MyApplication.getInstance().getComponent()).
                mainActivityModule(new MainActivityModule(this)).build().inject(this);
        setContentView(R.layout.main_activity);
        mPresenter.refresh(this.<Response<List<GiftEntity>>>bindToLifecycle());
    }

    @Override
    public void refreshComplete(List<GiftEntity> giftEntities) {
        for (GiftEntity entity : giftEntities) {
            Log.d("Small", entity.url);
        }
    }

    @Override
    public void refreshError(Throwable throwable) {

    }
}
