package com.passerbywhu.smallproject.network;

import android.os.StatFs;

import com.passerbywhu.smallproject.MyApplication;
import com.passerbywhu.smallproject.dagger.ApplicationScope;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by passe on 2017/9/7.
 */

@Module
public class OKHttpModule {
    private static final int CACHE_SIZE = 100 * 1024 * 1024;
    private static final int MIN_DISK_CACHE_SIZE = 100 * 1024 * 1024;
    private static final int MAX_DISK_CACHE_SIZE = 500 * 1024 * 1024;

    private static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }

    @ApplicationScope
    @Provides
    OkHttpClient.Builder provideOkHttpClientBuilder() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS).
                writeTimeout(30, TimeUnit.SECONDS).
                readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).
                addInterceptor(loggingInterceptor).
                cookieJar(MyCookieJar.getInstance());
        return builder;
    }

    @ApplicationScope
    @Provides
    OkHttpClient provideOkHttpClient(OkHttpClient.Builder builder) {
        Cache cache = null;
        File httpCachePath = new File(MyApplication.getInstance().getCacheDir(), "httpcache");
        cache = new Cache(httpCachePath, CACHE_SIZE);
        if (cache != null) {
            builder.cache(cache);
        }
        return builder.build();
    }

    @PicassoQualifiler
    @ApplicationScope
    @Provides
    OkHttpClient providePicassoOkHttpClient(OkHttpClient.Builder builder) {
        builder.dispatcher(new Dispatcher());
        Cache cache = null;
        File picassoCachePath = new File(MyApplication.getInstance().getCacheDir(), "picasso");
        cache = new Cache(picassoCachePath, calculateDiskCacheSize(picassoCachePath));
        if (cache != null) {
            builder.cache(cache);
        }
        return builder.build();
    }
}
