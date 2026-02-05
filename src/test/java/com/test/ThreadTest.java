package com.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class ThreadTest {

    public static void main(String[] args) throws InterruptedException {
        // LongAdder是Java 8引入的一个用于高并发环境下的计数器类。
        // 与AtomicLong相比，LongAdder在多线程环境下提供了更好的性能。
        // LongAdder内部维护了一个数组，每个线程可以独立地更新数组中的某个元素，
        // 从而减少竞争。当需要获取总数时，将数组中的所有元素相加。
        LongAdder adder = new LongAdder();

        //ExecutorService是一个用于管理线程的接口，
        //创建了一个固定大小为10的线程池。线程池中的线程可以并发地执行提交的任务。
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10000; i++) {
            executor.submit(() -> adder.increment());
        }

        // 方法会停止接受新的任务，但已经提交的任务会继续执行。
        executor.shutdown();

        // 方法会等待所有任务完成，最多等待1分钟。
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // 当前计数器的总和，并打印出来。
        System.out.println("Total increments: " + adder.sum());
    }

}
