package com.passerbywhu.smallproject.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class SectionAdapter<T extends SectionAdapter.ISection, VH extends RecyclerView.ViewHolder> extends DragSortAdapter<SectionAdapter.ISection, VH> {
    private Class<T> clazz;

    public interface ISection {
        boolean isSection();
        String getSection();
        void setSection(String section);
    }
    public static class Section implements ISection {
        private String section;

        @Override
        public boolean isSection() {
            return true;
        }

        @Override
        public String getSection() {
            return section;
        }

        @Override
        public void setSection(String section) {
            Section.this.section = section;
        }
    }
    protected final int TYPE_SECTION = 1 + super.MAX_TYPE_VALUE;
    protected final int MAX_TYPE_VALUE = TYPE_SECTION;

    public SectionAdapter(Context context, boolean footerViewMatchParent) {
        super(context, footerViewMatchParent);
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        clazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    public SectionAdapter(Context context) {
        this(context, false);
    }

    protected abstract View getSectionHeaderView();

    protected void onSectionChanged(ISection section) {
    }

    protected boolean isSection(int position) {
        if (getItemViewTypeImpl(position) == TYPE_SECTION) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMove(RecyclerView.ViewHolder from, RecyclerView.ViewHolder target) {
        if (from.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        int fromPosition = from.getAdapterPosition() - getHeaderCount();
        int targetPosition = target.getAdapterPosition() - getHeaderCount();
        ISection fromSec = getItem(fromPosition);
        ISection targetSec = getItem(targetPosition);
        if (!TextUtils.equals(fromSec.getSection(), targetSec.getSection())) {
            return false;
        }
        return super.onMove(from, target);
    }

    @Override
    public void bindToRecyclerView(RecyclerView recyclerView) {
        super.bindToRecyclerView(recyclerView);
        ViewParent p = recyclerView.getParent();
        final View stickyView = getSectionHeaderView();
        if (stickyView != null) {
            if (p instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) p;
                int index = parent.indexOfChild(recyclerView);
                parent.removeViewAt(index);
                StickyFrameLayout container = new StickyFrameLayout(mContext);
                container.bindRecyclerView(recyclerView, stickyView);
                stickyView.setVisibility(View.GONE);
                parent.addView(container, index, recyclerView.getLayoutParams());
            }
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            /**
             * 经测试，即使有SectionView在上面遮盖，被遮盖的部分对RecyclerView来说仍然是可见的。
             * @param recyclerView
             * @param dx
             * @param dy
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (stickyView == null) {
                    super.onScrolled(recyclerView, dx, dy);
                    return;
                }
                if (getItemCountImpl() <= 0) {
                    return;
                }
                View firstVisibleView = RecyclerViewUtils.getFirstVisibleItem(recyclerView);
                int firstVisiblePosition = RecyclerViewUtils.findFirstVisiblePosition(recyclerView);
                int firstCompletelyVisiblePosition = RecyclerViewUtils.findFirstCompletelyVisiblePosition(recyclerView);
                View firstCompletelyVisibleView = RecyclerViewUtils.findViewByPosition(recyclerView, firstCompletelyVisiblePosition);

                if (firstVisiblePosition >= getHeaderCount()) {
                    stickyView.setVisibility(View.VISIBLE);
                    SectionAdapter.ISection sec = getItem(firstVisiblePosition - getHeaderCount());
                    if (sec.isSection()) {
                        //如果section的那个View加了topMargin。那么topMargin显示的部分的时候，firstVisibleView就已经是这个section了
                        int top = firstVisibleView.getTop();
                        if (top <= 0) { //这种情况直接盖在最上面就行了
                            stickyView.setY(0);
                            onSectionChanged(sec);
                        } else if (top < stickyView.getHeight()) {
                            stickyView.setY(top - stickyView.getHeight());
                            //TODO 这里应该要修改section标题为前一个组的标题头
                        } else {
                            stickyView.setY(0);
                        }
                    } else if (firstVisiblePosition < getItemCountImpl() + getHeaderCount() - 1) {
                        //如果相等或者没有能够完全看见的item，那么等待下一次滚动事件再进行判断
                        if (firstVisiblePosition == firstCompletelyVisiblePosition || firstCompletelyVisiblePosition == RecyclerView.NO_POSITION) {
                            return;
                        }
                        //如果不相等，那么firstCompletelyVisiblePosition肯定是下一行的。同一行右边高度比左边小的情况不考虑

                        /**  已纠正这个问题
                         *
                         *  这里获取紧接着的下一个item有问题。因为FlexBoxLayoutManager或者GridLayoutManager这种，紧邻着的可能是
                         同一行右边那一个item。但实际上下一行确实是section。从而导致判断错误。
                         所以这里必须拿到下一行的第一个position才行。

                         注意这里第一个item即使被SectionView遮挡住完全不可见了，LayoutManager返回的firstVisiblePosition仍然是这个item。
                         SectionAdapter.ISection nextSec = getItem(firstVisiblePosition + 1 - getHeaderCount());
                         */

                        SectionAdapter.ISection nextSec = getItem(firstCompletelyVisiblePosition - getHeaderCount());

                        if (nextSec.isSection()) {  //第一个不是section，第二个才是section。
                            int bottom = firstVisibleView.getBottom();
                            //这里还有个问题，如果这个view是有bottomMargin的，那么bottom可能已经为0了，margin还有空间。所以Y的位置要加上bottomMargin
                            //所幸在只有margin显示的部分，firstVisibleView仍然是那个bottom已经小于0的那个view。
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) firstVisibleView.getLayoutParams();
                            int firstVisibleViewBottomMarign = params.bottomMargin;
                            //这里还需要考虑nextSectionView是不是有marginTop
                            params = (ViewGroup.MarginLayoutParams) firstCompletelyVisibleView.getLayoutParams();
                            int firstCompletelyVisibleViewTopMargin = params.topMargin;
                            if (bottom + firstVisibleViewBottomMarign + firstCompletelyVisibleViewTopMargin <= stickyView.getHeight()) {
                                stickyView.setY(firstVisibleViewBottomMarign + firstCompletelyVisibleViewTopMargin + bottom - stickyView.getHeight());
                                onSectionChanged(sec);
                            } else {  //有时候滑太快了会导致跳过上面的逻辑，导致sectionView的内容没有改变
                                stickyView.setY(0);
                                onSectionChanged(sec);
                            }
                        } else {
                            stickyView.setY(0);
                        }
                    }
                } else { //headerView可见的时候
                    stickyView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 用于setData，addItems, appendItems，只负责sectionList中间的section插入
     * 不负责头和尾，因为头和尾根据前后的状态不同有不同的处理方式
     * @param sectionList  sectionList中不能包含section
     */
    private void addSectionIntoList(List<ISection> sectionList) {
        if (sectionList == null || sectionList.isEmpty()) {
            return;
        }
        int size = sectionList.size();
        for (int i = size - 1; i > 0; i --) {
            int pre = i - 1;
            String section = sectionList.get(i).getSection();
            String preSection = sectionList.get(pre).getSection();
            if (!TextUtils.equals(section, preSection)) {
                Section sec = new Section();
                sec.section = section;
                sectionList.add(i, sec);
            }
        }
    }

    @Override
    public void setData(List<ISection> list) {
        if (list != null && !list.isEmpty()) {
            for (ISection section : list) {
                if (!clazz.isAssignableFrom(section.getClass())) {
                    throw new IllegalStateException("list should only contain elements of type of T");
                }
            }
            addSectionIntoList(list);
            Section sec = new Section();
            sec.section = list.get(0).getSection();
            if (!TextUtils.isEmpty(list.get(0).getSection())) {
                sec.section = list.get(0).getSection();
                list.add(0, sec);
            }
            onSectionChanged(sec);
        }
        super.setData(list);
    }

    private boolean checkNotContainSectionHeader(List<ISection> items) {
        if (items == null || items.isEmpty()) {
            return true;
        }
        for (ISection element : items) {
            if (element.isSection()) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param position  不包含header的position
     * @param items 不能包含Section实例
     */
    @Override
    public void addItems(int position, List<ISection> items) {
        if (!checkNotContainSectionHeader(items)) {
            throw new IllegalStateException("items should contain section header element");
        }
        addSectionIntoList(items);
        ISection firstItem = items.get(0);
        int pre = position - 1;
        if (pre < 0) { //前面没有元素
            Section sec = new Section();
            if (!TextUtils.isEmpty(firstItem.getSection())) {
                sec.section = firstItem.getSection();
                items.add(0, sec);
            }
        } else {  //前面有元素
            ISection preItem = getItem(pre);
            //如果前面一个元素不是section头，且和第一个元素的section不同，则需要插入一个section头
            //不会出现第一个元素前面紧跟着是另一个section的section头的情况。
            if (!preItem.isSection() && !TextUtils.equals(preItem.getSection(), firstItem.getSection())) {
                Section sec = new Section();
                sec.section = firstItem.getSection();
                items.add(0, sec);
            }
        }
        //如果后面是一个section头且和当前插入的最后一个元素的section相同，就需要删掉那个section头
        ISection lastItem = items.get(items.size() - 1);
        int after = position;
        if (after >= getItemCountImpl()) { //后面是没有元素的
            //不用管
        } else {
            ISection afterItem = items.get(after);
            //如果最后一个元素和后面一个元素的section相同，且后面那个元素是一个section头，那么就需要移除后面那个section头
            if (afterItem.isSection() && TextUtils.equals(afterItem.getSection(), lastItem.getSection())) {
                items.remove(position);
            }
        }
        super.addItems(position, items);
    }

    /**
     * 如果该item是该Section唯一的元素，那么就需要删除对应的Section头
     * 那么这个Section组被删除了，前后的Section组就可能出现合并的情况。这里暂时可以不考虑
     * 注意Section头不允许主动被删除
     * @param position 不包含header的position
     * @return
     */
    @Override
    public ISection removeItem(int position) {
        ISection element = getItem(position);
        if (element.isSection()) {
            throw new IllegalStateException("you can't remove a section header element");
        }
        ISection preEle = null;
        ISection afterEle = null;
        if (position > 0) {  //前面有元素
            preEle = getItem(position - 1);
        }
        if (position < getItemCountImpl() - 1) {
          afterEle = getItem(position + 1);
        }
        if (preEle == null) { //非section头，前面没有元素是不可能出现的情况。
            throw new IllegalStateException("the element before the position which you want to remove should not be absent");
        }
        if (afterEle == null) { //没有后面的元素
            if (preEle.isSection()) {  //需要连同前面一个元素一起删掉
                super.removeItem(position);
                super.removeItem(position - 1);
            }
        } else { //后面有元素，前面也有元素
            if (afterEle.isSection()) {
                if (preEle.isSection()) {  //后面是另一个Section组且前面一个元素是Section头，那就说明position是当前Section组唯一的元素
                    super.removeItem(position);
                    super.removeItem(position - 1);
                }
            } else { //后面不是section头，那就说明position不是当前Section组唯一的元素，直接删掉position就行了。
                super.removeItem(position);
            }
        }
        return null;
    }

    /**
     * 子类覆写此方法，首先调用super.getItemViewTypeImpl,如果返回值为非TYPE_UNDEFINED,那么说明父Adapter已经处理了对应的TYPE。直接返回
     * 如果返回值为TYPE_UNDEFINED，则说明父Adapter没有处理当前的TYPE。那么子类应该增加自己的TYPE处理逻辑。如果只能处理一部分，那么剩余的
     * 返回TYPE_UNDEFINED，由子类再继续去处理。类似双亲委托模型。但是要注意子类定义TYPE的时候不能覆盖父Adapter中定义的TYPE值，因此
     * 这里在Apdater中定义了一个MAX_TYPE_VALUE，每个子类定义新的TYPE时都必须加上这个偏移量。并且子类也需要覆盖这个变量为最新的值。
     * @param position
     * @return
     */
    @Override
    protected int getItemViewTypeImpl(int position) {
        int type = super.getItemViewTypeImpl(position);
        if (type != TYPE_UNDEFINED) {
            return type;
        }
        ISection section = getItem(position);
        if (section.isSection()) {
            return TYPE_SECTION;
        }
        return TYPE_UNDEFINED;
    }

    @Override
    protected VH onCreateViewHolderImpl(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION) {
            return onCreateSectionViewHolder(parent);
        }
        return onCreateItemViewHolder(parent, viewType);
    }

    protected abstract VH onCreateSectionViewHolder(ViewGroup parent);

    protected abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

    @Override
    protected void onBindViewHolderImpl(VH holder, int position) {
        super.onBindViewHolderImpl(holder, position);
        int type = getItemViewTypeImpl(position);
        if (type == TYPE_SECTION) {
            onBindSectionViewHolder(holder, position);
        } else {
            onBindItemViewHolder(holder, position);
        }
    }

    protected void onBindSectionViewHolder(VH holder, int position) {
        onBindSectionViewHolder(holder, getItem(position));
    }

    protected abstract void onBindSectionViewHolder(VH holder, ISection section);

    protected void onBindItemViewHolder(VH holder, int position) {
        onBindItemViewHolder(holder, (T) getItem(position));
    }

    protected abstract void onBindItemViewHolder(VH holder, T t);
}
