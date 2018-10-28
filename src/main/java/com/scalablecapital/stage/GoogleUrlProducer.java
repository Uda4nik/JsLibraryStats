package com.scalablecapital.stage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.scalablecapital.Main.USER_AGENT;
import static com.scalablecapital.Main.VISIT_LIMIT;


/*
 * Powermock to test this???
 * */
public class GoogleUrlProducer {
    private static final String SEARCH_URL = "http://www.google.com/search?q=%s&num=" + VISIT_LIMIT;
    private static final String ENCODING = "UTF-8";

    public List<String> provideFor(String searchString) {
        List<String> result = new ArrayList<>();

        try {
            String url = String.format(SEARCH_URL, URLEncoder.encode(searchString, ENCODING));
            Elements links = Jsoup.connect(url).userAgent(USER_AGENT).get().select(".g>.r>a");
            for (Element link : links) {
                String linkUrl = link.absUrl("href");
                linkUrl = URLDecoder.decode(linkUrl.substring(linkUrl.indexOf('=') + 1, linkUrl.indexOf('&')), ENCODING);
                if (!linkUrl.startsWith("http")) {
                    continue; // Ads/news/etc.
                }
                result.add(linkUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get links for " + searchString, e);
        }
        return result;
    }
}