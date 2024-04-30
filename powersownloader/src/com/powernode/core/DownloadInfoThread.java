package com.powernode.core;

import com.powernode.constant.Constant;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.LongAdder;

public class DownloadInfoThread implements Runnable{

    //file length + atomic class
    private long httpFileContentLength;

    public static LongAdder finishedSize = new LongAdder();

    public static volatile LongAdder downSize = new LongAdder();

    public double prevSize;

    public DownloadInfoThread(long httpFileContentLength){
        this.httpFileContentLength = httpFileContentLength;
    }

    public void run(){
        //compute file size unit:MB
        String httpFileSize = String.format("%2f", httpFileContentLength / Constant.MB);

        //compute download speed unit:KB
        int speed = (int) ((downSize.doubleValue() - prevSize) / 1024d);
        prevSize = downSize.doubleValue();

        //compute left content length
        double remainSize = httpFileContentLength - downSize.doubleValue();

        //compute time left
        String remainTime = String.format("%.1f", remainSize / 1024d / speed);

        if ("Infinity".equalsIgnoreCase(remainTime)){
            remainTime = "-";
        }

        String currentFileSize = String.format("%.2f", downSize.doubleValue() / Constant.MB);

        String downInfo = String.format("Already downloaded %smb/%smb, speed %skb/s, remain time %ss",
                currentFileSize, httpFileSize, speed, remainTime);

        System.out.print("\r");
        System.out.println(downInfo);

    }
}
