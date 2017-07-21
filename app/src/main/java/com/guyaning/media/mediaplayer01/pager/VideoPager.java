package com.guyaning.media.mediaplayer01.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.activity.SystemVideoPlayer;
import com.guyaning.media.mediaplayer01.adapter.videoPagerAdapter;
import com.guyaning.media.mediaplayer01.base.BasePager;
import com.guyaning.media.mediaplayer01.bean.MediaItem;
import com.guyaning.media.mediaplayer01.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/27.
 * 本地视频界面
 */

public class VideoPager extends BasePager {

    private ListView listview;
    private TextView tvNoMedia;
    private ProgressBar pb;

    private ArrayList<MediaItem> mediaItems;
    private com.guyaning.media.mediaplayer01.adapter.videoPagerAdapter videoPagerAdapter;
    private com.guyaning.media.mediaplayer01.adapter.videoPagerAdapter pagerAdapter;

    public VideoPager(Context context) {
        super(context);
    }

    /**
     * 利用handler发消息，来进行数据适配
     */
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SystemClock.sleep(2000);
            if (mediaItems != null && mediaItems.size() > 0) {
                //设置数据
                pagerAdapter = new videoPagerAdapter(context,mediaItems,false);
                listview.setAdapter(pagerAdapter);
                //隐藏文本
                tvNoMedia.setVisibility(View.GONE);
                LogUtil.e("集合的大小是"+mediaItems.size());
            } else {
                //显示文本
                tvNoMedia.setVisibility(View.VISIBLE);
                tvNoMedia.setText("手机中没有改类型的数据");
            }

            //隐藏progressBar
            pb.setVisibility(View.GONE);

        }
    };

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.viedeo_pager, null);
        listview = (ListView) view.findViewById(R.id.listview);
        tvNoMedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pb = (ProgressBar) view.findViewById(R.id.pb_loading);

        //设置listview的条目的点击事件
        listview.setOnItemClickListener(new MyListviewOnclickListener());
        return view;
    }

     class MyListviewOnclickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Toast.makeText(context, mediaItems.get(position).toString()+"", Toast.LENGTH_SHORT).show();
//            //点击实现系统播放
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaItems.get(position).getData()),"video/*");
//            context.startActivity(intent);

//            点击实现自定义播放
//            Intent intent = new Intent(context,SystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItems.get(position).getData()),"video/*");
//            context.startActivity(intent);

            //传递列表进行播放的切换操作
            Intent intent = new Intent(context,SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("medislsit",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频数据初始化了");
        //判断是否有权限
        getDataFromLocal();
    }

    /**
     * 从本地内存中读取数据
     * 一般采用通过内容提供者从数据库中提取
     */
    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                mediaItems = new ArrayList<MediaItem>();
                ContentResolver Resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] org = {
                        MediaStore.Video.Media.DISPLAY_NAME,  //视频在sd卡中的名字
                        MediaStore.Video.Media.DURATION,      //视频的总时长
                        MediaStore.Video.Media.SIZE,          //视频的大小
                        MediaStore.Video.Media.DATA,          //视频的绝对播放地址
                        MediaStore.Video.Media.ARTIST,        //音视频的演唱者

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
                //发送消息
                handler.sendEmptyMessage(100);

            }
        }.start();

    }
}
