package com.scalablecapital.stage;

import com.scalablecapital.domain.DocumentLoader;
import com.scalablecapital.infrastructure.GoogleResultLinkExtractor;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleUrlProducerTest {

    GoogleUrlProducer cut;
    DocumentLoader documentLoader;
    GoogleResultLinkExtractor extractor;

    @BeforeMethod
    public void setUp() throws Exception {
        documentLoader = mock(DocumentLoader.class);
        extractor = mock(GoogleResultLinkExtractor.class);
        cut = new GoogleUrlProducer(documentLoader, extractor);
    }

    @Test
    public void usesDocLoadAndGoogleExtractorToGetLinks() throws Exception {
        Document doc = new Document("doesnt matter");
        when(documentLoader.load("http://www.google.com/search?q=test&num=50")).thenReturn(doc);
        when(extractor.extract(doc)).thenReturn(Arrays.asList("link1", "link2"));

        List<String> links = cut.provideFor("test");

        assertThat(links).containsExactly("link1", "link2");
    }
}