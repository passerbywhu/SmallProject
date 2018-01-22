package com.passerbywhu.smallproject.main;

import com.passerbywhu.smallproject.dagger.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {
    MainActivityContract.View mView;

    public MainActivityModule(MainActivityContract.View view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    MainActivityContract.View provideView() {
        return mView;
    }
}
