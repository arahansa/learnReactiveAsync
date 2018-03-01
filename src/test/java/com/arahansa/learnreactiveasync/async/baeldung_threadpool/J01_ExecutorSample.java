package com.arahansa.learnreactiveasync.async.baeldung_threadpool;

import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class J01_ExecutorSample {

    // http://www.baeldung.com/thread-pool-java-and-guava

    /**
     * Executor 인터페이스는 하나의 단일 execute 메서드를 가지고서 실행을 위하여 Runnable 인스턴스를 submit 함.
     * @throws Exception
     */
    @Test
    public void executorsample() throws Exception{
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> System.out.println("Hello world"));
    }

    /**
     * ExecutorService 를 통해서 task 를 submit 하고 반환된 Future 의 GET를 통해 기다림.
     * @throws Exception
     */
    @Test
    public void executorServiceExample() throws Exception{

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<String> future = executorService.submit(() -> "Hello World");
// some operations

        String result = future.get();
        System.out.println("result : "+result);
    }




}
