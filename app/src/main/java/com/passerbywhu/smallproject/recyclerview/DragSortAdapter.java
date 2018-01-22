package com.passerbywhu.smallproject.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hzwuwenchao on 2015/11/25.
 */
public abstract class DragSortAdapter<T, VH extends RecyclerView.ViewHolder> extends HeaderFooterAdapter<T, VH> {
    private boolean allowMoveUp = false, allowMoveDown = false;
    private boolean allowSwipeStart = false, allowSwipeEnd = false;
    private boolean isLongPressDragEnabled = true;
    private ItemTouchHelper mItemTouchHelper;

    public void enableDrag() {
        allowMoveUp = true;
        allowMoveDown = true;
    }

    public void enableSwipe() {
        allowSwipeStart = true;
        allowSwipeEnd = true;
    }

    protected boolean onMove(RecyclerView.ViewHolder from, RecyclerView.ViewHolder target) {
        moveItem(from.getAdapterPosition() - getHeaderCount(), target.getAdapterPosition() - getHeaderCount());
        return true;
    }

    protected void onSwiped(int position) {
    }

    protected void onMoveStart() {

    }

    protected void onMoveEnd() {

    }

    protected boolean allowMoveUp(RecyclerView.ViewHolder viewHolder) {
        return allowMoveUp;
    }

    protected boolean allowMoveDown(RecyclerView.ViewHolder viewHolder) {
        return allowMoveDown;
    }

    private ItemTouchHelper.Callback mCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return mCallback.makeMovementFlags(
                    (allowMoveUp(viewHolder) ? ItemTouchHelper.UP : 0) |
                            (allowMoveDown(viewHolder) ? ItemTouchHelper.DOWN : 0), (allowSwipeStart ? ItemTouchHelper.START : 0) | (allowSwipeEnd ? ItemTouchHelper.END : 0));
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            if (isHeaderViewAdded() && (viewHolder.getAdapterPosition() < mHeaderViews.size() || target.getAdapterPosition() < mHeaderViews.size())) {
                return false;  //headerView不允许拖拽
            } else if (isFooterViewAdded() && (viewHolder.getAdapterPosition() >= mHeaderViews.size() + getItemCountImpl() || target.getAdapterPosition() >= mHeaderViews.size() + getItemCountImpl())) {
                return false; //footerView不允许拖拽
            } else {
                return DragSortAdapter.this.onMove(viewHolder, target);
            }
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (isHeaderViewAdded() && viewHolder.getAdapterPosition() < mHeaderViews.size()) {
                return;
            } else if (isFooterViewAdded() && viewHolder.getAdapterPosition() > mHeaderViews.size() + getItemCountImpl()) {
                return;
            }
            DragSortAdapter.this.onSwiped(getPositionWithoutHeader(viewHolder.getAdapterPosition()));
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return super.isLongPressDragEnabled();
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return super.isItemViewSwipeEnabled();
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
        }
    };

    public DragSortAdapter(Context context, boolean footerViewMatchParent) {
        super(context, footerViewMatchParent);
    }

    public DragSortAdapter(Context context) {
        super(context);
    }

    public void bindToRecyclerView(RecyclerView recyclerView) {
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(this);
    }

    @Override
    protected void onBindViewHolderImpl(final VH holder, int position) {
        final View view = getDragView(holder);
        if (view != null) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                        if (action == MotionEvent.ACTION_DOWN) {
                            mItemTouchHelper.startDrag(holder);
                            onMoveStart();
                    }
                    return false;
                }
            });
        }
    }

    public View getDragView(VH viewHolder) {
        return null;
    }
}
