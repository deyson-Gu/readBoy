package com.guyaning.media.mediaplayer01.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.bean.MediaItem;
import com.guyaning.media.mediaplayer01.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/3.
 */

public class videoPagerAdapter extends BaseAdapter {

    private Context context;

    private List<MediaItem> mediaItems;
    private ViewHolder holder;


    public videoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems) {
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
            convertView = convertView.inflate(context, R.layout.item_video_pager, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvVideoName = (TextView) convertView.findViewById(R.id.tv_videoName);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvSize = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MediaItem mediaItem = mediaItems.get(position);
        //为控件填充数据
        holder.tvVideoName.setText(mediaItem.getName());
        Utils utils = new Utils();
        holder.tvTime.setText(utils.stringForTime(Integer.parseInt(mediaItem.getDuration())));
        holder.tvSize.setText(Formatter.formatFileSize(context, Long.parseLong(mediaItem.getSize())));

        return convertView;
    }

    static class ViewHolder {
        TextView tvVideoName;     //视频名字
        ImageView ivIcon;    //视频图标
        TextView tvTime;     //视频时长
        TextView tvSize;     //视频大小
    }

}
