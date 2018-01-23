package com.passerbywhu.smallproject.image;

import android.content.Context;

import com.passerbywhu.smallproject.dagger.ApplicationScope;
import com.passerbywhu.smallproject.network.PicassoQualifiler;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class ImageLoaderModule {
    @ApplicationScope
    @Provides
    Picasso providePicasso(@PicassoQualifiler OkHttpClient okHttpClient, Context context) {
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OKHttp3Downloader(okHttpClient));
        return builder.build();
    }
}
