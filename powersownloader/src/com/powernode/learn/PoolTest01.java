package com.powernode.learn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolTest01 {
    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor threadPool = null;

        try {
            // new a thread pool object
            threadPool = new ThreadPoolExecutor(2,3,1,
                    TimeUnit.MINUTES,new ArrayBlockingQueue<>(2)
                /*r -> {
                    Thread t = new Thread();
                    return t;
                }*/
            );

            // new a task
            Runnable r = () -> System.out.println(Thread.currentThread().getName());

            //submit task to the thread pool
            //threadPool.execute(r);
            for (int i=0; i < 5; i++){
                threadPool.execute(r);
            }

        } finally {
            if (threadPool != null){
                //mild, still would finish the tasks in the queue
                //threadPool.shutdown();
                // immediately shut down all
                //threadPool.shutdownNow();
                threadPool.shutdown();
                if(!threadPool.awaitTermination(1, TimeUnit.MINUTES)){
                    threadPool.shutdownNow();
                }
            }

        }

    }
}
