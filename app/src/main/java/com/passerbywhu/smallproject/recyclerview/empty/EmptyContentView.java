package com.passerbywhu.smallproject.recyclerview.empty;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by passe on 2017/8/2.
 */

public abstract class EmptyContentView extends FrameLayout implements EmptyContent {
    public EmptyContentView(@NonNull Context context) {
        super(context);
    }

    public EmptyContentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyContentView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
