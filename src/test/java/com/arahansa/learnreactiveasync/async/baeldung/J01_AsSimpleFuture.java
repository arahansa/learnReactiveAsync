package com.arahansa.learnreactiveasync.async.baeldung;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class J01_AsSimpleFuture {

    public Future<String>  calculateAsync() throws InterruptedException{
        CompletableFuture<String> completablefuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(()->{
            Thread.sleep(2000);
            completablefuture.complete("Hello");
            return null;
        });

        return completablefuture;
    }

    @Test
    public void simpleFuture() throws Exception{
        System.out.println("Coding..");
        Future<String> completableFuture = calculateAsync();
        System.out.println(completableFuture.get()); // block
    }
}
