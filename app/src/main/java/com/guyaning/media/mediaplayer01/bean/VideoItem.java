package com.guyaning.media.mediaplayer01.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class VideoItem {

    private List<TrailersBean> trailers;

    public List<TrailersBean> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<TrailersBean> trailers) {
        this.trailers = trailers;
    }

    public  class TrailersBean implements  Serializable{
        /**
         * id : 66563
         * movieName : 《敦刻尔克》中文预告
         * coverImg : http://img5.mtime.cn/mg/2016/12/15/174641.37616965.jpg
         * movieId : 230741
         * url : http://vfx.mtime.cn/Video/2017/07/12/mp4/170712132402662967.mp4
         * hightUrl : http://vfx.mtime.cn/Video/2017/07/12/mp4/170712132402662967.mp4
         * videoTitle : 敦刻尔克 中文定档预告
         * videoLength : 30
         * rating : -1
         * type : ["剧情","战争"]
         * summary : 军民协作“大撤退”
         */

        private int id;
        private String movieName;
        private String coverImg;
        private int movieId;
        private String url;
        private String hightUrl;
        private String videoTitle;
        private int videoLength;
        private int rating;
        private String summary;
        private List<String> type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMovieName() {
            return movieName;
        }

        public void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        public String getCoverImg() {
            return coverImg;
        }

        public void setCoverImg(String coverImg) {
            this.coverImg = coverImg;
        }

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHightUrl() {
            return hightUrl;
        }

        public void setHightUrl(String hightUrl) {
            this.hightUrl = hightUrl;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public int getVideoLength() {
            return videoLength;
        }

        public void setVideoLength(int videoLength) {
            this.videoLength = videoLength;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<String> getType() {
            return type;
        }

        public void setType(List<String> type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "TrailersBean{" +
                    "id=" + id +
                    ", movieName='" + movieName + '\'' +
                    ", coverImg='" + coverImg + '\'' +
                    ", movieId=" + movieId +
                    ", url='" + url + '\'' +
                    ", hightUrl='" + hightUrl + '\'' +
                    ", videoTitle='" + videoTitle + '\'' +
                    ", videoLength=" + videoLength +
                    ", rating=" + rating +
                    ", summary='" + summary + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "trailers=" + trailers +
                '}';
    }
}
