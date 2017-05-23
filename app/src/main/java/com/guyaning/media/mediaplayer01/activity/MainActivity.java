package com.guyaning.media.mediaplayer01.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.base.BasePager;
import com.guyaning.media.mediaplayer01.fragment.ReplaceFragment;
import com.guyaning.media.mediaplayer01.pager.AudioPager;
import com.guyaning.media.mediaplayer01.pager.NetAudioPager;
import com.guyaning.media.mediaplayer01.pager.NetVideoPager;
import com.guyaning.media.mediaplayer01.pager.VideoPager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private FrameLayout flContent;
    private RadioGroup rgBottomTag;
    private RadioButton rbVideo;
    private ArrayList<BasePager> basePagers;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViews();

        initListener();

        rgBottomTag.check(R.id.rb_videos);

    }

    /**
     * 初始化控件和布局
     */
    private void findViews() {
        flContent = (FrameLayout) findViewById(R.id.fl_contents);
        rgBottomTag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
        rbVideo = (RadioButton) findViewById(R.id.rb_videos);

        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new NetAudioPager(this));
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        rgBottomTag.setOnCheckedChangeListener(new MyCheckedChangedListener());
    }

    class MyCheckedChangedListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_videos:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_netvideo:
                    position = 2;
                    break;
                case R.id.rb_netaudio:
                    position = 3;
                    break;
                default:
                    position = 0;
                    break;
            }
            setFragment();
        }

    }


    private void setFragment() {

        FragmentManager fg = getSupportFragmentManager();

        FragmentTransaction ft = fg.beginTransaction();

        ft.replace(R.id.fl_contents, new ReplaceFragment(getBasePager()));

        ft.commit();
    }

    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if (basePager != null && !basePager.isInitData) {
            basePager.initData();  //初始化数据
            basePager.isInitData = true;
        }
        return basePager;
    }

}

