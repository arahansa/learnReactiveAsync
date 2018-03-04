package com.arahansa.learnreactiveasync.async.baeldung_completablefuture;

import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * http://www.baeldung.com/java-completablefuture
 *
 */
public class J01_CompletableFuture {
    /**
     *
     비동기 연산은 다루기 어렵지만 보통 우리는 어떤 단계들의 연속적인 연산으로 이해하기를 바래
     근데 이러한 비동기 연산의 경우에는, 콜백으로 나타나지는 액션들이 여기저기 흩뿌려진것처럼 보여지기도하거나 깊게 중첩된 것처럼 보이기도 합니다.
     우리가 에러 핸들링을 할때 이것은 좀 더 나빠지죠

     Future 인터페이스는 Java5 에 추가되어, 비동기 연산의 결과를 다루는데 사용됩니다.
     그러나 이것은 이러한 연산끼리 조합하거나 에러를 다루는 메서드를 제공하지 않습니다.

     자바 8에서 CompletableFuture 클래스가 소개되었는데
     Future 인터페이스와 함께, 이것은 CompletionStage 인터페이스를 구현하였습니다.
     이 인터페이스는 비동기적인 연산의 단계를 정의하고 다른 단계와 조합합니다.

     CompletableFuture 는 동시에 블록을 만들기도하며, 조합, 구성, 비동기 연산과 에러를 다루는 50가지의 다른 메서드를 가진 프레임워크이기도 합니다.
     이런 많은 API 에 압도될 것같지만 사용처를 구별하면 꽤 분명해집니다.
     */

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

    /**
     대안시나리오로, Future 의 실행을 취소하길 원할 수도 있습니다.
     우리가 결과를 찾는 것을 다루지 않고, 비동기 실행을 같이 취소하기로 할려고 한다면
     이것은 Future 의 cancel 메서드를 통해서 이뤄질 수 있습니다.
     이 메서드는 boolean 아규먼트 mayInterruptIfRunning 을 받습니다.
     CompletableFuture 를 위한 조정작업을 하는데 인터럽트가 사용되지 않기 때문에, 이러한 CompletableFuture 의 경우 어떠한 효과도 없습니다.

     여기 비동기메서드의 수정된 버젼을 봅시다.
     */
    public Future<String> calculateAsyncWithCancellation() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500);
            completableFuture.cancel(false);
            return null;
        });

        return completableFuture;
    }

    @Test(expected = CancellationException.class)
    public void cancelation() throws Exception{
        Future<String> future = calculateAsyncWithCancellation();
        future.get(); // CancellationException
    }


    /**
     4. 캡슐화된 연산 로직으로서의 CompletableFuture
     위의 코드는 우리에게 동시성 실행의 메커니즘을 보여주고 있지만, 보일러 플레이트 코드를 생략하고 단순히 어떤 코드를 비동기적으로 실행하고 싶다면?

     static Method들은 runAsync 과 supplyAsync 가 CompletableFuture  인스턴스를 만드는데 도움을 줄 수가 있습니다.

     Runnable 과 Suuplier 는 함수적 인터페이스로 그들의 인스턴스를 람다 표현식에 전달할 수 있습니다.
     Runnable 인터페이스는 같은 오래된 인터페이스로 스레드에서 사용되고 값을 리턴하는 것을 허용하지 않습니다.
     */

    @Test
    public void runnable() throws Exception{
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
        assertEquals("Hello", future.get());
    }

    @Test
    public void thenApply() throws Exception{
        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> "Hello");

        CompletableFuture<String> future = completableFuture
                .thenApply(s -> s + " World");

        assertEquals("Hello World", future.get());


        // Consumer 로 하고 싶다면..
        CompletableFuture<Void> future2 = completableFuture
                .thenAccept(s -> System.out.println("Computation returned: " + s));
        future2.get();

        // Runnable 로 하고 싶다면..
        CompletableFuture<Void> future3 = completableFuture
                .thenRun(() -> System.out.println("Computation finished."));

        future3.get();
    }

    // 6. Future 의 조합
    @Test
    public void composeFuture() throws Exception{
        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));

        assertEquals("Hello World", completableFuture.get());


        // thenCompose (flatMap) method receives a function that returns another object of the same type
        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCombine(CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> s1 + s2);

        assertEquals("Hello World", completableFuture2.get());


        // 비슷한 예
        CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
                .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"),
                        (s1, s2) -> System.out.println(s1 + s2));
        System.out.println("future get :"+future.getNow(true));
    }
    
    @Test
    public void runningMultipleFutureParallel() throws Exception{
        CompletableFuture<String> future1
                = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2
                = CompletableFuture.supplyAsync(() -> "Beautiful");
        CompletableFuture<String> future3
                = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<Void> combinedFuture
                = CompletableFuture.allOf(future1, future2, future3);

        combinedFuture.get();

        assertTrue(future1.isDone());
        assertTrue(future2.isDone());
        assertTrue(future3.isDone());

        //CompletableFuture.allOf() is a CompletableFuture<Void>. 의 한계가 있어서 Java8 에서는 CompletableFuture.join() 이 존재하게 된다.
        String combined = Stream.of(future1, future2, future3)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));

        assertEquals("Hello Beautiful World", combined);
    }

    @Test
    public void handlingErrors() throws Exception{
        String name = null;

        CompletableFuture<String> completableFuture
                =  CompletableFuture.supplyAsync(() -> {
            if (name == null) {
                throw new RuntimeException("Computation error!");
            }
            return "Hello, " + name;
        }).handle((s, t) -> s != null ? s : "Hello, Stranger!");

        assertEquals("Hello, Stranger!", completableFuture.get());

    }

    @Test(expected = RuntimeException.class)
    public void completableError() throws Exception{
        CompletableFuture<String> completableFuture2 = new CompletableFuture<>();
        completableFuture2.completeExceptionally(new RuntimeException("Calculation failed!"));
        completableFuture2.get(); // ExecutionException
    }
}
