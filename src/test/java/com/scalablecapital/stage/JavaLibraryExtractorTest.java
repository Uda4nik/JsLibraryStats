package com.scalablecapital.stage;

import com.scalablecapital.domain.JsLibrary;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaLibraryExtractorTest {
    JavaLibraryExtractor cut;
    LinkedBlockingQueue<String> rawLibQueue = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<JsLibrary> libsQueue = new LinkedBlockingQueue<>();

    @BeforeMethod
    public void setUp() throws Exception {
        cut = new JavaLibraryExtractor(rawLibQueue, libsQueue);
    }

    @Test(timeOut = 100)
    public void createsJsLibWithoutPath() throws Exception {
        //given
        rawLibQueue.add("http://www.speedtest.pl/js/jquery-1.8.0.min.js");
        //when
        Thread thread = new Thread(cut);
        thread.start();
        //then
        assertThat(libsQueue.take()).isEqualTo(new JsLibrary("jquery-1.8.0.min.js"));
        cut.setTerminated(true);
        thread.interrupt();
    }
}