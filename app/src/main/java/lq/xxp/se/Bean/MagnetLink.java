package lq.xxp.se.Bean;

public class MagnetLink {

    protected String magnetLink;

    public static MagnetLink create(String magnetLink) {
        MagnetLink magnet = new MagnetLink();
        if (magnetLink != null) {
            magnet.magnetLink = magnetLink.substring(0, magnetLink.indexOf("&"));
        }
        return magnet;
    }

    public String getMagnetLink() {
        return magnetLink;
    }
}
