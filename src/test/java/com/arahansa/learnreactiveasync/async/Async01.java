package com.arahansa.learnreactiveasync.async;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

// https://www.youtube.com/watch?v=5KttCnoWLhs

public class Async01 {

    static class Result{
        String s;
        public Result(String s) {this.s = s;}
        @Override
        public String toString() {return "Result{ s='" + s + '\'' + '}';}
    }

    @Test
    public void asyncTest() throws Exception{
        Map map = new HashMap<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = getApi();
                map.put("API", result);
            }
        });

        thread.start();
        System.out.println(map.get("API")); // 여기서 NULL
    }

    private Result getApi() {
        return new Result("s");
    }
}
