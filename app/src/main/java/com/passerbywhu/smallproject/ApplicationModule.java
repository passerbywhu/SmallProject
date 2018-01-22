package com.passerbywhu.smallproject;

import android.content.Context;

import com.passerbywhu.smallproject.dagger.ApplicationScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by passe on 2017/5/23.
 */
@Module
public class ApplicationModule {
    private final Context mContext;

    ApplicationModule(Context context) {
        mContext = context;
    }

    @ApplicationScope
    @Provides
    Context provideContext() {
        return mContext;
    }
}
