package lq.xxp.se.Bean;

/**
 * Created by 钟大爷 on 2016/11/6.
 */

public class DbBean {
    private String title;
    private String type;
    private String imgUrl;
    private String num;
    private String time;
    private String Link;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DbBean(String title, String type, String link,String imgUrl,String num,String time) {
        this.title = title;
        this.type = type;
        this.Link = link;
        this.imgUrl = imgUrl;
        this.num = num;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
