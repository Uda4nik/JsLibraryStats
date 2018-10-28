package com.scalablecapital.stage;

import com.scalablecapital.domain.JsLibrary;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;


/*
* This stage does extraction of JsLibrary from given strings
* Currently works in one thread cause does not much work
* */
public class JavaLibraryExtractor implements Runnable {
    private final LinkedBlockingQueue<String> rawLibQueue;
    private final LinkedBlockingQueue<JsLibrary> libraryQueue;
    private volatile boolean isTerminated = false;

    public JavaLibraryExtractor(LinkedBlockingQueue<String> rawLibQueue,
                                LinkedBlockingQueue<JsLibrary> libraryQueue) {
        this.rawLibQueue = rawLibQueue;
        this.libraryQueue = libraryQueue;
    }

    public void run() {
        while (!isTerminated) {
            try {
                String jsLibString = rawLibQueue.take();
                if (jsLibString.contains("js")) {
                    jsLibString = jsLibString.substring(jsLibString.lastIndexOf("/") + 1);
                }
                libraryQueue.add(new JsLibrary(jsLibString));
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void setTerminated(boolean terminated) {
        this.isTerminated = terminated;
    }
}