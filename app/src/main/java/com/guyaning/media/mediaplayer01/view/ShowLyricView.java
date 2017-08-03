package com.guyaning.media.mediaplayer01.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.guyaning.media.mediaplayer01.bean.Lyric;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/31.
 */


@SuppressLint("AppCompatCustomView")
public class ShowLyricView extends TextView {

    //歌词的类的集合
    private ArrayList<Lyric> Lyrics;

    private int width, height;

    private Paint paint;

    private Paint whitePaint;

    //当前歌词的索引
    private int index;

    private float textHeight = 25;

    //当前播放的时间
    private int currentPosition;

    //时间节点
    private float timePoint;

    //高亮显示的时间
    private float seelpTime;

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.Lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (Lyrics != null && Lyrics.size() > 0) {

            //往上推移

            float plush = 0;
            if (seelpTime == 0) {
                plush = 0;
            } else {
                //平移
                //这一句所花的时间 ：休眠时间 = 移动的距离 ： 总距离（行高）
                //移动的距离 =  (这一句所花的时间 ：休眠时间)* 总距离（行高）
//                float delta = ((currentPositon-timePoint)/sleepTime )*textHeight;

                //屏幕的的坐标 = 行高 + 移动的距离
                plush = textHeight + ((currentPosition - timePoint) / seelpTime) * textHeight;
            }
            canvas.translate(0, -plush);
        }
        //绘制歌词
        if (Lyrics != null && Lyrics.size() > 0) {
            //开始绘制,绘制当前的歌词
            String currentText = Lyrics.get(index).getContent();
            canvas.drawText(currentText, width / 2, height / 2, paint);

            //绘制前面的部分的歌词，差别是y轴的变化
            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i--) {
                String preContent = Lyrics.get(i).getContent();
                tempY = tempY - textHeight;

                if (tempY < 0) {
                    break;
                }

                canvas.drawText(preContent, width / 2, tempY, whitePaint);
            }

            //绘制后面的部分
            tempY = height / 2;
            for (int i = index + 1; i < Lyrics.size(); i++) {
                String nextContent = Lyrics.get(i).getContent();
                tempY = tempY + textHeight;

                if (tempY > height) {
                    break;
                }

                canvas.drawText(nextContent, width / 2, tempY, whitePaint);
            }

        } else {
            //没有歌词
            canvas.drawText("没有歌词...", width / 2, height / 2, paint);
        }
    }

    //初始化数据
    private void initView(Context context) {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(20);
        paint.setAntiAlias(true);  //抗锯齿
        paint.setTextAlign(Paint.Align.CENTER); //设置位置

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(20);
        whitePaint.setAntiAlias(true);  //抗锯齿
        whitePaint.setTextAlign(Paint.Align.CENTER); //设置位置

    }

    //设置当前播放的高亮的歌词
    public void setCurrentTime(int currentPosition) {
        this.currentPosition = currentPosition;

        if (Lyrics == null || Lyrics.size() == 0) {
            return;
        }

        for (int i = 1; i < Lyrics.size(); i++) {

            if (currentPosition < Lyrics.get(i).getTimePoint()) {

                int tempIndex = i - 1;

                if (currentPosition >= Lyrics.get(tempIndex).getTimePoint()) {
                    //如果满足这个条件，说明当前的播放就是需要高亮显示的歌词
                    index = tempIndex;
                    timePoint = Lyrics.get(index).getTimePoint();
                    seelpTime = Lyrics.get(index).getSeelpTime();
                }
            }
        }

        //重新绘制歌词
        invalidate();

    }
}
