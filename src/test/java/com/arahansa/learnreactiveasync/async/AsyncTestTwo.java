package com.arahansa.learnreactiveasync.async;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

// https://www.youtube.com/watch?v=5KttCnoWLhs

public class AsyncTestTwo {

    static class Result{
        String s;
        public Result(String s) {this.s = s;}
        @Override
        public String toString() {return "Result{ s='" + s + '\'' + '}';}
    }

    @Test
    public void asyncTest() throws Exception{
        Map map = new HashMap<>();

        Thread thread = new Thread(() -> {
            Result result = getApi();
            map.put("API", result);
            synchronized (map){
                map.notify();
            }
        });

        thread.start();
        if(map.get("API")==null){
            synchronized (map){
                map.wait();
            }
        }
        System.out.println(map.get("API"));
    }

    private Result getApi() {
        return new Result("s");
    }
}
