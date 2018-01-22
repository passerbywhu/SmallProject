package com.passerbywhu.smallproject.network;

import com.passerbywhu.smallproject.dagger.ApplicationScope;
import com.passerbywhu.smallproject.dagger.SmallQualifier;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by passe on 2017/5/26.
 */
@Module
public class RetrofitModule {
    public static final String BASE_URL = "";

    @ApplicationScope
    @Provides
    RxJava2CallAdapterFactory provideRxJava2CallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @ApplicationScope
    @Provides
    GsonConverterFactory provideGsonConvertFactory() {
        return GsonConverterFactory.create();
    }

    @SmallQualifier
    @Provides
    @ApplicationScope
    Retrofit provideUpRetrofit(OkHttpClient client, RxJava2CallAdapterFactory adapter, GsonConverterFactory converter) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BASE_URL).client(client).addConverterFactory(converter).addCallAdapterFactory(adapter);
        return builder.build();
    }

    @ApplicationScope
    @Provides
    APIService.SmallService provideUpService(@SmallQualifier Retrofit retrofit) {
        return retrofit.create(APIService.SmallService.class);
    }
}
