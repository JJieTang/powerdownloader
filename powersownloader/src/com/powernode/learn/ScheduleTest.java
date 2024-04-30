package com.powernode.learn;
/*
    ScheduleExecutorService
 */


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleTest {
    public static void main(String[] args) {
        ScheduledExecutorService s = Executors.newScheduledThreadPool(1);
        //delay for 2 seconds, and then start to repeat each 3 seconds
        //but if the command cost time more than 3 seconds, it would not wait for extra 3 seconds, if you want to use in that way should use scheduledWithFixedDelay
        s.scheduleAtFixedRate(()->{System.out.println(System.currentTimeMillis());}, 2, 3, TimeUnit.SECONDS);
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public static void schedule(String[] args) {
        ScheduledExecutorService s = Executors.newScheduledThreadPool(1);//how many thread to have

        //delay 2 second and print the thread name
        s.schedule(()->System.out.println(Thread.currentThread().getName()), 2, TimeUnit.SECONDS);

        s.shutdown();
    }
}
