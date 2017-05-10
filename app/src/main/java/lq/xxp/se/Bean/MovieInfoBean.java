package lq.xxp.se.Bean;

/**
 * Created by 钟大爷 on 2016/11/3.
 */

public class MovieInfoBean {

    String name;
    String imgUrl;
    String num;
    String time;
    String lenght;
    String producers;
    String series;
    String category;

    public MovieInfoBean(String category, String imgUrl, String lenght,
                         String name, String num, String producers, String series, String time) {
        this.category = category;
        this.imgUrl = imgUrl;
        this.lenght = lenght;
        this.name = name;
        this.num = num;
        this.producers = producers;
        this.series = series;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLenght() {
        return lenght;
    }

    public void setLenght(String lenght) {
        this.lenght = lenght;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getProducers() {
        return producers;
    }

    public void setProducers(String producers) {
        this.producers = producers;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
