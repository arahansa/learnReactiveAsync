package com.arahansa.learnreactiveasync.async.baeldung_threadpool;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.assertj.core.util.Sets;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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


    /**
     * ThreadPoolExecutor 는 확장가능한 스레드풀 구현체로 다양한 파라미터와 훅을 가지고 있어서 튜닝이 가능
     * 주요 설정 파라미터는 corePoolSize 와 maximumPoolSize 와 keepAliveTime 입니다.
     */

    /**
     * newFixedThreadPool  는 고정풀
     * @throws Exception
     */
    @Test
    public void threadPoolExecutorTest() throws Exception{
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        executor.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        executor.submit(() -> {
            Thread.sleep(1000);
            return null;
        });

        assertEquals(2, executor.getPoolSize());
        assertEquals(1, executor.getQueue().size());
    }

    /**
     * 여기서는  newCachedThreadPool 이 사용되었다.
     * queue size 는 언제나 0 이다. 왜냐하면 SynchronousQueue 인스턴스가 사용되기 때문이다.
     * @throws Exception
     */
    @Test
    public void paramvalues() throws Exception{
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        executor.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        executor.submit(() -> {
            Thread.sleep(1000);
            return null;
        });

        assertEquals(3, executor.getPoolSize());
        assertEquals(0, executor.getQueue().size());
    }

    /**
     * SynchronousQueue 에서는 insert  와 remove 작업이 동시대에 일어납니다. 그러므로 queue 는 어떤 것도 포함하지 않습니다.
     * Executors.newSingleThraedExecutor() API 는 ThreadpoolExecutor 의 다른 형태를 만들어냅니다.
     *
     * single thread executor 는 이벤트 루프에 이상적인 형태입니다.
     * corePoolsize 와 maximumPoolSize 파라미터는 같고 1 입니다. 그리고 keepAliveTime 는 0입니다.
     *
     *
     *추가적으로 ThreadPoolExecutor 는 immutable wrapper로 decorated  됩니다. 그래서 생성 이후 다시 설정 될 수 없습니다.
     * 이것이 우리가 ThreadPoolExecutor 로 cast 할 수 없는 이유입니다.
     * @throws Exception
     */
    @Test
    public void Exeuctors_newSingleThreadExecutior() throws Exception{
        AtomicInteger counter = new AtomicInteger();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            counter.set(1);
        });
        executor.submit(() -> {
            counter.compareAndSet(1, 2);
        });

        System.out.println("counter :"+counter);
        Thread.sleep(1000);
        System.out.println("counter :"+counter);
    }

    // 3.3 스케쥴된 스레드 풀 Executor

    /**
     * ScheduledThreadPoolExecutor  는 ThreadPoolExecutor 를 상속하고 또한
     * 몇가지 추가적인 메서드와 함께  ScheduledExecutorService를 구현합니다.
     *
     * 명시된 딜레이 이후에 schedule Method 는
     */

    @Test
    public void scheduledExecutorService() throws Exception{
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        executor.schedule(() -> {
            System.out.println("Hello World");
        }, 500, TimeUnit.MILLISECONDS);
        Thread.sleep(1000);
    }

    /**
     * future.cancit 메서드를 통해서 멈추기로 함
     * @throws Exception
     */
    @Test
    public void repeatTask() throws Exception{
        CountDownLatch lock = new CountDownLatch(3);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            System.out.println("Hello World");
            lock.countDown();
        }, 500, 100, TimeUnit.MILLISECONDS);

        lock.await(1000, TimeUnit.MILLISECONDS);
        future.cancel(true);
    }

    // 3.4 ForkJoinPool
    /**
     * ForkJoinPool 은 포크조인프레임워크의 중요 부분으로 자바7에 소개되었다.
     * 회귀알고리즘의 여러개의 태스크를 spawning 하는 보통의 문제들을 해결하였다.
     * 간단한 ThreadPoolExecutor 를 사용하는 것으로 인하여 당신은 스레드를 빠르게 소모할 것이고
     * 각각의 task나 subtask 는 그들자신의 실행할 스레드를 요청할 것이다.
     *
     * 포크 조인 프레임워크에서 어떤 task 건간에 서브 태스크의 number를 만들고 그들의 완료를 join 메서드를 통해서 기다린다.
     * 포크조인프레임워크의 이득은 각각의 task 나 subtask 등에 대하여 새로운 스레드를 만들지 않고
     * Work Stealing 알고리즘을 대신에 구현하는 것이다.
     *
     * ForkJoinPool 을 사용하는 간단한 예제를 살펴보자.
    */

    static class TreeNode {

        int value;

        Set<TreeNode> children;

        TreeNode(int value, TreeNode... children) {
            this.value = value;
            this.children = Sets.newHashSet(Arrays.asList(children));
        }
    }

    /**
     만약 트리의 값들은 병렬적으로 모두 합하고 싶다면 우리는 RecursiveTask<Integer> 인퍼이스를 사용하면 됩니다.
     각각의 Task 는 각각 자신의 노드를 받고 그것의 값들을 자신의 자식들의 합한 값에 더합니다.

     자식들의 값을 계산하기 위하여 task 구현체는 다음을 따르게 됩니다.
     자식들의 집합을 stream 하고
     이 스트림을 map 하고 새로운 Counting Task를 각각의 요소를 위하여 만들고, forking함으로써 각각의 서브태스크들을 실행합니다.
     각각의 포크된 태스크들에 대하여 조인 메서드를 실행함으로써 결과들을 모으고
     Collectors.summingInt 콜렉터를 사용하여 결과들을 합합니다.
     */

    static class CountingTask extends RecursiveTask<Integer> {

        private final TreeNode node;

        public CountingTask(TreeNode node) {
            this.node = node;
        }

        @Override
        protected Integer compute() {
            return node.value + node.children.stream()
                    .map(childNode -> new CountingTask(childNode).fork())
                    .collect(Collectors.summingInt(ForkJoinTask::join));
        }
    }

    @Test
    public void computeTreeNode() throws Exception{
        TreeNode tree = new TreeNode(5,
                new TreeNode(3), new TreeNode(2,
                new TreeNode(2), new TreeNode(8)));

        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        int sum = forkJoinPool.invoke(new CountingTask(tree));
        System.out.println("sum :"+sum);
    }


    /**
     * MoreExecutors.directExecutor() 를 통해서 Executor 를 받는 모습
     * MoreExecutors.newDirectExecutorService() 를 통해서 모든 호출하다  full-fledged executor service implementation를 받을 수 있다.
     * @throws Exception
     */
    @Test
    public void guavaTest() throws Exception{
        Executor executor = MoreExecutors.directExecutor();

        AtomicBoolean executed = new AtomicBoolean();

        executor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executed.set(true);
        });

        assertTrue(executed.get());
    }

    // 4.3 Exiting Executor Services
    /**
     *
     또 다른 주요 문제는 버쳘 머신을 끄는 것입니다. 언제? 스레드풀이 여전히 태스크들을 실행하고 있는 도중에.
     심지어 취소 메커니즘이 있어도, executor 서비스가 셧다운 될 때, 태스크가 깔끔하게 그들의 작업을 멈춘다는 보장이 없습니다.

     이것은 JVM이 무한대로 hang 걸리게 할 수도 있습니다.
     이 문제를 해결하기 위해 구아바는 존재하는 executor services 들에 대한 가족들을 소개하였습니다.
     그것들은 데몬스레드에 기초로 하여 JVM 과 함께 그것들을 종료합니다.

     이러한 서비스들은 또한 셧다운훅을 추가합니다. Runtime.getRuntime().addShutdownHJook() 메서드를 통해서 말이죠.
     다음의 예에 우리는 무한루프를 포함하고 있는 task를 submit 하고 executorservice를 사요하기전에 100밀리세컨드를 설정한 executorservice를 사용할 것입니다.
     VM 종료를 위해서 기다릴 거구요.
     exitingExecutorService 이 없이는 이러한 작업은 vm 이 hang 이 무한대로 걸리게할 수 있습니다.
     */

    @Test
    public void existingExecutorService() throws Exception{
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        ExecutorService executorService =
                MoreExecutors.getExitingExecutorService(executor,
                        3000, TimeUnit.MILLISECONDS);

        executorService.submit(() -> {
            while (true) {
                Thread.sleep(1000);
                System.out.println("ha?");
            }
        });
        System.out.println("hello...");

    }

    /**
     * 4.4. Listening Decorators
     데코레이터를 사용하는 것은 당신이 ExecutorService 를 wrapping 하고, task 서브미션에 따라서 간단한 Future 인스턴스가 아닌 ListenableFuture 인스턴스를 받게 해줍니다.
     ListenableFuture 인터페이스는 Future를 상속하고 addListener 라는 추가적은 메서드를 가지고 있습니다.
     이 메서드는 future 완료 시에 호출될 수 있는 listener 를 추가하게 해줍니다.

     당신은 거의 ListenableFuture.addListener()메서드를 직접사용할 일이 없을 겁니다.
     그러나 이것은 Futures 유틸리티 클래스들 의 도움 메서드들중 정수입니다.
     예를 들어서 Futures.allAsList()메서드에서, 당신은 몇가지의 ListenableFuture 인스턴스를 조합할 수 있습니다.
     *
     */
    @Test
    public void listeningDecorators() throws Exception{
        ExecutorService executorService = Executors.newCachedThreadPool();
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

        ListenableFuture<String> future1 = listeningExecutorService.submit(() -> "Hello");
        ListenableFuture<String> future2 = listeningExecutorService.submit(() -> "World");

        String greeting = Futures.allAsList(future1, future2).get()
                .stream()
                .collect(Collectors.joining(" "));
        assertEquals("Hello World", greeting);
    }





}
