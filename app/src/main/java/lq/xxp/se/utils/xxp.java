package lq.xxp.se.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 钟大爷 on 2016/11/20.
 */

public class xxp {
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; And" +
            "roid 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWeb" +
            "Kit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/" +
            "43.0.2357.65 Mobile Safari/537.36";
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("User-Agent", USER_AGENT)
                    .build();

            return chain.proceed(request);
        }
    })
            .cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url, cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();
}
