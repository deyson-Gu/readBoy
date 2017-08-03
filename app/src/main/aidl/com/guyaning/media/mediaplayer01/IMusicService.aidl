// IMusicService.aidl
package com.guyaning.media.mediaplayer01;

// Declare any non-default types here with import statements

interface IMusicService {

        //打开指定位置的音频
        void openAudio(int position);


        //开始
        void start();


        //暂停
        void pause();


        //停止
        void stop();


        //得到当前播放音乐的时长
        int getDurationPosition();


        //得到播放的时长
        int getDuration();


        //得到音乐的作者
        String getArtist();


        //得到音乐的名称
        String getName();


        //得到音乐的源路径
        String getAudioPath();


        //上一个
        void pre();


        //下一个
        void next();


        //设置播放模式
       void setPlayMode(int playMode);


        //获取当前播放模式
       int getPlayMode();


       //获取音频的控制状态
       boolean isPlaying();

       void  seekTo(int progress);

       int  getAudioSessionId();

}
