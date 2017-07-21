package com.guyaning.media.mediaplayer01.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.guyaning.media.mediaplayer01.R;

public class AudioPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        ImageView rocketImage = (ImageView) findViewById(R.id.iv_icon);
        rocketImage.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) rocketImage.getBackground();
        animationDrawable.start();

    }

}
