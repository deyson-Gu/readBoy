package com.guyaning.media.mediaplayer01.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guyaning.media.mediaplayer01.IMusicService;
import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.service.MusicPlayService;
import com.guyaning.media.mediaplayer01.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioPlayerActivity extends AppCompatActivity {


    @BindView(R.id.iv_icon)
    ImageView ivIcon;
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

    private int position;

    private IMusicService iMusicService;

    private BroadcastReceiver MyReveiver;

    private Utils utils;

    public static  final  int  PROGRESS = 100;

    private static  final  int  progress = 101;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case PROGRESS:
                    try {
                        int currentPosition = iMusicService.getDurationPosition();

                        tvTime.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(iMusicService.getDuration()));

                        seekbarAudio.setProgress(currentPosition);

                        //先移动消息在更新
                        handler.removeMessages(PROGRESS);

                        handler.sendEmptyMessageDelayed(PROGRESS,1000);

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
                    iMusicService.openAudio(position);
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
        setContentView(R.layout.activity_audio_player);
        ButterKnife.bind(this);

        initData();

        initView();

        getData();

        bingAndStartService();

        setListener();

    }

    private void setListener() {
        seekbarAudio.setOnSeekBarChangeListener(new MySeekChangeListener());
    }

    class  MySeekChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             if(fromUser){
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

        utils  =  new Utils();

        MyReveiver = new MyReveiver();

        IntentFilter intentFilter = new IntentFilter(MusicPlayService.OPEN_AUDIO);

        registerReceiver(MyReveiver,intentFilter);
    }


    class MyReveiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            //为控件设置数据
            setViewData();

        }
    }


    private void setViewData() {
        //为控件设置数据
        try {
            Toast.makeText(getBaseContext(),"收到广播了",Toast.LENGTH_SHORT).show();
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

        position = getIntent().getIntExtra("position", 0);
    }

    private void initView() {
        ImageView rocketImage = (ImageView) findViewById(R.id.iv_icon);
        rocketImage.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) rocketImage.getBackground();
        animationDrawable.start();
    }

    @OnClick({R.id.btn_audio_playmode, R.id.btn_audio_pre, R.id.btn_audio_start_pause, R.id.btn_audio_next, R.id.btn_lyrc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_audio_playmode:
                break;
            case R.id.btn_audio_pre:
                break;
            case R.id.btn_audio_start_pause:
                //控制音频的暂停和播放
                if(iMusicService!=null){

                    try {
                        if(iMusicService.isPlaying()){
                            //设置为暂停，改变按钮的状态为播放状态
                            iMusicService.pause();
                            btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                        }else{
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
                break;
            case R.id.btn_lyrc:
                break;
        }
    }


    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        if(MyReveiver!=null){
            unregisterReceiver(MyReveiver);
            MyReveiver = null;
        }

        if(serviceConnection!=null){
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        super.onDestroy();
    }
}
