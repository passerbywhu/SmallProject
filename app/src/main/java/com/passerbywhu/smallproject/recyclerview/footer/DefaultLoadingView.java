package com.passerbywhu.smallproject.recyclerview.footer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.passerbywhu.smallproject.R;

public class DefaultLoadingView extends FooterLoadingView {
    private ContentLoadingProgressBar mProgressBar;
    private TextView mLoadText;


    public DefaultLoadingView(Context context) {
        this(context, null);
    }

    public DefaultLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.load_more, this, true);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);
        mLoadText = (TextView) findViewById(R.id.loadtext);
    }

    @Override
    public void onPreLoading() {

    }

    @Override
    public void onLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLoadText.setVisibility(View.GONE);
    }

    @Override
    public void onLoaded() {
        mProgressBar.setVisibility(View.GONE);
        mLoadText.setVisibility(View.GONE);
    }

    @Override
    public void onComplete() {
        mProgressBar.setVisibility(View.GONE);
        mLoadText.setVisibility(View.VISIBLE);
        mLoadText.setText(R.string.no_more_data);
    }

    @Override
    public void onReset() {
        mProgressBar.setVisibility(View.GONE);
        mLoadText.setVisibility(View.GONE);
    }
}
