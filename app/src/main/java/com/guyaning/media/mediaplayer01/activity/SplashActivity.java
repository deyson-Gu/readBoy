package com.guyaning.media.mediaplayer01.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.guyaning.media.mediaplayer01.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        startMainActivity();

                    }
                }, 2000);
    }

    //设置一个标志位
    public boolean isstartMain = false;

    private void startMainActivity() {
        if (!isstartMain) {
            isstartMain = true;
            //延迟两秒之后所做的操作
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            //结束当前界面
            finish();
        }

    }

    //触摸进入

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }
}
