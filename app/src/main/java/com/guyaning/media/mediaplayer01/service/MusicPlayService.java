package com.guyaning.media.mediaplayer01.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.guyaning.media.mediaplayer01.IMusicService;
import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.activity.AudioPlayerActivity;
import com.guyaning.media.mediaplayer01.bean.MediaItem;
import com.guyaning.media.mediaplayer01.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/22.
 */

public class MusicPlayService extends Service {


    public static final String OPEN_AUDIO = "com.guyaning.mediaPlater.open_audio";
    private ArrayList<MediaItem> mediaItems;

    private MediaItem mediaItem;

    private MediaPlayer mediaPlayer;

    private Notification notification;

    private int  playMode = 2;

    public static  final int  PLAYMODE_SINGLE = 1;

    public static  final int  PLAYMODE_NORMAL = 2;

    public static  final int  PLAYMODE_ALL = 3;

    private int position;
    @Override
    public void onCreate() {
        super.onCreate();
        playMode = CacheUtils.getPlaymode(getBaseContext(),"playMode");
        //在服务初始化的时候获取数据
        getMusicItems();
    }

    private void getMusicItems() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                mediaItems = new ArrayList<MediaItem>();
                ContentResolver Resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] org = {
                        MediaStore.Audio.Media.DISPLAY_NAME,  //视频在sd卡中的名字
                        MediaStore.Audio.Media.DURATION,      //视频的总时长
                        MediaStore.Audio.Media.SIZE,          //视频的大小
                        MediaStore.Audio.Media.DATA,          //视频的绝对播放地址
                        MediaStore.Audio.Media.ARTIST,        //音视频的演唱者

                };

                Cursor cursor = Resolver.query(uri, org, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //向集合中添加数据
                        MediaItem mediaItem = new MediaItem();
                        mediaItems.add(mediaItem);

                        String name = cursor.getString(0);
                        mediaItem.setName(name);

                        String duration = cursor.getString(1);
                        mediaItem.setDuration(duration);

                        String size = cursor.getString(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                    }

                    cursor.close();
                }

            }
        }.start();
    }

    IMusicService.Stub stub = new IMusicService.Stub() {

        MusicPlayService musicPlayService = MusicPlayService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            musicPlayService.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            musicPlayService.start();
        }

        @Override
        public void pause() throws RemoteException {
            musicPlayService.pause();
        }

        @Override
        public void stop() throws RemoteException {
            musicPlayService.stop();
        }

        @Override
        public int getDurationPosition() throws RemoteException {
            return musicPlayService.getDurationPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return musicPlayService.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return musicPlayService.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return musicPlayService.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return musicPlayService.getAudioPath();
        }
        @Override
        public void pre() throws RemoteException {
            musicPlayService.pre();
        }

        @Override
        public void next() throws RemoteException {
            musicPlayService.next();

        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            musicPlayService.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return musicPlayService.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return musicPlayService.isPlaying();
        }

        @Override
        public void seekTo(int progress) throws RemoteException {
            mediaPlayer.seekTo(progress);
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }


    };


    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    //打开指定位置的音频
    private void openAudio(int position) {

        if (mediaItems != null && mediaItems.size() > 0) {

            mediaItem = mediaItems.get(position);

            try {

                if (mediaPlayer != null) {
//                    mediaPlayer.release();
                    mediaPlayer.reset();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(new MyMusicOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyMusicOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyMusicOnErrorListener());
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync(); //准备异步播放

                if(playMode == MusicPlayService.PLAYMODE_SINGLE){
                    mediaPlayer.setLooping(true);
                }else {
                    mediaPlayer.setLooping(false);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getBaseContext(), "暂时没有数据", Toast.LENGTH_SHORT).show();
        }

    }


    class MyMusicOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

//            Intent intent = new Intent(OPEN_AUDIO);
//            getBaseContext().sendBroadcast(intent);
            EventBus.getDefault().post(mediaItem);

            start();
        }
    }

    class MyMusicOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    class MyMusicOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return false;
        }
    }

    private NotificationManager manager;

    //开始
    @TargetApi(Build.VERSION_CODES.N)
    private void start() {
        mediaPlayer.start();
        //开启通知栏的管理
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent  =  new Intent(this, AudioPlayerActivity.class);

        intent.putExtra("Notification",true);

        PendingIntent pi = PendingIntent.getActivity(getBaseContext(),1,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        notification = new Notification.Builder(getBaseContext())
                .setContentTitle("万能播放器")
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentText("当前播放的音乐是"+mediaItem.getName())
                .setContentIntent(pi)
                .build();

        manager.notify(1, notification);
    }

    //暂停
    private void pause() {
        mediaPlayer.pause();
        manager.cancel(1);
    }

    //停止
    private void stop() {

    }

    //得到当前播放音乐的时长
    private int getDurationPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    //得到播放的时长
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    //得到音乐的作者
    private String getArtist() {
        return mediaItem.getArtist();
    }

    //得到音乐的名称
    private String getName() {
        return mediaItem.getName();
    }

    //得到音乐的源路径
    private String getAudioPath() {
        return mediaItem.getData();
    }

    //上一个
    private void pre() {

        setPrePositon();

        openPreAudio();

    }

    private void openPreAudio() {
        int playmode = getPlayMode();
        if(playmode==MusicPlayService.PLAYMODE_NORMAL){
            if(position>=0){
                openAudio(position);
            }else {
                openAudio(0);
            }
        }else if(playmode == MusicPlayService.PLAYMODE_SINGLE){
            openAudio(position);
        }else if(playmode ==MusicPlayService.PLAYMODE_ALL){
            openAudio(position);
        }else{
            if(position>=0){
                openAudio(position);
            }else {
                openAudio(0);
            }
        }
    }

    //设置上一个音频的位置
    private void setPrePositon() {
        int playmode = getPlayMode();
        if(playmode==MusicPlayService.PLAYMODE_NORMAL){
            position--;
        }else if(playmode == MusicPlayService.PLAYMODE_SINGLE){
            position--;
            if(position < 0){
              position = mediaItems.size() - 1;
            }
        }else if(playmode ==MusicPlayService.PLAYMODE_ALL){
            position--;
            if(position < 0){
                position = mediaItems.size() - 1;
            }
        }else{
            position--;
        }
    }

    //下一个
    private void next() {

        setNextPosition();

        openActionAudio();
    }

    //设置下一个音频的位置
    private void setNextPosition() {

        int playmode = getPlayMode();
        if(playmode==MusicPlayService.PLAYMODE_NORMAL){
            position++;
        }else if(playmode == MusicPlayService.PLAYMODE_SINGLE){
            position++;
            if(position >=mediaItems.size()){
                position = 0;
            }
        }else if(playmode ==MusicPlayService.PLAYMODE_ALL){
            position++;
            if(position >=mediaItems.size()){
                position = 0;
            }
        }else{
            position++;
        }
    }

    private void openActionAudio() {

        int playMode = getPlayMode();

        if (playMode == MusicPlayService.PLAYMODE_NORMAL) {
            if(position < mediaItems.size()){
                //正常范围
                openAudio(position);
            }else{
                position = mediaItems.size()-1;
            }

        } else if (playMode == MusicPlayService.PLAYMODE_ALL) {
           openAudio(position);
        } else if(playMode == MusicPlayService.PLAYMODE_SINGLE){
            openAudio(position);
        }else {
            if(position < mediaItems.size()){
                //正常范围
                openAudio(position);
            }else{
                position = mediaItems.size()-1;
            }
        }

    }

    //设置播放模式
    private void setPlayMode(int playMode) {
       this.playMode = playMode;

        CacheUtils.putPlaymode(getBaseContext(),"palyMode",playMode);

        if(playMode == MusicPlayService.PLAYMODE_SINGLE){

            mediaPlayer.setLooping(true);
        }else {
            mediaPlayer.setLooping(false);
        }
    }


    //获取当前播放模式
    private int getPlayMode() {
        return  playMode;
    }

    //获取当前音频的状态
    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

//    private int getAudioSessionId(){
//        return mediaPlayer.getAudioSessionId();
//    }

}
