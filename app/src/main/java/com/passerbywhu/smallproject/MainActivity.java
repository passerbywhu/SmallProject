package com.passerbywhu.smallproject;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.passerbywhu.smallproject.base.BaseActivity;

/**
 * Created by hzwuwenchao on 2018/1/22.
 */

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }
}
