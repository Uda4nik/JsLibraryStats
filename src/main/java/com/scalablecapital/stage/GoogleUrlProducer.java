package com.scalablecapital.stage;

import com.scalablecapital.domain.DocumentLoader;
import com.scalablecapital.infrastructure.GoogleResultLinkExtractor;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.util.List;

import static com.scalablecapital.Main.ENCODING;
import static com.scalablecapital.Main.RESULT_LIMIT;


public class GoogleUrlProducer {
    private static final String SEARCH_URL = "http://www.google.com/search?q=%s&num=" + RESULT_LIMIT;

    private final DocumentLoader documentLoader;
    private final GoogleResultLinkExtractor extractor;

    public GoogleUrlProducer(DocumentLoader documentLoader, GoogleResultLinkExtractor extractor) {
        this.documentLoader = documentLoader;
        this.extractor = extractor;
    }

    public List<String> provideFor(String searchString) {
        try {
            String url = String.format(SEARCH_URL, URLEncoder.encode(searchString, ENCODING));
            Document document = documentLoader.load(url);
            return extractor.extract(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get links for " + searchString, e);
        }
    }
}