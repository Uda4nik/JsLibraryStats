package com.scalablecapital.stage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import static com.scalablecapital.Main.USER_AGENT;


/*
 * The class does nothing except creating and submitting IO tasks
 * (get page strings and pass lines with js libs to the next queue)
 * */
public class PageLoader implements Runnable {
    private final Executor ioBoundPool;
    private final LinkedBlockingQueue<String> urlQue;
    private final LinkedBlockingQueue<String> rawLibQue;
    private volatile boolean isTerminated = false;

    public PageLoader(Executor ioBoundPool, LinkedBlockingQueue<String> urlQue, LinkedBlockingQueue<String> rawLibQueue) {
        this.ioBoundPool = ioBoundPool;
        this.urlQue = urlQue;
        this.rawLibQue = rawLibQueue;
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
                Document doc = Jsoup.connect(urToLoad).userAgent(USER_AGENT).get();
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
