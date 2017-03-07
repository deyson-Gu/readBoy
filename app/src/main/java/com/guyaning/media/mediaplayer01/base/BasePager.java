package com.guyaning.media.mediaplayer01.base;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.guyaning.media.mediaplayer01.utils.Utils;

/**
 * Created by Administrator 基类Basepager
 *on 2017/2/27.
 *  VideoPager
 * AudioPager
 * NetVideoPager
 * NetAudioPager
 * 上述的四个基类需要继承自基类
 */

public abstract class BasePager {

    public Context context;

    public View rootView;

    public boolean isInitData;

    public BasePager(Context context) {
        this.context = context;
        rootView = initView();

    }

    /**
     * 强制子类去实现这个方法
     */
    public abstract View initView();

    /**
     * 实现网络请求的方法，根据子类的需求确定书否继承这个方法
     */
    public void initData() {

    }


}
