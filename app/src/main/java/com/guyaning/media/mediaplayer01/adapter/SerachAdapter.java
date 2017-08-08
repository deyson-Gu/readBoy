package com.guyaning.media.mediaplayer01.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guyaning.media.mediaplayer01.R;
import com.guyaning.media.mediaplayer01.bean.SerachBean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/3.
 */

public class SerachAdapter extends BaseAdapter {

    private Context context;

    private List<SerachBean.ItemsBean> items;

    private ViewHolder holder;
    private SerachBean.ItemsBean itemImageBean;


    public SerachAdapter(Context context, List<SerachBean.ItemsBean> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = convertView.inflate(context, R.layout.item_search, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvChannel = (TextView) convertView.findViewById(R.id.tv_channel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        itemImageBean = items.get(position);
        //为控件填充数据
        holder.tvTitle.setText(itemImageBean.getItemTitle());

        holder.tvTime.setText(itemImageBean.getPubTime());

        holder.tvChannel.setText(itemImageBean.getChannel());

        Glide.with(context).load(itemImageBean.getItemImage().getImgUrl1()).into(holder.ivIcon);

        return convertView;
    }

    static class ViewHolder {
        ImageView ivIcon;      //视频图标
        TextView tvTitle;      //视频标题
        TextView tvTime;       //视频的发布时间
        TextView tvChannel;    //所属频道

    }

}
