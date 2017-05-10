package lq.xxp.se.utils;

import java.util.List;

import lq.xxp.se.Bean.DownloadLink;
import lq.xxp.se.Bean.MagnetLink;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Project: XXPSE
 */
public abstract class DownloadLinkProvider {
    public abstract Call<ResponseBody> search(String keyword, int page);

    public abstract List<DownloadLink> parseDownloadLinks(String htmlContent);

    public abstract Call<ResponseBody> get(String url);

    public abstract MagnetLink parseMagnetLink(String htmlContent);

    public static DownloadLinkProvider getProvider(String name) {
        switch (name.toLowerCase().trim()) {
            case "btso":
                return new BTSOLinkProvider();
            case "torrentkitty":
                return new TorrentKittyLinkProvider();
            default:
                return null;
        }
    }
}
