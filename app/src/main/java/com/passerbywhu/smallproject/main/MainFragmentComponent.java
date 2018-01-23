package com.passerbywhu.smallproject.main;

import com.passerbywhu.smallproject.ApplicationComponent;
import com.passerbywhu.smallproject.dagger.FragmentScope;

import dagger.Component;

@FragmentScope
@Component(dependencies = ApplicationComponent.class, modules = {MainFragmentModule.class})
public interface MainFragmentComponent {
    void inject(MainFragment fragment);
}
