package com.guyaning.media.mediaplayer01.utils;

import android.content.Context;

/**
 */
public class DensityUtil {
    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**获取屏幕的宽度（像素）*/
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;//1080
    }

    /**获取屏幕的宽度（dp）*/
    public static int getScreenWidthDp(Context context) {
        float scale = getScreenDendity(context);
        return (int)(context.getResources().getDisplayMetrics().widthPixels / scale);//360
    }

    /**获取屏幕的高度（像素）*/
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;//1776
    }

    /**获取屏幕的高度（像素）*/
    public static int getScreenHeightDp(Context context) {
        float scale = getScreenDendity(context);
        return (int)(context.getResources().getDisplayMetrics().heightPixels / scale);//592
    }

    /**屏幕密度比例*/
    public static float getScreenDendity(Context context){
        return context.getResources().getDisplayMetrics().density;//3
    }

}
