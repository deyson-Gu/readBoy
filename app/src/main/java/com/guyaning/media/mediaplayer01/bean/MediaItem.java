package com.guyaning.media.mediaplayer01.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/2.
 * 媒体域对象
 */

public class MediaItem implements Serializable{

    private String name;
    private String duration;
    private String size;
    private String data;
    private String artist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration='" + duration + '\'' +
                ", size='" + size + '\'' +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
