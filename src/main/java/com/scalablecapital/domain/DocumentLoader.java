package com.scalablecapital.domain;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface DocumentLoader {
    // domain shouldn't have dependency for any library (like jsoup)
    Document load(String url) throws IOException;
}
