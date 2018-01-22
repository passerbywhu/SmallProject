package com.passerbywhu.smallproject.recyclerview.footer;

/**
 * Created by passe on 2017/8/1.
 */

public interface FooterLoading {
    //准备加载
    void onPreLoading();
    //正在加载
    void onLoading();
    //加载完成
    void onLoaded();
    //没有更多数据了
    void onComplete();
    //重置
    void onReset();
}
