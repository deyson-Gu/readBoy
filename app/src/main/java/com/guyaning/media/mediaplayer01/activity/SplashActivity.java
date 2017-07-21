package com.guyaning.media.mediaplayer01.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.guyaning.media.mediaplayer01.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {

                        if(AndPermission.hasPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // 有权限，直接do anything.
                            startMainActivity();

                        } else {
                            // 申请权限。
                            AndPermission.with(SplashActivity.this)
                                    .requestCode(100)
                                    .permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_SETTINGS)
                                    .send();
                        }
                    }
                }, 2000);
    }

    private PermissionListener listener = new PermissionListener() {
        public static final  int REQUEST_CODE_SETTING = 406 ;

        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if(requestCode == 100) {
               startMainActivity();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(SplashActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
//                AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING).show();

//                 第二种：用自定义的提示语。
                AndPermission.defaultSettingDialog(SplashActivity.this, REQUEST_CODE_SETTING)
                        .setTitle("权限申请失败")
                        .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .setPositiveButton("好，去设置")
                        .show();
            }
        }
    };

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
