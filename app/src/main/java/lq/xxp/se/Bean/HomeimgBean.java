package lq.xxp.se.Bean;

/**
 * Created by 钟大爷 on 2016/11/18.
 */

public class HomeimgBean {
    String title;
    String picUrl;
    String description;
    String ctime;
    String url;

    public HomeimgBean(String title, String picUrl, String description, String ctime, String url) {
        this.title = title;
        this.picUrl = picUrl;
        this.description = description;
        this.ctime = ctime;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
