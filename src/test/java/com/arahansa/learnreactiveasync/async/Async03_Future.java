package com.arahansa.learnreactiveasync.async;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Async03_Future {

    @Test
    public void futureTest() throws Exception{
        Callable<Async02.Result> callable = new Callable<Async02.Result>() {
            @Override
            public Async02.Result call() throws Exception {
                return Async02.getApi();
            }
        };

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Async02.Result> future = es.submit(callable);

        Async02.Result result = future.get(); // block 해제
        System.out.println(result);
        es.shutdown();
        // https://www.youtube.com/watch?v=5KttCnoWLhs 을 30분부터 다시 보기로 한다
    }

}
