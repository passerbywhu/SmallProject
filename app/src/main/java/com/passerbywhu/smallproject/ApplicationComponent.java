package com.passerbywhu.smallproject;

import com.passerbywhu.smallproject.dagger.ApplicationScope;
import com.passerbywhu.smallproject.data.Repository;
import com.passerbywhu.smallproject.data.RepositoryModule;
import com.passerbywhu.smallproject.image.ImageLoader;
import com.passerbywhu.smallproject.image.ImageLoaderModule;
import com.passerbywhu.smallproject.network.OKHttpModule;
import com.passerbywhu.smallproject.network.RetrofitModule;

import dagger.Component;

/**
 * Created by passe on 2017/5/23.
 */
@ApplicationScope
@Component(modules = {ApplicationModule.class, RetrofitModule.class,
        OKHttpModule.class, RepositoryModule.class, ImageLoaderModule.class})
public interface ApplicationComponent {
    Repository repository();
    ImageLoader imageLoader();
    void inject(MyApplication myApplication);
}
