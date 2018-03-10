package com.arahansa.learnreactiveasync.async.baeldung_forkjoin;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class J01_ForkJoin {

    /**
     *
     * http://www.baeldung.com/java-fork-join

     포크/조인 프레임워크는 자바7에 나타납니다.
     모든 프로세서를 사용해서 병렬 프로그래밍을 하게 해주는데

     fork 라고해서 태스크를 작은 독립적인 subtask 로 나누어 비동기적으로 실행한다.
     그 이후에 join 부분이 실행되고, 서브태스크들의 결과를 회귀적으로 합쳐져서 하나의 결과가 되거나
     이러한 경우 void 가 리턴되기도 한다. 서브태스크들이 실행될 때까지 프로그램은 기다린다.

     효과적인 병렬 실행을 제공하기 위하여 포크조인 프레임워크는 포크조인풀이라는 스레드들의 풀을 사용한다.
     이것은 ForkJoinWorkerThread 의 스레드들을 관리한다.



     */
    @Test
    public void forkJoin() throws Exception{

        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool = new ForkJoinPool(2);

        // ForkJoinPool forkJoinPool = PoolUtil.forkJoinPool;
    }

    /**
     포크조인태스크는 ForkJoinPool 내부에서 실행되는 태스크들을 위한 기본적인 타입인데
     실제로, 이것의 하위 클래스중의 한 가지를 상속해야 한다
     void task들을 위한 RecursiveAction과 값을 리턴하는 RecursiveTask<V>가 있음
     이것들은 task의 로직이 정의된 추상메소드 compute()를 가지고 있다

     3.1 RecursiveAction – An Example
     */


    @Slf4j
    static class CustomRecursiveAction extends RecursiveAction {

        private String workload = "";
        private static final int THRESHOLD = 4;


        public CustomRecursiveAction(String workload) {
            this.workload = workload;
        }

        @Override
        protected void compute() {
            if (workload.length() > THRESHOLD) {
                ForkJoinTask.invokeAll(createSubtasks());
            } else {
                processing(workload);
            }
        }

        private List<CustomRecursiveAction> createSubtasks() {
            List<CustomRecursiveAction> subtasks = new ArrayList<>();

            String partOne = workload.substring(0, workload.length() / 2);
            String partTwo = workload.substring(workload.length() / 2, workload.length());

            subtasks.add(new CustomRecursiveAction(partOne));
            subtasks.add(new CustomRecursiveAction(partTwo));

            return subtasks;
        }

        private void processing(String work) {
            String result = work.toUpperCase();
            log.info("This result - (" + result + ") - was processed by "
                    + Thread.currentThread().getName());
        }
    }

    @Test
    public void forkJoinTask_V() throws Exception{
        new CustomRecursiveAction("hello world").compute();

    }

    /**
     * RecursiveTask<V>
     */

    static class CustomRecursiveTask extends RecursiveTask<Integer> {
        private int[] arr;

        private static final int THRESHOLD = 20;

        public CustomRecursiveTask(int[] arr) {
            this.arr = arr;
        }

        @Override
        protected Integer compute() {
            if (arr.length > THRESHOLD) {
                return ForkJoinTask.invokeAll(createSubtasks())
                        .stream()
                        .mapToInt(ForkJoinTask::join)
                        .sum();
            } else {
                return processing(arr);
            }
        }

        private Collection<CustomRecursiveTask> createSubtasks() {
            List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
            dividedTasks.add(new CustomRecursiveTask(
                    Arrays.copyOfRange(arr, 0, arr.length / 2)));
            dividedTasks.add(new CustomRecursiveTask(
                    Arrays.copyOfRange(arr, arr.length / 2, arr.length)));
            return dividedTasks;
        }

        private Integer processing(int[] arr) {
            return Arrays.stream(arr)
                    .filter(a -> a > 10 && a < 27)
                    .map(a -> a * 10)
                    .sum();
        }
    }

    @Test
    public void executeForkJoinTaskV() throws Exception{
        Integer compute = new CustomRecursiveTask(new int[]{11,12,13}).compute();
        System.out.println("compute :"+compute);
    }

    // Task들을 ForkJoinPool 에 서브밋하기

    /**
     task들을 스레들에 제출하기 위하여 몇가지 접근방법이 사용됩니다.
     submit() 이나 execute() 메소드같은 방법들이죠  이것들의 사용방법은 같습니다.
     * @throws Exception
     */
    @Test
    public void test() throws Exception{
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        CustomRecursiveTask customRecursiveTask = new CustomRecursiveTask(new int[]{11, 12, 13});
        forkJoinPool.execute(customRecursiveTask);

        int result = customRecursiveTask.join();
        System.out.println("result: "+ result);

        //
        result = forkJoinPool.invoke(customRecursiveTask);
        System.out.println("result: "+ result);
    }




}
