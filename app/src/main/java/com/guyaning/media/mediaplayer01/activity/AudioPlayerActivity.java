package com.guyaning.media.mediaplayer01.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guyaning.media.mediaplayer01.IMusicService;
import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.bean.MediaItem;
import com.guyaning.media.mediaplayer01.service.MusicPlayService;
import com.guyaning.media.mediaplayer01.utils.LyricUtils;
import com.guyaning.media.mediaplayer01.utils.Utils;
import com.guyaning.media.mediaplayer01.view.BaseVisualizerView;
import com.guyaning.media.mediaplayer01.view.ShowLyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AudioPlayerActivity extends Activity {


    @BindView(R.id.tv_artist)
    TextView tvArtist;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.seekbar_audio)
    SeekBar seekbarAudio;

    @BindView(R.id.btn_audio_playmode)
    Button btnAudioPlaymode;
    @BindView(R.id.btn_audio_pre)
    Button btnAudioPre;
    @BindView(R.id.btn_audio_start_pause)
    Button btnAudioStartPause;
    @BindView(R.id.btn_audio_next)
    Button btnAudioNext;
    @BindView(R.id.btn_lyrc)
    Button btnLyrc;

    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;

    @BindView(R.id.baseVisualizerView)
    BaseVisualizerView baseVisualizerView;

    @BindView(R.id.ShowLyricView)
    com.guyaning.media.mediaplayer01.view.ShowLyricView ShowLyricView;

    private int position;

    private IMusicService iMusicService;

    private BroadcastReceiver MyReveiver;

    private Utils utils;

    public static final int PROGRESS = 100;

    private static final int SHOW_LYRIC = 101;


    private boolean notification;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SHOW_LYRIC:
                    //将当前播放的进度传递给歌词控件
                    try {
                        int currentPosition = iMusicService.getDurationPosition();

                        ShowLyricView.setCurrentTime(currentPosition);

                        handler.removeMessages(SHOW_LYRIC);

                        handler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case PROGRESS:
                    try {
                        int currentPosition = iMusicService.getDurationPosition();

                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(iMusicService.getDuration()));

                        seekbarAudio.setProgress(currentPosition);

                        //先移动消息在更新
                        handler.removeMessages(PROGRESS);

                        handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    /**
     * 服务连接绑定的回调
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            iMusicService = IMusicService.Stub.asInterface(iBinder);
            if (iMusicService != null) {
                try {
                    if (!notification) {
                        iMusicService.openAudio(position);
                    } else {
                        setViewData();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (iMusicService != null) {
                try {
                    iMusicService.stop();
                    iMusicService = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        getData();

        bingAndStartService();

        setListener();

    }

    private void setListener() {
        seekbarAudio.setOnSeekBarChangeListener(new MySeekChangeListener());
    }

    class MySeekChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    iMusicService.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void initData() {

        utils = new Utils();

        EventBus.getDefault().register(this);

//        MyReveiver = new MyReveiver();
////
//        IntentFilter intentFilter = new IntentFilter(MusicPlayService.OPEN_AUDIO);
////
//        registerReceiver(MyReveiver, intentFilter);
    }


//    class MyReveiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            showLyric();
//            //为控件设置数据
//            setViewData();
//
//            checkModeState();
//
//            setupVisualizerFxAndUi();
//        }
//}

    private Visualizer mVisualizer;
    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi()
    {

        int audioSessionid = 0;
        try {
            audioSessionid = iMusicService.getAudioSessionId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("audioSessionid=="+audioSessionid);
        mVisualizer = new Visualizer(audioSessionid);
        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 设置允许波形表示，并且捕获它
        baseVisualizerView.setVisualizer(mVisualizer);
        mVisualizer.setEnabled(true);
    }


    //展示歌词
    private void showLyric() {
        LyricUtils lyricUtils = new LyricUtils();

        try {
            String path = iMusicService.getAudioPath();

            path = path.substring(0, path.lastIndexOf("."));


            File file = new File(path + ".lrc");
//            if(!file.exists()){
//               file = new File(name +".txt");
//            }
            lyricUtils.readLyricFile(file);

            ShowLyricView.setLyrics(lyricUtils.getLyrics());


        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (lyricUtils.isExistsLyric()) {
            handler.sendEmptyMessage(SHOW_LYRIC);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showViewData(MediaItem mediaItem) {

        showLyric();
        //为控件设置数据
        setViewData();

        checkModeState();

        setupVisualizerFxAndUi();
    }


    /**
     * 给控件设置数据
     */
    private void setViewData() {
        //为控件设置数据
        try {

            Toast.makeText(getBaseContext(), "收到广播了", Toast.LENGTH_SHORT).show();
            tvArtist.setText(iMusicService.getArtist());
            tvName.setText(iMusicService.getName());

            handler.sendEmptyMessage(PROGRESS);

            seekbarAudio.setMax(iMusicService.getDuration());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定和开启服务
     */
    private void bingAndStartService() {
        Intent intent = new Intent(this, MusicPlayService.class);
        intent.setAction("com.guyaning.media.mediaPlayer01");
        bindService(intent, serviceConnection, MusicPlayService.BIND_AUTO_CREATE);
        //防止重复创建service
        startService(intent);

    }


    private void getData() {

        notification = getIntent().getBooleanExtra("Notification", false);

        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }

    }

    private void initView() {
        setContentView(R.layout.activity_audio_player);
        ButterKnife.bind(this);

        ShowLyricView = (ShowLyricView) findViewById(R.id.ShowLyricView);
//        ImageView rocketImage = (ImageView) findViewById(R.id.iv_icon);
//        rocketImage.setBackgroundResource(R.drawable.animation_list);
//        AnimationDrawable animationDrawable = (AnimationDrawable) rocketImage.getBackground();
//        animationDrawable.start();
    }

    @OnClick({R.id.btn_audio_playmode, R.id.btn_audio_pre, R.id.btn_audio_start_pause, R.id.btn_audio_next, R.id.btn_lyrc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_audio_playmode:
                //控制播放模式的改变

                try {
                    int playMode = iMusicService.getPlayMode();
                    if (playMode == MusicPlayService.PLAYMODE_NORMAL) {
                        playMode = MusicPlayService.PLAYMODE_SINGLE;
                    } else if (playMode == MusicPlayService.PLAYMODE_SINGLE) {
                        playMode = MusicPlayService.PLAYMODE_ALL;
                    } else if (playMode == MusicPlayService.PLAYMODE_ALL) {
                        playMode = MusicPlayService.PLAYMODE_NORMAL;
                    } else {
                        playMode = MusicPlayService.PLAYMODE_NORMAL;
                    }

                    iMusicService.setPlayMode(playMode);

                    showModeState();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_audio_pre:
                try {
                    if (iMusicService != null) {
                        iMusicService.pre();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_audio_start_pause:
                //控制音频的暂停和播放
                if (iMusicService != null) {

                    try {
                        if (iMusicService.isPlaying()) {
                            //设置为暂停，改变按钮的状态为播放状态
                            iMusicService.pause();
                            btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                        } else {
                            //设置成相反的状态
                            iMusicService.start();
                            btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.btn_audio_next:
                try {
                    if (iMusicService != null) {
                        iMusicService.next();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_lyrc:
                break;
        }
    }

    //模式的状态
    private void showModeState() {
        //控制播放模式的改变

        try {
            int playMode = iMusicService.getPlayMode();
            if (playMode == MusicPlayService.PLAYMODE_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playMode == MusicPlayService.PLAYMODE_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else if (playMode == MusicPlayService.PLAYMODE_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    //模式的状态
    private void checkModeState() {
        //控制播放模式的改变

        try {
            int playMode = iMusicService.getPlayMode();

            if (playMode == MusicPlayService.PLAYMODE_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playMode == MusicPlayService.PLAYMODE_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else if (playMode == MusicPlayService.PLAYMODE_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        if (MyReveiver != null) {
            unregisterReceiver(MyReveiver);
            MyReveiver = null;
        }

        //取消注册
        EventBus.getDefault().unregister(this);

        if (serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        super.onDestroy();
    }
}
