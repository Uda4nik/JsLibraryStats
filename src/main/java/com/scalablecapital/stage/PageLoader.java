package com.scalablecapital.stage;

import com.scalablecapital.domain.DocumentLoader;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;


/*
 * The class does nothing except creating and submitting IO tasks
 * (get page strings and pass lines with js libs to the next queue)
 * */
public class PageLoader implements Runnable {
    private final Executor ioBoundPool;
    private final LinkedBlockingQueue<String> urlQue;
    private final LinkedBlockingQueue<String> rawLibQue;
    private volatile boolean isTerminated = false;
    private final DocumentLoader documentLoader;

    public PageLoader(Executor threadPool,
                      LinkedBlockingQueue<String> urlQue,
                      LinkedBlockingQueue<String> rawLibQueue,
                      DocumentLoader documentLoader) {
        this.ioBoundPool = threadPool;
        this.urlQue = urlQue;
        this.rawLibQue = rawLibQueue;
        this.documentLoader = documentLoader;
    }

    @Override
    public void run() {
        while (!isTerminated) {
            try {
                String urToLoad = urlQue.take();
                Runnable loadTask = createTask(urToLoad);
                ioBoundPool.execute(loadTask);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }

    private Runnable createTask(String urToLoad) {
        return () -> {
            try {
                Document doc = documentLoader.load(urToLoad);
                Elements libraries = doc.getElementsByTag("script");
                for (Element library : libraries) {
                    if (library.hasAttr("src")) {
                        String libraryName = library.attr("src");
                        rawLibQue.add(libraryName);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error during processing page + " + urToLoad, e);
            }
        };
    }

    public void setTerminated(boolean terminated) {
        this.isTerminated = terminated;
    }
}
