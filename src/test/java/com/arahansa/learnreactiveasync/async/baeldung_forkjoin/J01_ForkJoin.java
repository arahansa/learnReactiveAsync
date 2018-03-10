package com.arahansa.learnreactiveasync.async.baeldung_forkjoin;

import org.junit.Test;

public class J01_ForkJoin {

    /**
     *
     * http://www.baeldung.com/java-fork-join

     The fork/join framework was presented in Java 7.
     It provides tools to help speed up parallel processing by attempting to use all available processor cores –
     which is accomplished through a divide and conquer approach.

     In practice, this means that the framework first “forks”, recursively breaking the task into smaller independent subtasks
     until they are simple enough to be executed asynchronously.

     After that, the “join” part begins, in which results of all subtasks are recursively joined into a single result,
     or in the case of a task which returns void, the program simply waits until every subtask is executed.

     To provide effective parallel execution, the fork/join framework uses a pool of threads called the ForkJoinPool,
     which manages worker threads of type ForkJoinWorkerThread.

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



    }

}
