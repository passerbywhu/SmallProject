package com.passerbywhu.smallproject.main;

import com.passerbywhu.smallproject.ApplicationComponent;
import com.passerbywhu.smallproject.dagger.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(dependencies = {ApplicationComponent.class}, modules = {MainActivityModule.class})
public interface MainActivityComponent {
    void inject(MainActivity activity);
}
