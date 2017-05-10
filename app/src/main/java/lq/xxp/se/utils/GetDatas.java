package lq.xxp.se.utils;

import android.util.Log;

import com.show.api.ShowApiRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lq.xxp.se.Bean.HomeimgBean;
import lq.xxp.se.Bean.MovieBean;
import lq.xxp.se.Bean.MovieInfoBean;
import lq.xxp.se.Bean.PlayBean;


/**
 * Created by 钟大爷 on 2016/10/30.
 */

public class GetDatas {
    public static String getMovieJson() {
        String movieJson = "";
        return movieJson;
    }

    public static List<MovieBean> getInfo(String url) {
        List<MovieBean> lists = new ArrayList<MovieBean>();
        Document document;
        try {
            document = Jsoup.connect(url).timeout(5000).get();
            Elements elements = document.select("#waterfall").first().select("div.item");
            for (Element element : elements) {
                String blockLink = element.select("a").attr("href");
                if (blockLink.length() < 50) {
                    String imgSrc = element.select("div.photo-frame").select("img").attr("src");
                    String name = element.select("div.photo-frame").select("img").attr("title");
                    String num = element.select("div.photo-info").select("span").select("date").first().text();
                    String time = element.select("div.photo-info").select("span").select("date").last().text();
                    lists.add(new MovieBean(blockLink, imgSrc, name, num, time));
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
        }

        return lists;
    }

    public static MovieInfoBean getMovieInfo(String url) {
        String name = null;
        String imgSrc = null;
        String num = null;
        String time = null;
        String len = null;
        String make = null;
        String series = null;
        String category = null;

        try {
            Document document = Jsoup.connect(url).timeout(5000).get();
            Elements elements = document.select("div.container");
            Element element = elements.get(1);
            name = element.select("h3").text();
            imgSrc = element.select("a").attr("href");
            Elements elements2 = elements.select("p");
            num = elements2.get(0).select("span[style]").text();
            time = elements2.get(1).ownText();
            len = elements2.get(elements2.size() - 6).ownText();
            make = elements2.get(elements2.size() - 4).text();
            series = elements2.get(elements2.size() - 2).text();
            category = elements2.get(elements2.size() - 1).text();
            return new MovieInfoBean(category, imgSrc, len, name, num, make, series, time);

        } catch (Exception e) {
            return new MovieInfoBean("暂无", "暂无", "暂无", "暂无", "暂无", "暂无", "暂无", "暂无");
        }

    }


    public static List<Map<String, Object>> getSeedInfo(String num) {
        List<Map<String, Object>> infos = new ArrayList<>();
        String url = "http://www.cilihome.com/word/" + num + ".html";

        Document document = null;
        try {
            document = Jsoup.connect(url).get();
            Elements elements = document.select("div.r");
            for (Element element : elements) {
                String Link = element.select("a").attr("abs:href");
                String title = element.select("a").select("h5").text();
                Elements spans = element.select("span");
                String otherInfo = spans.get(1).text();
                Document document2 = Jsoup.connect(Link).get();
                Element element2 = document2.select("dl.BotInfo").first();
                Elements elements2 = element2.select("p");
                String seedLink = elements2.get(4).select("a").attr("href");
                Map<String, Object> map = new HashMap<String, Object>();

                Log.d("mmds", "在get中" + title + otherInfo + seedLink);
                map.put("title", title);
                map.put("otherInfo", otherInfo);
                map.put("seedLink", seedLink);
                infos.add(map);
            }

            return infos;
        } catch (IOException e) {
        }
        return null;
    }

    public static String GetJson(String url) {
        String result = "";
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = new URL(url).openStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String len = "";
            while ((len = br.readLine()) != null) {
                result += len;
            }
        } catch (IOException e) {

        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }

        }


        return result;
    }

    public static List<PlayBean> getPlayVideo(String url) {
        List<PlayBean> lists = new ArrayList<PlayBean>();
        try {
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(100000).get();
            Elements elements = document.select("ul.movieList").select("li");
            int i = 1;
            for (Element element : elements) {
                String blockLink = element.select("a").attr("abs:href");
                String imgUrl = element.select("a").select("img").attr("src");
                String time = element.select("a").select("h3").text();
                Document document2 = Jsoup.connect(blockLink).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(100000).get();

                String element2 = document2.select("div[align]").select("a").attr("href");
                String title = document2.select("title").text();
                String titles[];
                if (title.contains("&nbsp;&nbsp;")) {
                    titles = title.split("&nbsp;&nbsp;");
                } else if (title.contains("&nbsp;&nb")) {
                    titles = title.split("&nbsp;");
                } else if (title.contains("&nbsp;&")) {
                    titles = title.split("&nbsp;&");
                } else if (title.contains("&nbsp;")) {
                    titles = title.split("&nbsp;&nb");
                } else {
                    titles = title.split("亚洲图片");
                }

                lists.add(new PlayBean(i++ + "", titles[0], time, element2, imgUrl, "无码"));
            }

            return lists;
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        return null;
    }


    public static List<HomeimgBean> getImgJson(int pag) {
        List<HomeimgBean> lists = new ArrayList<HomeimgBean>();
        String jsonString = initImgData(pag);
        try {
            Log.e("mmds", jsonString);
            JSONObject jb = new JSONObject(jsonString);
            jb = jb.getJSONObject("showapi_res_body");
            JSONArray ja = jb.getJSONArray("newslist");
            Log.e("mmds", ja.length() + "");
            for (int i = 0; i < ja.length(); i++) {
                jb = ja.getJSONObject(i);
                String title = jb.getString("title");
                String picUrl = jb.getString("picUrl");
                String description = jb.getString("description");
                String ctime = jb.getString("ctime");
                String blockUrl = jb.getString("url");
                lists.add(new HomeimgBean(title, picUrl, description, ctime, blockUrl));
            }
            return lists;
        } catch (JSONException e) {
        }
        return null;
    }

    public static String initImgData(int pag) {
        String appid = "27477";//要替换成自己的
        String secret = "c2c949d03d7a4191b4840e776efc47d4";//要替换成自己的
        final String res = new ShowApiRequest("http://route.showapi.com/197-1", appid, secret)
                .addTextPara("num", "10")
                .addTextPara("page", pag + "")
                .post();
        return res;
    }
 }
