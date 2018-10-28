package com.scalablecapital.infrastructure;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static com.scalablecapital.Main.ENCODING;

public class GoogleResultLinkExtractor {

    public List<String> extract(Document document) throws UnsupportedEncodingException {
        List<String> result = new ArrayList<>();
        Elements links = document.select(".g>.r>a");
        for (Element link : links) {
            String linkUrl = link.absUrl("href");
            linkUrl = URLDecoder.decode(linkUrl.substring(linkUrl.indexOf('=') + 1, linkUrl.indexOf('&')), ENCODING);
            if (!linkUrl.startsWith("http")) {
                continue; // Ads/news/etc.
            }
            result.add(linkUrl);
        }
        return result;
    }
}
