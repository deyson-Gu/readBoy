package com.guyaning.media.mediaplayer01.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.bean.VideoItem;

import java.util.List;

/**
 * Created by Administrator on 2017/3/3.
 */

public class netVideoPagerAdapter extends BaseAdapter {

    private Context context;

    private List<VideoItem.TrailersBean> mediaItems;
    private ViewHolder holder;


    public netVideoPagerAdapter(Context context, List<VideoItem.TrailersBean> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = convertView.inflate(context, R.layout.item_net_video_pager, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvVideoName = (TextView) convertView.findViewById(R.id.tv_net_videoName);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        VideoItem.TrailersBean trailersBean = mediaItems.get(position);
        //为控件填充数据
        holder.tvVideoName.setText(trailersBean.getMovieName());
        Glide.with(context).load(trailersBean.getCoverImg()).into(holder.ivIcon);
        holder.tvTitle.setText(trailersBean.getVideoTitle());

        return convertView;
    }

    static class ViewHolder {
        TextView tvVideoName;     //视频名字
        ImageView ivIcon;    //视频图标
        TextView tvTitle;     //视频标题

    }

}
