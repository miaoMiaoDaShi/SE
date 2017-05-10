package lq.xxp.se.Bean;

/**
 * Created by 钟大爷 on 2016/10/30.
 */

public class ActorBean {
    String name;
    String imgUrl;
    String link;

    public ActorBean(String imgUrl, String link, String name) {
        this.imgUrl = imgUrl;
        this.link = link;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
