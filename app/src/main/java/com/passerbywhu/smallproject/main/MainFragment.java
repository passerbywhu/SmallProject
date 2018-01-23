package com.passerbywhu.smallproject.main;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.passerbywhu.smallproject.MyApplication;
import com.passerbywhu.smallproject.R;
import com.passerbywhu.smallproject.base.BaseRecyclerViewFragment;
import com.passerbywhu.smallproject.image.ImageLoader;
import com.passerbywhu.smallproject.main.entity.GiftEntity;
import com.passerbywhu.smallproject.network.Response;
import com.passerbywhu.smallproject.recyclerview.HeaderFooterAdapter;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Function;


public class MainFragment extends BaseRecyclerViewFragment<Response<List<GiftEntity>>, GiftEntity> implements MainFragmentContract.View<Response<List<GiftEntity>>> {
    public static final String TAG = "MainFragment";
    @Inject
    MainFragmentPresenter mPresenter;
    @Inject
    ImageLoader mImageLoader;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    protected boolean autoRefresh() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerMainFragmentComponent.builder().applicationComponent(MyApplication.getInstance().getComponent())
                .mainFragmentModule(new MainFragmentModule(this)).build().inject(this);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected HeaderFooterAdapter<GiftEntity, ?> getAdapter() {
        return new GiftAdapter(getContext());
    }

    @Override
    protected Observable<Response<List<GiftEntity>>> loadPageFromCache(int page, int pageSize) {
        return mPresenter.loadPage(page, pageSize, true);
    }

    @Override
    protected Observable<Response<List<GiftEntity>>> loadPage(int page, int pageSize) {
        return mPresenter.loadPage(page, pageSize, false);
    }

    @Override
    protected Observable<List<GiftEntity>> map(Observable<Response<List<GiftEntity>>> observable) {
        return observable.map(new Function<Response<List<GiftEntity>>, List<GiftEntity>>() {
            @Override
            public List<GiftEntity> apply(Response<List<GiftEntity>> listResponse) throws Exception {
                if (!listResponse.error) {
                    return listResponse.results;
                }
                throw new Exception();
            }
        });
    }

    @Override
    protected void onError(Throwable throwable) {

    }

    private static class GiftAdapter extends HeaderFooterAdapter<GiftEntity, GiftAdapter.ViewHolder> {
        private HashMap<Integer, Point> positionSizeMap = new HashMap<>();

        public GiftAdapter(Context context) {
            super(context);
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img;

            public ViewHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.img);
            }
        }

        @Override
        protected ViewHolder onCreateViewHolderImpl(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.gift_item, parent, false));
        }

        @Override
        protected void onBindViewHolderImpl(final ViewHolder holder, int position) {
            GiftEntity entity = getItem(position);
            ImageLoader.getInstance().loadImage(entity.url, holder.img, R.drawable.img_default_bg, R.drawable.img_default_bg);
        }
    }
}
