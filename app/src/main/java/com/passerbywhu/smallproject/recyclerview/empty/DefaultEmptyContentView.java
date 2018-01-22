package com.passerbywhu.smallproject.recyclerview.empty;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by passe on 2017/8/2.
 */

public class DefaultEmptyContentView extends EmptyContentView {
    private TextView mHint;

    public DefaultEmptyContentView(@NonNull Context context) {
        this(context, null);
    }

    public DefaultEmptyContentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultEmptyContentView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHint = new TextView(getContext());
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mHint.setLayoutParams(params);
        mHint.setMovementMethod(new LinkMovementMethod());
    }

    protected void setText(CharSequence text) {
        if (text != null) {
            mHint.setText(text);
        }
    }

    protected void setText(int resID) {
        mHint.setText(resID);
    }

    @Override
    public void hide() {
        setVisibility(View.GONE);
    }

    @Override
    public void show() {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick() {

    }

    @Override
    public void empty() {
    }

    @Override
    public void loading() {
    }
}
