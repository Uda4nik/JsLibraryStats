package com.scalablecapital.stage;

import com.scalablecapital.domain.JsLibrary;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

public class StatisticAggregatorTest {
    StatisticAggregator cut;
    LinkedBlockingQueue<JsLibrary> queue;

    @BeforeMethod
    public void setUp() throws Exception {
        queue = new LinkedBlockingQueue<>();
        cut = new StatisticAggregator(queue);
    }

    @Test
    public void testName() throws Exception {
        //given
        queue.add(new JsLibrary("jquery-1.8.0.min.js"));
        //when
        Thread thread = new Thread(cut);
        thread.start();
        //then
        List<String> top = new ArrayList<>();
        while (top.isEmpty()) {
            top = cut.getTop(1);
        }
        assertThat(top).containsExactly("jquery-1.8.0.min.js => 1");
        cut.setTerminated(true);
        thread.interrupt();
    }
}