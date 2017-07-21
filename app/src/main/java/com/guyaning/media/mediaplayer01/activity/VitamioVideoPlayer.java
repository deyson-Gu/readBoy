package com.guyaning.media.mediaplayer01.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.bean.MediaItem;
import com.guyaning.media.mediaplayer01.utils.LogUtil;
import com.guyaning.media.mediaplayer01.utils.Utils;
import com.guyaning.media.mediaplayer01.view.VitamioVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;


/**
 * Created by Administrator on 2017/3/22.
 */

public class VitamioVideoPlayer extends Activity implements View.OnClickListener {

    @BindView(R.id.videoview)
    VitamioVideoView videoview;
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

    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.mediaController)
    RelativeLayout rel_mediaController;

    private TextView tv_buffer_netspeed;
    private LinearLayout ll_buffer;
    private TextView tv_laoding_netspeed;
    private LinearLayout ll_loading;


    private boolean isUseSystem = true;

    /**
     * 显示网络速度
     */
    private static final int SHOW_SPEED = 3;

    private Button btnVideoSiwchScreen;
    //默认的屏幕
    private static final int DEFAULTSCREEN = 10;
    //全屏
    private static final int FULL_SCREEN = 20;

    private static final int PROGRESS = 1;

    private Utils utils;

    //监听电量变化的广播
    private MyReceiver receiver;

    private ArrayList<MediaItem> mediaItems;

    private boolean isShowMeidaContraller = false;

    private static final int MIDEACONTRALLER = 2;

    private int position;

    private Uri uri;

    private boolean isFullScreen = false;

    //手势识别器
    private GestureDetector detector;

    private int screenWidth = 0;

    private int screenHeight = 0;

    private int mVideoWidth;

    private int mVideoHeight;

    //是否静音
    private boolean isMute = false;

    //电量相关的参数
    private AudioManager audioManager;

    private int currentVoice;

    private int maxVoice;

    /**
     * 上一次的播放进度
     */
    private int precurrentPosition;
    /**
     * 是否是网络uri
     */
    private boolean isNetUri;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case SHOW_SPEED:
                    //1.得到网络速度
                    String netSpeed = utils.getNetSpeed(VitamioVideoPlayer.this);

                    //显示网络速
                    tv_laoding_netspeed.setText("玩命加载中..."+netSpeed);
                    tv_buffer_netspeed.setText("缓存中..."+netSpeed);

                    //2.每两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);
                    break;

                case MIDEACONTRALLER:
                    InVisMediaController();
                    break;

                case PROGRESS:
                    int currentPosition = (int) videoview.getCurrentPosition();

                    seekbarVideo.setProgress(currentPosition);

                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    tvSystemTime.setText(getSystemTime());

                    if(isNetUri){
                        //是网络资源的话，开启缓存进度刷新
                        int buffer = videoview.getBufferPercentage();
                        int totalBuffers = buffer*seekbarVideo.getMax();
                        int secondaryProgress = totalBuffers/100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    }else{
                        seekbarVideo.setSecondaryProgress(0);
                    }


                    //监听卡
                    if (!isUseSystem) {

                        if(videoview.isPlaying()){
                            int buffer = currentPosition - precurrentPosition;
                            if (buffer < 500) {
                                //视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡了
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else{
                            ll_buffer.setVisibility(View.GONE);
                        }

                    }

                    precurrentPosition = currentPosition;

                    handler.removeMessages(PROGRESS);

                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                default:
                    break;
            }

        }
    };

    private int startX;

    private String getSystemTime() {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    /**
     * 设置系统的亮度调节为手动
     */
    public void setScrennManualMode() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取系统的亮度
     *
     * @return
     */
    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SETTINGS};

    public Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_vitamio_video_player);
        btnVideoSiwchScreen = (Button) findViewById(R.id.btn_video_siwch_screen);
        tv_buffer_netspeed = (TextView) findViewById(R.id.tv_buffer_netspeed);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_laoding_netspeed = (TextView) findViewById(R.id.tv_laoding_netspeed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        //开始更新网络速度
        handler.sendEmptyMessage(SHOW_SPEED);

        ButterKnife.bind(this);
        utils = new Utils();
        setScrennManualMode();

        //初始化数据
        activity = this;
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
            isNetUri = new Utils().isNetUri(mediaItems.get(position).getData());
            videoview.setVideoPath(mediaItems.get(position).getData());
        } else if (uri != null) {
            tvName.setText(uri.toString());
            isNetUri = new Utils().isNetUri(uri.toString());
            videoview.setVideoURI(uri);
        } else {
            Toast.makeText(VitamioVideoPlayer.this, "没有在手机上收到可以播放的文件", Toast.LENGTH_SHORT).show();
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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);

        setScrennManualMode();
