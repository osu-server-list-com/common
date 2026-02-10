package de.marcandreher.common.helpers;

import org.slf4j.Logger;

import de.marcandreher.fusionkit.core.FusionKit;
import okhttp3.OkHttpClient;

public class GetRequest {
    public static final String USER_AGENT = "osu!ListBot/3.0 (+https://osu-server-list.com/docs/crawler)";
    private static final Logger logger = FusionKit.getLogger(GetRequest.class);
    private static OkHttpClient client = new OkHttpClient();

    public static String send(String url) {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();
            
        long startTime = System.currentTimeMillis();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (!response.isSuccessful()) {
                logger.error("GET request to {} failed with status code {} ({}ms)", url, response.code(), elapsedTime);
                throw new Exception("Unexpected code " + response);
            }
            logger.debug("[GET] {} in ({}ms)", url, elapsedTime);
            return response.body().string();
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.error("Error during GET request to {} after ({}ms)", url, elapsedTime, e);
            return null;
        }
    }
}
