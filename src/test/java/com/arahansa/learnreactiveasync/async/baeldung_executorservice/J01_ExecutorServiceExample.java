package com.arahansa.learnreactiveasync.async.baeldung_executorservice;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 익스큐터 서비스 튜토리얼
 * http://www.baeldung.com/java-executor-service-tutorial
 * 관련 소스
 * https://github.com/eugenp/tutorials/tree/master/core-java-concurrency
 */
public class J01_ExecutorServiceExample {

    @Test
    public void executorServiceExample() throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // ExecutorService 는 인터페이스고 ThreadPoolExecutor  는 여러개의 설정을 할 수 있다.
        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        // ExecutorService
        /**
         * 익스큐터서비스는 러너블고 콜러블 태스크를 실행할 수 있다.
         */
        Runnable runnableTask = () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
                System.out.println("러너블");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Callable<String> callableTask = () -> {
            TimeUnit.MILLISECONDS.sleep(300);
            return "Task's execution";
        };

        List<Callable<String>> callableTasks = new ArrayList<>();
        callableTasks.add(callableTask);
        callableTasks.add(callableTask);
        callableTasks.add(callableTask);

        // execute() 메서드는 void 이고 태스크의 실행에 대한 결과를 받을 가능성을 주진 않는다.
        executorService.execute(runnableTask);

        // submit() 은 Future 의 결과를 리턴하는데 Executor Service 에게 Callable 이나 Runnable Task 를 제출한다.
        final Future<String> submit = executorService.submit(callableTask);
        final String s = submit.get();
        System.out.println(s);

        // Invoke Any 는 ES 에 콜렉션 태스크를 할당하고 각각 실행되게 한다.. 그리고 성공한 실행이 있다면 하나의 실행결과를 리턴한다.
        final String s1 = executorService.invokeAny(callableTasks);
        System.out.println("invokeAny result : "+s1);

        // invoke All 은 모든 결과를 받는다.
        final List<Future<String>> futures = executorService.invokeAll(callableTasks);
        futures.stream().forEach(x->{
            try {
                System.out.println("invokeAll : "+x.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        // 4. ExecutorService 를 끄기

        // shotdown 메서드는 ES의 즉시 종료를 유발하지 않는다. 모든 스레드작업이 실수한 이후에 shutdown 된다.
        executorService.shutdown();

        // shutdownNow는 즉시. 하지만 모든 스레드가 즉시 종료되는 것을 보장하진 ㅇ낳는다.
        final List<Runnable> runnables = executorService.shutdownNow();
        System.out.println("남은 러너블 :"+runnables.size());

        // 오라클이 추천하는 방식은 awaitTermination() 메서드의 사용

        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)){
                executorService.shutdownNow();
            }
        }catch(InterruptedException e){
            executorService.shutdownNow();
        }

        // 05. 퓨처
        executorService = Executors.newFixedThreadPool(10);
        Future<String> future = executorService.submit(callableTask);
        String result = null;
        try {
            result = future.get();
            System.out.println("future example :"+result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //  작업이 길면 TimeoutException  예외가 떨어진다.
        result = future.get(200, TimeUnit.MILLISECONDS);
        System.out.println("future get 2 :"+result);

        final boolean cancel = future.cancel(true);
        System.out.println("cancel : "+cancel);
        System.out.println("isCancelled : "+future.isCancelled());



        // 6. 스케쥴익스큐터서비스 인터페이스
        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        // 일정 시간 이후에 이 스케쥴 실해오딤.
        Future<String> resultFuture = es.schedule(callableTask, 1, TimeUnit.SECONDS);
        // scheduleAtFixedRate() 메서드는 일정 시간 이후에 주기적으로 task 를 실행하게 함.
        // scheduleWithFixedDelay는 작업간의 딜레이
        final ScheduledFuture<?> scheduledFuture = es.scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);
        final ScheduledFuture<?> scheduledFuture1 = es.scheduleWithFixedDelay(runnableTask, 100, 150, TimeUnit.MILLISECONDS);

        // 7. ExecutorService vs Fork/Join
        // 자바 7 이후에 많은 개발자들은 fork/join 프레임워크에 의해서 ExecutorService 가 대체될 것이라고 결정하였지만. 언제나 올바른 결정은 아니다.
        //  The best use case for ExecutorService is the processing of independent tasks, such as transactions or requests according to the scheme “one thread for one task.”
        // 독립적인 태스크를 실행하는데 좋다. 트랜잭션이나 scheme 에 따른 요청을 하는 것같은..


    }
}
