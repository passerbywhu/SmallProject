package com.passerbywhu.smallproject.main;

import com.passerbywhu.smallproject.dagger.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class MainFragmentModule {
    private MainFragmentContract.View mView;

    public MainFragmentModule(MainFragmentContract.View view) {
        mView = view;
    }

    @FragmentScope
    @Provides
    MainFragmentContract.View provideView() {
        return mView;
    }
}
