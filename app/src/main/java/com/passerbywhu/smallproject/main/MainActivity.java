package com.passerbywhu.smallproject.main;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.passerbywhu.smallproject.R;
import com.passerbywhu.smallproject.base.BaseActivity;

public class MainActivity extends BaseActivity {
    private MainFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState != null) {
            fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        }
        if (fragment == null) {
            fragment = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, MainFragment.TAG).commit();
        }
    }
}
