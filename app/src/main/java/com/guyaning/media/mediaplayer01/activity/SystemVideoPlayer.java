package com.guyaning.media.mediaplayer01.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.bean.MediaItem;
import com.guyaning.media.mediaplayer01.utils.Utils;
import com.guyaning.media.mediaplayer01.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/22.
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    @BindView(R.id.videoview)
    VideoView videoview;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_battery)
    ImageView ivBattery;
    @BindView(R.id.tv_system_time)
    TextView tvSystemTime;
    @BindView(R.id.btn_voice)
    Button btnVoice;
    @BindView(R.id.seekbar_voice)
    SeekBar seekbarVoice;
    @BindView(R.id.btn_swich_player)
    Button btnSwichPlayer;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.seekbar_video)
    SeekBar seekbarVideo;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.btn_video_pre)
    Button btnVideoPre;
    @BindView(R.id.btn_video_start_pause)
    Button btnVideoStartPause;
    @BindView(R.id.btn_video_next)
    Button btnVideoNext;
    @BindView(R.id.btn_video_siwch_screen)
    Button btnVideoSiwchScreen;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.mediaController)
    RelativeLayout  rel_mediaController;

    //默认的屏幕
    private static final int  DEFAULTSCREEN = 10 ;
    //全屏
    private static final int  FULL_SCREEN =  20;

    private static  final  int PROGRESS = 1;

    private Utils utils;

    //监听电量变化的广播
    private MyReceiver receiver;

    private ArrayList<MediaItem> mediaItems;

    private  boolean isShowMeidaContraller = false;

    private  static  final  int  MIDEACONTRALLER = 2;

    private int position;

    private Uri uri;

    private boolean isFullScreen = false;

    //手势识别器
    private GestureDetector detector;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case MIDEACONTRALLER:
                    InVisMediaController();
                    break;

                case PROGRESS:
                    int currentPosition = videoview.getCurrentPosition();

                    seekbarVideo.setProgress(currentPosition);

                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    tvSystemTime.setText(getSystemTime());

                    handler.removeMessages(PROGRESS);

                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                default:
                    break;
            }

        }
    };


    private String getSystemTime() {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_system_video_player);
        ButterKnife.bind(this);
        utils = new Utils();
        //从MediaPlayer中直接获取data的数据

        //初始化数据

        initData();

        setListener();

        getData();

        setData();

    }

    /**
     * 进行数据源的设置播放
     */
    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            tvName.setText(mediaItems.get(position).getName());
            videoview.setVideoPath(mediaItems.get(position).getData());
        } else if (uri != null) {
            tvName.setText(uri.toString());
            videoview.setVideoURI(uri);
        } else {
            Toast.makeText(this, "没有在手机上收到可以播放的文件", Toast.LENGTH_SHORT).show();
        }

        setButtonState();
    }

    /**
     * 设置数据源
     */
    private void getData() {
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("medislsit");

        position = getIntent().getIntExtra("position", 0);
        uri = getIntent().getData();

    }

    private void initData() {
        //注册电量广播
        receiver = new MyReceiver();

        IntentFilter intentfilter = new IntentFilter();

        intentfilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(receiver, intentfilter);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //得到当前系统的电量值
            int level = intent.getIntExtra("level", 0);

            if (level <= 0) {
                ivBattery.setBackgroundResource(R.drawable.ic_battery_0);
            } else if (level <= 10) {
                ivBattery.setBackgroundResource(R.drawable.ic_battery_10);
            } else if (level <= 20) {
                ivBattery.setBackgroundResource(R.drawable.ic_battery_20);
            } else if (level <= 40) {
                ivBattery.setBackgroundResource(R.drawable.ic_battery_40);
            } else if (level <= 60) {
                ivBattery.setBackgroundResource(R.drawable.ic_battery_60);
            } else if (level <= 80) {
                ivBattery.setBackgroundResource(R.drawable.ic_battery_80);
            } else if (level <= 100) {
                ivBattery.setBackgroundResource(R.drawable.ic_battery_100);
            } else {
                ivBattery.setBackgroundResource(R.drawable.ic_battery_100);
            }
        }
    }

    private void setListener() {

        btnVoice.setOnClickListener(this);
        btnSwichPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSiwchScreen.setOnClickListener(this);

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Toast.makeText(SystemVideoPlayer.this, "长按操作", Toast.LENGTH_SHORT).show();
                videoPlayState();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(SystemVideoPlayer.this, "双击操作", Toast.LENGTH_SHORT).show();
                if(isFullScreen){
                    //显示默认
                    setVideoType(DEFAULTSCREEN);
                }else{
                    //显示全屏
                    setVideoType(FULL_SCREEN);
                }
                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Toast.makeText(SystemVideoPlayer.this, "单击操作", Toast.LENGTH_SHORT).show();
                if(isShowMeidaContraller){
                    InVisMediaController();
                    handler.removeMessages(MIDEACONTRALLER);
                }else {
                    ShowMediaController();
                    //发消息，延迟隐藏控制栏
                    handler.sendEmptyMessageDelayed(MIDEACONTRALLER,5000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        //设置播放监听
        videoview.setOnPreparedListener(new MediaPredListener());

        videoview.setOnCompletionListener(new CompledListener());

        seekbarVideo.setOnSeekBarChangeListener(new SeekBarListener());
    }

    private void setVideoType(int Screen) {
        switch (Screen){
            case DEFAULTSCREEN:
                break;
            case FULL_SCREEN:
                break;
        }
    }


    /**
     * seekBar的监听
     */
    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * @param seekBar  控件
         * @param progress 进度
         * @param fromUser 如果用户触碰是true,否则是false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoview.seekTo(progress);
            }
        }

        /**
         * 用户手指触摸的时候
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
             handler.removeMessages(MIDEACONTRALLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
           handler.sendEmptyMessageDelayed(MIDEACONTRALLER,5000);
        }
    }

    /**
     * 播放器的监听
     */
    class MediaPredListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start();

            long duration = videoview.getDuration();

            //设置播放的时长
            tvDuration.setText(utils.stringForTime((int) duration));

            seekbarVideo.setMax((int) duration);

            //设置控制栏是默认隐藏的
            InVisMediaController();

            handler.sendEmptyMessage(PROGRESS);


        }
    }

    /**
     * 播放完成的监听
     */
    private class CompledListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(position==mediaItems.size()-1){
                finish();
            }else {
                playnextVideo();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_voice:
                break;
            case R.id.btn_swich_player:
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_video_pre:

                playPreVideo();

                break;
            case R.id.btn_video_start_pause:
                //判断播放的状态
                videoPlayState();
                break;
            case R.id.btn_video_next:
                playnextVideo();
                break;
            case R.id.btn_video_siwch_screen:
                break;
            default:
                break;

        }

        handler.removeMessages(MIDEACONTRALLER);
        handler.sendEmptyMessageDelayed(MIDEACONTRALLER,5000);
    }

    /**
     * 判断并改变播放的状态
     */
    private void videoPlayState() {
        if (videoview.isPlaying()) {
            videoview.pause();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            videoview.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {
                tvName.setText(mediaItems.get(position).getName());
                videoview.setVideoPath(mediaItems.get(position).getData());

                //设置按钮的状态
                setButtonState();
            }
        } else if (uri != null) {
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playnextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {
                tvName.setText(mediaItems.get(position).getName());
                videoview.setVideoPath(mediaItems.get(position).getData());

                //设置按钮的状态
                setButtonState();
            }
        } else if (uri != null) {
            setButtonState();
        }
    }

    /**
     * 设置按钮的播放状态
     */
    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {

            if (mediaItems.size() == 1) {
                //如果视频的个数只有一个的话,设置前后按钮都不能进行点击
                setButtonStateEnable(false);
            } else if (mediaItems.size() == 2) {

                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == (mediaItems.size() - 1)) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            } else {
                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                } else if (position == (mediaItems.size() - 1)) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else {
                    setButtonStateEnable(true);
                }
            }

        } else if (uri != null) {
            setButtonStateEnable(false);
        }
    }

    /**
     * 设置按钮的状态同一变化
     */
    private void setButtonStateEnable(boolean enable) {
        btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
        btnVideoPre.setEnabled(enable);
        btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
        btnVideoNext.setEnabled(enable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private  void ShowMediaController(){
        isShowMeidaContraller = true;
        rel_mediaController.setVisibility(View.VISIBLE);

    }

    private  void InVisMediaController(){
        isShowMeidaContraller = false;
        rel_mediaController.setVisibility(View.GONE);

    }


    @Override
    protected void onDestroy() {
        //先取消子类，再取消父类
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }


}
