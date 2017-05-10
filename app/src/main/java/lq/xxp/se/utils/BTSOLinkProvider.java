package lq.xxp.se.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.DownloadLink;
import lq.xxp.se.Bean.MagnetLink;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class BTSOLinkProvider extends DownloadLinkProvider {


    @Override
    public Call<ResponseBody> search(String keyword, int page) {
        return BTSO.INSTANCE.search(keyword, page);
    }

    @Override
    public List<DownloadLink> parseDownloadLinks(String htmlContent) {
        ArrayList<DownloadLink> links = new ArrayList<>();
        Elements rows = Jsoup.parse(htmlContent).getElementsByClass("row");
        for (Element row : rows) {
            try {
                Element a = row.getElementsByTag("a").first();
                links.add(
                        DownloadLink.create(
                                row.getElementsByClass("file").first().text(),
                                "文件大小：" + row.getElementsByClass("size").first().text(),
                                row.getElementsByClass("date").first().text(),
                                a.attr("href"),
                                null)
                );
            } catch (Exception ignored) {

            }
        }
        return links;
    }

    @Override
    public Call<ResponseBody> get(String url) {
        return BTSO.INSTANCE.get(url);
    }

    @Override
    public MagnetLink parseMagnetLink(String htmlContent) {
        return MagnetLink.create(Jsoup.parse(htmlContent).getElementsByClass("magnet-link").first().text());
    }
}