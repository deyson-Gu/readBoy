package com.guyaning.media.mediaplayer01.bean;

/**
 * Created by Administrator on 2017/7/31.
 * 关于的歌词的类
 * [00:42.96]人们疯狂逃散那条曾经喧闹的街
 [00:46.37]火药的气味让生命全部凋谢
 [00:49.97]大地的哭泣声蔓延整个世界
 */

public class Lyric {
    /**
     * 歌词的内容
     */
   private String  content;
    /**
     * 时间节点
     */
    private  long  timePoint;

    /**
     * 高亮显示的时间
     */
    private long  seelpTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSeelpTime() {
        return seelpTime;
    }

    public void setSeelpTime(long seelpTime) {
        this.seelpTime = seelpTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", seelpTime=" + seelpTime +
                '}';
    }
}
