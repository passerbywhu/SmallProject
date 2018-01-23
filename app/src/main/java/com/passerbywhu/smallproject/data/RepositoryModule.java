package com.passerbywhu.smallproject.data;

import com.passerbywhu.smallproject.dagger.ApplicationScope;
import com.passerbywhu.smallproject.data.source.local.LocalAPI;
import com.passerbywhu.smallproject.data.source.qulifier.Local;
import com.passerbywhu.smallproject.data.source.qulifier.Remote;
import com.passerbywhu.smallproject.data.source.remote.RemoteAPI;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {
    @ApplicationScope
    @Remote
    @Provides
    API provideRemoteAPI(RemoteAPI remoteAPI) {
        return remoteAPI;
    }

    @ApplicationScope
    @Local
    @Provides
    API provideLocalAPI(LocalAPI localAPI) {
        return localAPI;
    }
}
