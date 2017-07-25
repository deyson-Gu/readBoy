//package com.guyaning.media.mediaplayer01.view;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//
///**
// * Created by Administrator on 2017/5/18.
// */
//
//public class VitamioVideoView extends io.vov.vitamio.widget.VideoView{
//
//    public VitamioVideoView(Context context) {
//        this(context,null);
//    }
//
//    public VitamioVideoView(Context context, AttributeSet attrs) {
//        this(context, attrs,0);
//    }
//
//    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
//    }
//
//    /**
//     * 设置视频的宽和高
//     * @param videoWidth
//     * @param videoHeight
//     */
//    public  void setVideoSize(int videoWidth, int videoHeight){
//        ViewGroup.LayoutParams params = getLayoutParams();
//        params.width = videoWidth;
//        params.height = videoHeight;
//        setLayoutParams(params);
//    }
//
//}
