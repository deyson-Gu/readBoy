package com.guyaning.media.mediaplayer01.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.guyaning.media.mediaplayer01.R;

/**
 * Created by Administrator on 2017/3/1.
 * 自定义的页面顶部功能栏
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {

    private View tvSearch;

    private View rlGame;

    private View tvHistory;

    private Context context;

    /**
     * 在代码中实例化的时候调用这个方法
     *
     * @param context
     */
    public TitleBar(Context context) {
        this(context, null);
    }


    /**
     * 在布局文件使用该类的时候，android系统调用的是这个方法
     *
     * @param context
     * @param attrs
     */
    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 给布局文件添加样式的时候，使用这个方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }


    /**
     * 布局文件实例化完成之后回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvSearch = getChildAt(1);
        rlGame = getChildAt(2);
        tvHistory = getChildAt(3);

        tvSearch.setOnClickListener(this);
        rlGame.setOnClickListener(this);
        tvHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_serach:
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_history:
                Toast.makeText(context, "记录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