//        changeAppBrightness(SystemVideoPlayer.this,getSystemBrightness());

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


    //设置相关按钮和一些其他的监听事件
    private void setListener() {

        btnVoice.setOnClickListener(VitamioVideoPlayer.this);
        btnSwichPlayer.setOnClickListener(VitamioVideoPlayer.this);
        btnExit.setOnClickListener(VitamioVideoPlayer.this);
        btnVideoPre.setOnClickListener(VitamioVideoPlayer.this);
        btnVideoStartPause.setOnClickListener(VitamioVideoPlayer.this);
        btnVideoNext.setOnClickListener(VitamioVideoPlayer.this);
        btnVideoSiwchScreen.setOnClickListener(VitamioVideoPlayer.this);

        detector = new GestureDetector(VitamioVideoPlayer.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                videoPlayState();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                setVideoScreenType();
                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                if (isShowMeidaContraller) {
                    InVisMediaController();
                    handler.removeMessages(MIDEACONTRALLER);
                } else {
                    ShowMediaController();
                    //发消息，延迟隐藏控制栏
                    handler.sendEmptyMessageDelayed(MIDEACONTRALLER, 5000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        //设置播放监听
        videoview.setOnPreparedListener(new MediaPredListener());

        videoview.setOnCompletionListener(new CompledListener());

        seekbarVideo.setOnSeekBarChangeListener(new SeekBarListener());

        seekbarVoice.setOnSeekBarChangeListener(new VoiceListener());



        if (isUseSystem) {
            //监听视频播放卡-系统的api
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoview.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }


    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡了，拖动卡
//                    Toast.makeText(SystemVideoPlayer.this, "卡了", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡结束了，拖动卡结束了
//                    Toast.makeText(SystemVideoPlayer.this, "卡结束了", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }


    class VoiceListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }

                updateVoice(progress, isMute);

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(MIDEACONTRALLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(MIDEACONTRALLER, 5000);
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
            handler.sendEmptyMessageDelayed(MIDEACONTRALLER, 5000);
        }
    }

    /**
     * 播放器的监听
     */
    class MediaPredListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

            mVideoWidth = mp.getVideoWidth();

            mVideoHeight = mp.getVideoHeight();

            LogUtil.e(mVideoHeight + "宽---真实---" + mVideoHeight + "高");

            videoview.start();

            long duration = videoview.getDuration();

            //设置播放的时长
            tvDuration.setText(utils.stringForTime((int) duration));

            seekbarVideo.setMax((int) duration);

            //设置控制栏是默认隐藏的
            InVisMediaController();

            handler.sendEmptyMessage(PROGRESS);

            setVideoType(DEFAULTSCREEN);

        }
    }

    /**
     * 播放完成的监听
     */
    private class CompledListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (position == mediaItems.size() - 1) {
                finish();
            } else {
                playnextVideo();
            }

        }
    }

    /**
     * 更新音量的大小
     *
     * @param progress
     */
    private void updateVoice(int progress, boolean isMute) {
        if (isMute) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }

    }


    private void setVideoScreenType() {
        if (isFullScreen) {
            //显示默认
            setVideoType(DEFAULTSCREEN);

        } else {
            //显示全屏
            setVideoType(FULL_SCREEN);

        }
    }

    /***
     * 设置屏幕的样式
     * @param Screen
     */
    private void setVideoType(int Screen) {
        switch (Screen) {
            case FULL_SCREEN:
                LogUtil.e(screenWidth + "宽----手机-----" + screenHeight + "高");
                videoview.setVideoSize(screenWidth, screenHeight);
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULTSCREEN:
                //视频的真实宽高
                int videoWidth = mVideoWidth;
                int videoHeight = mVideoHeight;

                LogUtil.e(videoHeight + "宽---视频真实----" + videoHeight + "高");
                //屏幕的宽高
                int width = screenWidth;
                int height = screenHeight;

                //设置视频的大小,参照系统的videoview进行实现等比缩放
                if (videoWidth * height < width * videoHeight) {
                    width = height * videoWidth / videoHeight;
                } else if (videoWidth * height > width * videoHeight) {
                    height = width * videoHeight / videoWidth;
                }
                LogUtil.e(videoHeight + "宽---缩放之后的比例----" + videoHeight + "高");
                videoview.setVideoSize(width, height);
                //改变按钮的状态
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen = false;
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_voice:
                isMute = !isMute;
                updateVoice(currentVoice, isMute);
                break;
            case R.id.btn_swich_player:
                switchSystemPlayer();
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
                setVideoScreenType();
                break;
            default:
                break;

        }

        handler.removeMessages(MIDEACONTRALLER);
        handler.sendEmptyMessageDelayed(MIDEACONTRALLER, 5000);
    }

    private void switchSystemPlayer() {
        new AlertDialog.Builder(getBaseContext())
        .setTitle("系统提示")
        .setMessage("如果播放视频出现花屏的现象，请尝试切换播放器")
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemPlayer();
            }
        }).create().show();

    }

    private void startSystemPlayer() {

        if(videoview!=null){
            videoview.stopPlayback();
        }

        Intent intent = new Intent(getBaseContext(),VitamioVideoPlayer.class);
        if(mediaItems!=null && mediaItems.size()>0){
            Bundle bundle = new Bundle();
            bundle.putSerializable("medislsit",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
        }else if(uri!=null){
            intent.setData(uri);
        }
        startActivity(intent);
        finish();
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
                isNetUri = new Utils().isNetUri(mediaItems.get(position).getData());
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
                isNetUri = new Utils().isNetUri(mediaItems.get(position).getData());
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

    //滑动改变音量
    private float startPosi;    //滑动开始的位置
    private float endPosi;      //滑动结束时候的位置

    private int prcessVoice;  //滑动时候的音量

    private int movel;

    private int maxDistance;   //滑动的时候的总长度

    private float movePosition;  //移动的距离

    private float moveBright;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                startPosi = event.getY();  //得到开始的位置
                maxDistance = Math.min(screenHeight, screenWidth);
                movel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(MIDEACONTRALLER);

                startX = (int) event.getX();
                int currentBright = getSystemBrightness();
                LogUtil.e("系统亮度" + currentBright + "-----");
                break;
            case MotionEvent.ACTION_MOVE:

                int endX = (int) event.getX();

                moveBright = endX - startX;

                endPosi = event.getY();
                movePosition = startPosi - endPosi;
                // 改变的音量  滑动的距离/总的距离 *总的音量
                prcessVoice = (int) ((movePosition / maxDistance) * maxVoice);
                //现在音量
                int voice = Math.min(Math.max(movel + prcessVoice, 0), maxVoice);

                if(voice>=maxVoice){
                    voice = maxVoice;   //参照上面的表达式
                }else if(voice<=0) {
                    voice = 0;
                }

                int processBright = (int) ((moveBright / 480) * 255);
                int brigtht = Math.min(Math.max(getSystemBrightness() + processBright, 0), 255);
                //更新屏幕亮度
                changeAppBrightness(VitamioVideoPlayer.this, brigtht);
                LogUtil.e("系统亮度" + brigtht + "-----");


                //更新声音
                if (prcessVoice != 0) {
                    isMute = false;
                    updateVoice(voice, isMute);
                }


                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(MIDEACONTRALLER, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 改变App当前Window亮度
     *      * @param brightness
     */
    public void changeAppBrightness(Activity context, int brightness) {
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    private void ShowMediaController() {
        isShowMeidaContraller = true;
        rel_mediaController.setVisibility(View.VISIBLE);

    }

    private void InVisMediaController() {
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


    //监听物理键改变声音
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            updateVoice(currentVoice, false);
            handler.removeMessages(MIDEACONTRALLER);
            handler.sendEmptyMessageDelayed(MIDEACONTRALLER, 4000);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updateVoice(currentVoice, false);
            handler.removeMessages(MIDEACONTRALLER);
            handler.sendEmptyMessageDelayed(MIDEACONTRALLER, 4000);
        }
        return super.onKeyDown(keyCode, event);
    }
}
