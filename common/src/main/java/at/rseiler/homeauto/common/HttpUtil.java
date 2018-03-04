package at.rseiler.homeauto.common;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Very basic HTTP util class.
 */
public final class HttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();

    /**
     * Fetches the data from the given url.
     *
     * @param url the url which will be opened
     * @return the content
     */
    public static String get(String url) {
        String result = "";

        try {
            HttpGet method = new HttpGet(url);
            method.addHeader("X-Requested-With", "XMLHttpRequest");
            HttpResponse httpResponse = HTTP_CLIENT.execute(method);
            result = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8").replaceAll("\\n", "");
            EntityUtils.consume(httpResponse.getEntity());
        } catch (Exception e) {
            LOGGER.error("Failed to open url", e);
        }

        return result;
    }

    /**
     * Sends a post request to the given url.
     *
     * @param url           the url which will be opened
     * @param authorization content for the authorization header
     */
    public static void post(String url, String authorization) {
        try {
            HttpPost method = new HttpPost(url);

            if (authorization != null) {
                method.addHeader("Authorization", authorization);
            }

            HttpResponse httpResponse = HTTP_CLIENT.execute(method);
            EntityUtils.consume(httpResponse.getEntity());
        } catch (Exception e) {
            LOGGER.error("Failed to open url", e);
        }
    }

    private HttpUtil() {
    }
}