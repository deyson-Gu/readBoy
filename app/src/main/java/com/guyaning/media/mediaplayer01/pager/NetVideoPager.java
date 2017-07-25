package com.guyaning.media.mediaplayer01.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.activity.SystemVideoPlayer;
import com.guyaning.media.mediaplayer01.adapter.netVideoPagerAdapter;
import com.guyaning.media.mediaplayer01.base.BasePager;
import com.guyaning.media.mediaplayer01.bean.MediaItem;
import com.guyaning.media.mediaplayer01.bean.VideoItem;
import com.guyaning.media.mediaplayer01.utils.Constants;
import com.guyaning.media.mediaplayer01.utils.LogUtil;
import com.guyaning.media.mediaplayer01.view.XListView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2017/2/27.
 * 网络视频界面
 */

public class NetVideoPager extends BasePager {


    private  com.guyaning.media.mediaplayer01.view.XListView listview;

    private TextView tvNomedia;

    private ProgressBar pbLoading;

    private List<VideoItem.TrailersBean> trailers;

    private ArrayList<MediaItem> mediaItems;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    setData();
                    break;
            }
        }
    };
    private MediaItem mediaItem;
    private netVideoPagerAdapter netAdapter;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_viedeo_pager, null);
        listview = (XListView) view.findViewById(R.id.listview);
        listview.setPullLoadEnable(true);
        tvNomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        trailers = new ArrayList<>();
        mediaItems = new ArrayList<>();
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        requetDataFromNet();
    }

    private void setData() {

        netAdapter = new netVideoPagerAdapter(context, trailers);

        listview.setAdapter(netAdapter);

        for (int i = 0; i < trailers.size(); i++) {
            mediaItem = new MediaItem();
            mediaItem.setName(trailers.get(i).getMovieName());
            mediaItem.setData(trailers.get(i).getHightUrl());
            mediaItem.setArtist(trailers.get(i).getCoverImg());
            mediaItem.setDuration(String.valueOf(trailers.get(i).getVideoLength()));
            mediaItems.add(mediaItem);
        }

        LogUtil.e(trailers.size() + "----");
        LogUtil.e(mediaItems.size()+"----"+mediaItems.get(19).getName());
        setListener();
    }

    //设置监听
    private void setListener() {

        listview.setXListViewListener(new MyPullToResfshListener());

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(context, SystemVideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("medislsit", mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position", position-1);
                context.startActivity(intent);
            }
        });
    }


    /**
     * 下拉刷新和上拉加载
     */
    class  MyPullToResfshListener implements XListView.IXListViewListener{

        @Override
        public void onRefresh() {
             requetDataFromNet();
             onLoad();
        }

        @Override
        public void onLoadMore() {
            requetDataFromNet();
            netAdapter.notifyDataSetChanged();
            onLoad();
        }
    }

    private void onLoad() {
        listview.stopRefresh();
        listview.stopLoadMore();
        listview.setRefreshTime("时间:"+getSystemTime());
    }
    private String getSystemTime() {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 获取关于网络视频的资源
     */
    private void requetDataFromNet() {
        OkGo.<String>get(Constants.NET_URL)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        VideoItem videoItem = JSON.parseObject(response.body(), VideoItem.class);
                        LogUtil.e(response.body()+"--------");
                        trailers = videoItem.getTrailers();
                        mHandler.sendEmptyMessage(100);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogUtil.e(response.toString());
                        tvNomedia.setText("请求错误");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        pbLoading.setVisibility(View.GONE);
                    }
                });
    }
}
