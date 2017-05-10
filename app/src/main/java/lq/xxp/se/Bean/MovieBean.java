package lq.xxp.se.Bean;

/**
 * Created by 钟大爷 on 2016/11/1.
 */

public class MovieBean {
    String blockLink;
    String imgUrl;
    String name;
    String num;
    String time;


    public MovieBean(String blockLink, String imgUrl, String name,String num,String time) {
        this.blockLink = blockLink;
        this.imgUrl = imgUrl;
        this.name = name;
        this.num = num;
        this.time = time;
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

    public String getBlockLink() {
        return blockLink;
    }

    public void setBlockLink(String blockLink) {
        this.blockLink = blockLink;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
