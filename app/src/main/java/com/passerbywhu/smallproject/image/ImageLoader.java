package com.passerbywhu.smallproject.image;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.passerbywhu.smallproject.MyApplication;
import com.passerbywhu.smallproject.dagger.ApplicationScope;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import javax.inject.Inject;

/**
 * Created by hzwuwenchao on 16/1/14.
 */
@ApplicationScope
public class ImageLoader {
    private static volatile ImageLoader instance;

    @Inject
    public ImageLoader() {
        instance = this;
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            new ImageLoader();
        }
        return instance;
    }

    @Inject
    Picasso picasso;

    public void loadImage(String url, ImageView imageView, boolean fit, boolean centerCrop, Transformation transformation, Callback callback, Drawable placeholderDrawable, Drawable errorDrawable) {
        if(TextUtils.isEmpty(url)) {
            imageView.setImageDrawable(errorDrawable);
            return;
        }
        RequestCreator requestCreator = picasso.load(url);
        if (fit) {
            requestCreator.fit();
        }

        if (centerCrop) {
            requestCreator.centerCrop();
        }
        if (transformation != null) {
            requestCreator.transform(transformation);
        }
        if (placeholderDrawable != null) {
            requestCreator.placeholder(placeholderDrawable);
        }
        if (errorDrawable != null) {
            requestCreator.error(errorDrawable);
        }
        if (callback != null) {
            requestCreator.into(imageView, callback);
        } else {
            requestCreator.into(imageView);
        }
    }

    public void loadImage(String url, Target target, int drawable) {
        RequestCreator requestCreator = picasso.load(url);
        requestCreator.error(drawable);
        requestCreator.placeholder(drawable);
        requestCreator.fit();
        requestCreator.into(target);
    }

    public void loadImage(String url, ImageView imageView) {
        loadImage(url, imageView, false, false, null, null ,null, null);
    }

    public void loadImage(String url, ImageView imageView, boolean fit, boolean crop) {
        loadImage(url, imageView, fit, crop, null, null ,null, null);
    }

    public void loadImage(String url, ImageView imageView, boolean fit, boolean crop, int placeHolderDrawableId, int errorDrawableId) {
        loadImage(url, imageView, fit, crop, null, null, placeHolderDrawableId <= 0 ? null : MyApplication.getInstance().getResources().getDrawable(placeHolderDrawableId), errorDrawableId <= 0 ? null : MyApplication.getInstance().getResources().getDrawable(errorDrawableId));
    }

    public void loadImage(String url, ImageView imageView, Drawable placeHolderDrawable, Drawable errorDrawable) {
        loadImage(url, imageView, false, false, null, null, placeHolderDrawable, errorDrawable);
    }

    public void loadImage(String url, ImageView imageView, int placeHolderDrawableId, int errorDrawableId) {
        loadImage(url, imageView, placeHolderDrawableId <= 0 ? null : MyApplication.getInstance().getResources().getDrawable(placeHolderDrawableId), errorDrawableId <= 0 ? null : MyApplication.getInstance().getResources().getDrawable(errorDrawableId));
    }

    public void loadImage(String url, ImageView imageView, Drawable drawable, Transformation transformation, Callback callback) {
        loadImage(url, imageView, true, true, transformation, callback, drawable, drawable);
    }

    public void loadImage(String url, ImageView imageView, Drawable drawable) {
        loadImage(url, imageView, drawable, drawable);
    }

    public void loadImage(String url, ImageView imageView, int drawableId) {
        loadImage(url, imageView, MyApplication.getInstance().getResources().getDrawable(drawableId));
    }

    public void loadImage(String url, ImageView imageView, int drawableId, Transformation transformation, Callback callback) {
        loadImage(url, imageView, MyApplication.getInstance().getResources().getDrawable(drawableId), transformation, callback);
    }

    public void loadImage(String url, ImageView imageView, int drawableId, Transformation transformation){
        loadImage(url, imageView, drawableId, transformation, null);
    }

    public void loadImage(String url, ImageView imageView, int drawableId, Callback callback){
        loadImage(url, imageView, drawableId, null, callback);
    }

    public void loadImage(String url, ImageView imageView, Callback callback){
        loadImage(url, imageView, null, null, callback);
    }

    public void clearCache(String url){
        picasso.invalidate(url);
    }
}
