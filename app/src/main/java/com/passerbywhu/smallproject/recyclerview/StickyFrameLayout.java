package com.passerbywhu.smallproject.recyclerview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class StickyFrameLayout extends FrameLayout {
    private RecyclerView mRecyclerView;
    private View mSectionView;

    public StickyFrameLayout(@NonNull Context context) {
        super(context);
    }

    public StickyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindRecyclerView(RecyclerView recyclerView, final View sectionView) {
        mRecyclerView = recyclerView;
        if (mRecyclerView.getAdapter() == null || !(mRecyclerView.getAdapter() instanceof SectionAdapter)) {
            throw new IllegalStateException("StickyFrameLayout only works with SectionAdapter");
        }
        mSectionView = sectionView;
        addView(mRecyclerView);
        mSectionView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(sectionView);
    }
}
