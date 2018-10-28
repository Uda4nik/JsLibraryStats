package com.scalablecapital.infrastructure;

import com.scalablecapital.domain.DocumentLoader;
import com.scalablecapital.domain.JsLibrary;
import com.scalablecapital.stage.GoogleUrlProducer;
import com.scalablecapital.stage.JavaLibraryExtractor;
import com.scalablecapital.stage.PageLoader;
import com.scalablecapital.stage.StatisticAggregator;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.*;

public class DiContext {
    private final DocumentLoader documentLoader = new JsoupDocumentLoader();
    private final GoogleResultLinkExtractor extractor = new GoogleResultLinkExtractor();
    //queues for data passing from one stage to another, could be limited to control memory
    private final LinkedBlockingQueue<String> urlQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> jsLibRawQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<JsLibrary> libraryQueue = new LinkedBlockingQueue<>();

    // thread pools for each stage - each can be tuned to utilize resources usage
    private final ExecutorService poolForIO = newCachedThreadPool();
    private final ExecutorService mainComponentsPool = newFixedThreadPool(3);
    private final ScheduledExecutorService printerPool = newScheduledThreadPool(1);

    //workers
    private final GoogleUrlProducer urlProvider = new GoogleUrlProducer(documentLoader, extractor);
    private final PageLoader pageLoader = new PageLoader(poolForIO, urlQueue, jsLibRawQueue, documentLoader);
    private final JavaLibraryExtractor jsLibExtractor = new JavaLibraryExtractor(jsLibRawQueue, libraryQueue);
    private final StatisticAggregator aggregator = new StatisticAggregator(libraryQueue);

    public GoogleUrlProducer getUrlProvider() {
        return urlProvider;
    }

    public Queue<String> getUrlQueue() {
        return urlQueue;
    }

    public void startAll() {
        mainComponentsPool.execute(pageLoader);
        mainComponentsPool.execute(jsLibExtractor);
        mainComponentsPool.execute(aggregator);
        printerPool.scheduleAtFixedRate(() -> System.out.println(aggregator.getTop(5)), 0, 1, TimeUnit.SECONDS);
    }

    public void stopAll() throws InterruptedException {
        pageLoader.setTerminated(true);
        jsLibExtractor.setTerminated(true);
        aggregator.setTerminated(true);

        poolForIO.shutdownNow();
        printerPool.shutdownNow();
        mainComponentsPool.shutdownNow();
    }
}
