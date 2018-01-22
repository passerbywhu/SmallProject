package com.passerbywhu.smallproject.dagger;

import android.app.Activity;

import dagger.Module;

/**
 * Created by passe on 2017/5/25.
 */
@Module
public class ActivityModule {
    private final Activity mActivity;
    public ActivityModule(Activity activity) {
        mActivity = activity;
    }
}
