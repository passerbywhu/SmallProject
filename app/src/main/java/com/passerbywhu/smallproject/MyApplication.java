package com.passerbywhu.smallproject;

import android.app.Application;

public class MyApplication extends Application {
    private static volatile MyApplication instance;
    private ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        mComponent.inject(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }
}
