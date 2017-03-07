package com.guyaning.media.mediaplayer01.pager;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.guyaning.media.mediaplayer01.base.BasePager;
import com.guyaning.media.mediaplayer01.utils.LogUtil;

/**
 * Created by Administrator on 2017/2/27.
 * 网络音频界面
 */

public class NetAudioPager extends BasePager {

    private TextView textView;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText("网络音频");
        LogUtil.e("网络音乐数据初始化了");
    }
}
