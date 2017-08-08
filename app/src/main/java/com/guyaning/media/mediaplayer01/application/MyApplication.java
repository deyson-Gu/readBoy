package com.guyaning.media.mediaplayer01.application;

import android.app.Application;

import com.guyaning.media.mediaplayer01.R;
import com.iflytek.cloud.SpeechUtility;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/7/18.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SpeechUtility.createUtility(MyApplication.this, "appid=" + getString(R.string.app_id));

        initOkGo();

    }

    public static final long CONNECT_OUT = 30000;

    private void initOkGo() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //全局的读取超时时间
        builder.readTimeout(CONNECT_OUT, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(CONNECT_OUT, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(CONNECT_OUT, TimeUnit.MILLISECONDS);

        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(5);
    }
}
