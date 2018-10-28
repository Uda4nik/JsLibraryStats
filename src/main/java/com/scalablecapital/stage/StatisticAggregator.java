package com.scalablecapital.stage;

import com.scalablecapital.domain.JsLibrary;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;

/*
* Currently works in one thread cause doesn't do much work
**/
public class StatisticAggregator implements Runnable {

    private final LinkedBlockingQueue<JsLibrary> libraryQueue;
    private final Map<JsLibrary, Integer> state = new ConcurrentHashMap<>();
    private volatile boolean isTerminated = false;

    public StatisticAggregator(LinkedBlockingQueue<JsLibrary> libraryQueue) {
        this.libraryQueue = libraryQueue;
    }

    public void run() {
        while (!isTerminated) {
            try {
                JsLibrary library = libraryQueue.take();
                state.compute(library, (k, v) -> Objects.isNull(v) ? 1 : v + 1);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }

    public List<String> getTop(int limit) {
        return state.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(reverseOrder()))
                .limit(limit)
                .map(e -> e.getKey().toString() + " => " + e.getValue())
                .collect(Collectors.toList());
    }

    public void setTerminated(boolean terminated) {
        isTerminated = terminated;
    }
}
