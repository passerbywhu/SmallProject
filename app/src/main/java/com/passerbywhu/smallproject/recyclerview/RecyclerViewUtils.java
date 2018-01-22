package com.passerbywhu.smallproject.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.google.android.flexbox.FlexboxLayoutManager;

/**
 * Created by passe on 2017/5/26.
 */

public class RecyclerViewUtils {
    /**
     * 判断recyclerView最后一个item是否完全可见，不包括footerView
     * @param recyclerView
     * @return
     */
    public static boolean isLastItemFullVisible(RecyclerView recyclerView) {
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (null == adapter || adapter.getItemCount() == 0) {
            return false;
        }
        int headerViewCount = 0, footerViewCount = 0;
        if (adapter instanceof HeaderFooterAdapter) {
            headerViewCount = ((HeaderFooterAdapter) adapter).getHeaderCount();
            footerViewCount = ((HeaderFooterAdapter) adapter).getFooterCount();
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return true;
        }
        int lastItemPosition = adapter.getItemCount() - 1 - footerViewCount;
        int lastCompletelyVisiblePosition = findLastCompletelyVisiblePosition(recyclerView);
        if (lastItemPosition <= lastCompletelyVisiblePosition) {
            return true;
        }
        return false;
    }

    /**
     * 判断整个RecyclerView的第一个item是不是完全可见的，包括HeaderView，主要用于下拉刷新
     * @param recyclerView
     * @return
     */
    public static boolean isFirstItemFullVisible(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager == null) {
            return true;
        }
        int firstVisiblePosition = findFirstVisiblePosition(recyclerView);
        int firstCompletelyVisiblePosition = findFirstCompletelyVisiblePosition(recyclerView);
        if (firstCompletelyVisiblePosition == firstVisiblePosition && (firstVisiblePosition != RecyclerView.NO_POSITION)) {
            return true;
        }
        return false;
    }

    public static int findLastCompletelyVisiblePosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager lm = (LinearLayoutManager) manager;
            return lm.findLastCompletelyVisibleItemPosition();
        }
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gm = (GridLayoutManager) manager;
            return gm.findLastCompletelyVisibleItemPosition();
        }
        if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sgm = (StaggeredGridLayoutManager) manager;
            return sgm.findLastCompletelyVisibleItemPositions(null)[0];
        }
        if (manager instanceof FlexboxLayoutManager) {
            FlexboxLayoutManager fm = (FlexboxLayoutManager) manager;
            return fm.findLastCompletelyVisibleItemPosition();
        }
        return RecyclerView.NO_POSITION;
    }

    public static View findLastVisibleSpecificView(RecyclerView recyclerView, Class<? extends View> clazz) {
        int firstVisiblePosition = findFirstVisiblePosition(recyclerView);
        int lastVisiblePosition = findLastVisiblePosition(recyclerView);
        for (int i = lastVisiblePosition; i >= firstVisiblePosition; i --) {
            View view = findViewByPosition(recyclerView, i);
            if (clazz.isAssignableFrom(view.getClass())) {
                return view;
            }
        }
        return null;
    }

    public static View findViewByPosition(RecyclerView recyclerView, int position) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        return manager.findViewByPosition(position);
    }

    public static View findLastVisibleItem(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int lastVisiblePosition = findLastVisiblePosition(recyclerView);
        if (lastVisiblePosition != RecyclerView.NO_POSITION) {
            return manager.findViewByPosition(lastVisiblePosition);
        }
        return null;
    }

    public static int findLastVisiblePosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) manager;
            return llm.findLastVisibleItemPosition();
        }
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gm = (GridLayoutManager) manager;
            return gm.findLastVisibleItemPosition();
        }
        if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sgm = (StaggeredGridLayoutManager) manager;
            return sgm.findLastVisibleItemPositions(null)[0];
        }
        if (manager instanceof FlexboxLayoutManager) {
            FlexboxLayoutManager fm = (FlexboxLayoutManager) manager;
            return fm.findLastVisibleItemPosition();
        }
        return RecyclerView.NO_POSITION;
    }

    public static int findFirstCompletelyVisiblePosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager lm = (LinearLayoutManager) manager;
            return lm.findFirstCompletelyVisibleItemPosition();
        }
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gm = (GridLayoutManager) manager;
            return gm.findFirstCompletelyVisibleItemPosition();
        }
        if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sgm = (StaggeredGridLayoutManager) manager;
            return sgm.findFirstCompletelyVisibleItemPositions(null)[0];
        }
        if (manager instanceof FlexboxLayoutManager) {
            FlexboxLayoutManager fm = (FlexboxLayoutManager) manager;
            return fm.findFirstCompletelyVisibleItemPosition();
        }
        return RecyclerView.NO_POSITION;
    }

    public static int findFirstVisiblePosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) manager;
            return llm.findFirstVisibleItemPosition();
        }
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gm = (GridLayoutManager) manager;
            return gm.findFirstVisibleItemPosition();
        }
        if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sgm = (StaggeredGridLayoutManager) manager;
            return sgm.findFirstVisibleItemPositions(null)[0];
        }
        if (manager instanceof FlexboxLayoutManager) {
            FlexboxLayoutManager fm = (FlexboxLayoutManager) manager;
            return fm.findFirstVisibleItemPosition();
        }
        return RecyclerView.NO_POSITION;
    }

    /**
     * 经测试，即使第一个Item被其他View遮挡，还是会算作可见。
     * @param recyclerView
     * @return
     */
    public static View getFirstVisibleItem(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int firstVisiblePosition = findFirstVisiblePosition(recyclerView);
        if (firstVisiblePosition != RecyclerView.NO_POSITION) {
            return manager.findViewByPosition(firstVisiblePosition);
        }
        return null;
    }

    public static void smoothScrollToPosition(RecyclerView recyclerView, int position, int offset) {
        if (recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager() instanceof SmoothScrollLinearLayoutManager) {
            ((SmoothScrollLinearLayoutManager) recyclerView.getLayoutManager()).smoothScrollToPosition(recyclerView, null, position, offset);
        }
    }

    public static void smoothScrollToPosition(RecyclerView recyclerView, int position) {
        smoothScrollToPosition(recyclerView, position, 0);
    }
}
