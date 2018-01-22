package com.passerbywhu.smallproject.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.passerbywhu.smallproject.dagger.ActivityModule;
import com.passerbywhu.smallproject.utils.MainThreadPoster;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Created by passe on 2017/5/23.
 */

public class BaseActivity extends RxAppCompatActivity {
    private String HANDLER_TOKEN = getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    @Override
    protected void onDestroy() {
        MainThreadPoster.clear(HANDLER_TOKEN);
        super.onDestroy();
    }

    protected void post(Runnable runnable) {
        MainThreadPoster.postRunnable(runnable, HANDLER_TOKEN);
    }

    protected void postDelay(Runnable runnable, long delay) {
        MainThreadPoster.postRunnableDelay(runnable, HANDLER_TOKEN, delay);
    }

    protected void postAtFixRate(Runnable runnable, long delay, long period) {
        MainThreadPoster.postRunnableAtFixRate(runnable, HANDLER_TOKEN, delay, period);
    }
}
