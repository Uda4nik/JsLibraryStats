package com.scalablecapital.infrastructure;

import com.scalablecapital.domain.DocumentLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static com.scalablecapital.Main.USER_AGENT;

public class JsoupDocumentLoader implements DocumentLoader {
    @Override
    public Document load(String url) throws IOException {
        return Jsoup.connect(url).userAgent(USER_AGENT).get();
    }
}
