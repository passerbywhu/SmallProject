package com.passerbywhu.smallproject.dagger;

import android.app.Activity;

import dagger.Module;

@Module
public class ActivityModule {
    private final Activity mActivity;
    public ActivityModule(Activity activity) {
        mActivity = activity;
    }
}
