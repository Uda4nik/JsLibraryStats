package com.scalablecapital;

import com.scalablecapital.infrastructure.DiContext;
import com.scalablecapital.stage.GoogleUrlProducer;

import java.util.List;
import java.util.Queue;

/*
 * SEDA ideas were used in this implementation of page crawler
 * Application prints current top 5 libs every second
 * Application can work forever, just new urls need to be added to first queue
 * */
public class Main {

    public static final int RESULT_LIMIT = 50;
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64)";
    public static final String ENCODING = "UTF-8";

    public static void main(String[] args) {
        if (args.length < 1) throw new RuntimeException("Please provide some search string");

        DiContext context = new DiContext();

        GoogleUrlProducer urlProducer = context.getUrlProvider();
        List<String> urls = urlProducer.provideFor(args[0]);

        context.addUrlToUrlQueue(urls);
        context.startAll();

//        Thread.sleep(10000);
//        context.stopAll();
    }
}
