package com.guyaning.media.mediaplayer01.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.guyaning.media.mediaplayer01.IMusicService;
import com.guyaning.media.mediaplayer01.bean.MediaItem;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/22.
 */

public class MusicPlayService extends Service {


    public static final String OPEN_AUDIO = "com.guyaning.mediaPlater.open_audio";
    private  ArrayList<MediaItem>   mediaItems;

    private MediaItem mediaItem;

    private MediaPlayer mediaPlayer;
    @Override
    public void onCreate() {
        super.onCreate();

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

    IMusicService.Stub   stub = new IMusicService.Stub() {

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


    };



    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    //打开指定位置的音频
    private void openAudio(int position){

        if(mediaItems!=null && mediaItems.size()>0){

            mediaItem = mediaItems.get(position);

            try {

                if(mediaPlayer!=null){
//                    mediaPlayer.release();
                    mediaPlayer.reset();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(new MyMusicOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyMusicOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyMusicOnErrorListener());
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync(); //准备异步播放
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(getBaseContext(),"暂时没有数据",Toast.LENGTH_SHORT).show();
        }

    }


    class  MyMusicOnPreparedListener   implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            Intent intent = new Intent(OPEN_AUDIO);
            getBaseContext().sendBroadcast(intent);
            start();
        }
    }

    class  MyMusicOnCompletionListener  implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    class  MyMusicOnErrorListener   implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return false;
        }
    }

    //开始
    private void start(){
       mediaPlayer.start();
    }

    //暂停
    private void pause(){
       mediaPlayer.pause();
    }

    //停止
    private  void stop(){

    }

    //得到当前播放音乐的时长
    private int getDurationPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    //得到播放的时长
    private int getDuration(){
        return mediaPlayer.getDuration();
    }

    //得到音乐的作者
    private String getArtist(){
        return mediaItem.getArtist();
    }

    //得到音乐的名称
    private String getName(){
        return mediaItem.getName();
    }

    //得到音乐的源路径
    private  String getAudioPath(){
        return "";
    }

    //上一个
    private  void pre(){

    }

    //下一个
    private  void next(){

    }

    //设置播放模式
    private void setPlayMode(int playMode){

    }


    //获取当前播放模式
    private int getPlayMode(){
        return 0;
    }

    //获取当前音频的状态
    private boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

}
