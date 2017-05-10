package lq.xxp.se.Bean;

/**
 * Created by 钟大爷 on 2016/11/5.
 */

public class PlayMovieBean {
    String name;
    String time;
    String imgUrl;
    String link;
    String type;

    public PlayMovieBean(String name, String time, String imgUrl, String link,String type) {
        this.name = name;
        this.time = time;
        this.imgUrl = imgUrl;
        this.link = link;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
