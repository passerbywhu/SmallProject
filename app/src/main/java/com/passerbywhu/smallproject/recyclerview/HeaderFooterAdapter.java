package com.passerbywhu.smallproject.recyclerview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.passerbywhu.smallproject.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 支持Footer和Header的RecyclerView Adapter
 * 暂时仅支持一个Header和一个Footer
 * 这个Adapter为其子Adapter封装了Header和Footer的细节。子Adapter只需要按照普通Adapter的操作即可。
 *
 * 支持多Header和多Footer
 */
public abstract class HeaderFooterAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<T> mRealData;
    protected List<View> mHeaderViews = new ArrayList<>();
    protected List<View> mFooterViews = new ArrayList<>();
    private static final int TYPE_HEADER = 1000;
    private static final int TYPE_FOOTER = 1001;
    protected static final int TYPE_UNDEFINED = 1002;
    protected final int MAX_TYPE_VALUE = TYPE_UNDEFINED;
    protected Context mContext;
    private int mDuration = 300;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = -1;
    private boolean mIsFirstOnly = true;
    private static final float DEFAULT_SCALE_FROM = .5f;
    private final float mFrom = DEFAULT_SCALE_FROM;
    private boolean mNeedAnimate = false;
    private boolean mIsFooterViewMatchParent = false;
    protected RecyclerView mRecyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View itemView);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position, View itemView);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public HeaderFooterAdapter(Context context, boolean isFooterMatchParent) {
        mRealData = new ArrayList<>();
        this.mContext = context;
        mIsFooterViewMatchParent = isFooterMatchParent;
    }

    public HeaderFooterAdapter(Context context) {
        this(context, false);
    }

    public List<T> getData() {
        return Collections.unmodifiableList(mRealData);
    }

    /**
     * 只允许remove非Header和非Footer的item
     * 此函数仅供子Adapter调用，不用于此Adapter本身调用。
     * @param position 不包含header的position
     * @return
     */
    public T removeItem(int position) {
//        T result;
//        result = mRealData.remove(position);
//        notifyItemRemoved(getPositionWithHeader(position));
//        return result;
        List<T> result = removeItems(position, position + 1);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public List<T> removeItems(int startPosition, int endPosition) {
        List<T> result = new ArrayList<>();
        startPosition = Math.max(0, startPosition);
        endPosition = Math.min(endPosition, getItemCountImpl());
        for (int i = endPosition - 1; i >= startPosition; i --) {
            result.add(mRealData.remove(i));
        }
        notifyItemRangeRemoved(getPositionWithHeader(startPosition), endPosition - startPosition);
        return result;
    }

    /**
     * 仅供外部调用
     * @param position  不包含header的position
     * @param t
     */
    public void addItem(int position, T t) {
        List<T> list = new ArrayList<>();
        list.add(t);
        addItems(position, list);
    }

    public void addItem(T t) {
        addItem(getItemCountImpl(), t);
    }

    /**
     * 仅供外部使用
     * @param fromPosition
     * @param toPosition
     */
    public void moveItem(int fromPosition, int toPosition) {
        T entity = mRealData.remove(fromPosition);
        int position = toPosition > fromPosition ? toPosition : toPosition;
        mRealData.add(position, entity);
        notifyItemMoved(getPositionWithHeader(fromPosition), getPositionWithHeader(toPosition));
    }

    /**
     * 仅供外部调用
     * @param position  不包含header的position
     * @param items
     */
    public void addItems(int position, List<T> items) {
        mRealData.addAll(position, items);
        notifyItemRangeInserted(getPositionWithHeader(position), items.size());
    }

    /**
     * 仅供外部使用
     * @param items
     */
    public void appendItems(List<T> items) {
        addItems(getItemCountImpl(), items);
    }
    /**
     *供外部使用接口
     * @param position  外部传进来的不带Header的position
     * @return
     */
    public boolean isLastItem(int position) {
        if (position == getItemCountImpl() - 1) {
            return true;
        }
        return false;
    }
    /**
     * 仅供外部使用
     * @param position
     */
    public void onNotifyItemChanged(int position) {
        notifyItemChanged(getPositionWithHeader(position));
    }

    public void onNotifyItemChanged(int position, T object) {
        notifyItemChanged(getPositionWithHeader(position), object);
    }
    public void clear() {
        mRealData.clear();
        notifyDataSetChanged();
    }

    public void setData(List<T> list) {
        if (list == null) {
            return;
        }
        mRealData.clear();
        mRealData.addAll(list);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        if (position < 0 || position >= getItemCountImpl()) {
            return null;
        }
        return mRealData.get(position);
    }

    public int getItemRealPosition(T value) {
        if (mRealData != null && value != null) {
            int length = mRealData.size();
            for (int i = 0; i < length; i++) {
                if (value.equals(mRealData.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     *
     * @param position  recyclerView的position，是包括header的position
     * @return  不包括header的position。也就是用户真正想要的position，减去了header的
     */
    protected int getPositionWithoutHeader(int position) {
        return position - mHeaderViews.size();
    }

    /**
     *
     * @param position  是子Adapter传进来的，也即已经减去了header的position
     * @return 加上了header的position
     */
    private int getPositionWithHeader(int position) {
        return position + mHeaderViews.size();
    }

    public void addHeaderView(View view) {
        if (view == null) {
            return;
        }
        mHeaderViews.add(0, view);
    }

    public void addFooterView(View view) {
        if (view == null) {
            return;
        }
        mFooterViews.add(view);
    }

    public boolean isHeaderViewAdded() {
        return mHeaderViews.isEmpty() ? false : true;
    }

    public boolean isFooterViewAdded() {
        return mFooterViews.isEmpty() ? false : true;
    }

    @Deprecated
    @Override
    /**
     * Please use getItemCountImpl instead
     */
    public int getItemCount() {
        return mRealData.size() + mHeaderViews.size() + mFooterViews.size();
    }

    public int getItemCountImpl() {
        return mRealData.size();
    }

    /**
     *
     * @param position  不带Header的position
     * @return
     */
    public int getSpanCount(int position) {
        return 1;
    }

    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    public int getFooterCount() {
        return mFooterViews.size();
    }

    @Deprecated
    @Override
    /**
     * Please use onCreateViewHolderImpl instead
     */
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER || viewType == TYPE_FOOTER) {
            View view;
            if (mIsFooterViewMatchParent) {
                view = LayoutInflater.from(mContext).inflate(R.layout.header_footer_view_container_flex, parent, false);
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.header_footer_view_container, parent, false);
            }
            return new ViewHolder(view);
        } else {
            return onCreateViewHolderImpl(parent ,viewType);
        }
    }

    @Deprecated
    @Override
    /**
     * Please use onBindViewHolderImpl instead
     */
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position < mHeaderViews.size()) {
            FrameLayout emptyContainer = (FrameLayout) holder.itemView;
            emptyContainer.removeAllViews();
            View realView = mHeaderViews.get(position);
            ViewParent parent = realView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(realView);
            }
            emptyContainer.addView(realView);
            return;
        } else if (position >= mHeaderViews.size() + mRealData.size()) {
            FrameLayout emptyContainer = (FrameLayout) holder.itemView;
            emptyContainer.removeAllViews();
            View realView = mFooterViews.get(position - (mHeaderViews.size() + mRealData.size()));
            ViewParent parent = realView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(realView);
            }
            emptyContainer.addView(realView);
            return;
        } else {
            onBindViewHolderImpl((VH) holder, getPositionWithoutHeader(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick((VH) holder, getPositionWithoutHeader(holder.getAdapterPosition()));
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(getPositionWithoutHeader(position), v);
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    boolean proceeded = onItemLongClick((VH) holder, getPositionWithoutHeader(holder.getAdapterPosition()));
                    if (mOnItemLongClickListener != null) {
                        proceeded = proceeded || mOnItemLongClickListener.onItemLongClick(getPositionWithoutHeader(holder.getAdapterPosition()), v);
                    }
                    return proceeded;
                }
            });
        }

        if (mNeedAnimate) {
            if (!mIsFirstOnly || position > mLastPosition) {
                for (Animator anim : getAnimators(holder.itemView)) {
                    anim.setDuration(mDuration).start();
                    anim.setInterpolator(mInterpolator);
                }
                mLastPosition = position;
            } else {
                clear(holder.itemView);
            }
        }
    }


    protected abstract VH onCreateViewHolderImpl(ViewGroup parent, int viewType);
    protected abstract void onBindViewHolderImpl(VH holder, int position);

    public static void clear(View v) {
        ViewCompat.setAlpha(v, 1);
        ViewCompat.setScaleY(v, 1);
        ViewCompat.setScaleX(v, 1);
        ViewCompat.setTranslationY(v, 0);
        ViewCompat.setTranslationX(v, 0);
        ViewCompat.setRotation(v, 0);
        ViewCompat.setRotationY(v, 0);
        ViewCompat.setRotationX(v, 0);
        ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
        ViewCompat.animate(v).setInterpolator(null).setStartDelay(0);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setStartPosition(int start) {
        mLastPosition = start;
    }

    protected Animator[] getAnimators(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", mFrom, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", mFrom, 1f);
        return new ObjectAnimator[] { scaleX, scaleY };
    }
    public void setFirstOnly(boolean firstOnly) {
        mIsFirstOnly = firstOnly;
    }

    protected void onItemClick(VH holder, final int position) {
    }

    protected boolean onItemLongClick(VH holder, final int position) {
        return false;
    }

    @Deprecated
    @Override
    /**
     * Please use getItemViewTypeImpl instead
     */
    public int getItemViewType(int position) {
        if (position < mHeaderViews.size()) {
            return TYPE_HEADER;
        }
        if (position >= mHeaderViews.size() + mRealData.size()) {
            return TYPE_FOOTER;
        }
        return getItemViewTypeImpl(getPositionWithoutHeader(position));
    }

    protected int getItemViewTypeImpl(int position) {
        return TYPE_UNDEFINED;
    }

    @Deprecated
    @Override
    /**
     * Please use getItemIdImpl instead
     */
    public long getItemId(int position) {
        if (position < mHeaderViews.size()) {
            return position;
        }
        if (position >= mHeaderViews.size() + mRealData.size()) {
            return position;
        }
        return getItemIdImpl(getPositionWithoutHeader(position));
    }

    public long getItemIdImpl(int position) {
        return position;
    }
}
