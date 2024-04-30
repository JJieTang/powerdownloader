package com.powernode.core;

import com.powernode.constant.Constant;
import com.powernode.util.FileUtils;
import com.powernode.util.HttpUtils;
import com.powernode.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;

/*
    downloader
 */
public class Downloader {

    public ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    public ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(Constant.THREAD_NUM, Constant.THREAD_NUM, 0, TimeUnit.MINUTES, new ArrayBlockingQueue<>(Constant.THREAD_NUM));
    private CountDownLatch countDownLatch = new CountDownLatch(Constant.THREAD_NUM);// make sure all threads finished

    public void download(String url){
        //get file name
        String httpFileName = HttpUtils.getHttpFileName(url);
        //get file download path
        httpFileName = Constant.PATH + httpFileName;

        long localFileLength = FileUtils.getFileContentLength(httpFileName);

        //get http connection
        HttpURLConnection httpURLConnection = null;

        DownloadInfoThread downloadInfoThread = null;
        try {
            httpURLConnection = HttpUtils.getHttpURLConnection(url);

            //get file size
            int contentLength = httpURLConnection.getContentLength();

            if (contentLength <= localFileLength){
                LogUtils.info("{}Already downloaded, no need to repeat.",httpFileName);
                return;
            }

            //new a object to finish downloading
            downloadInfoThread = new DownloadInfoThread(contentLength);

            scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread, 1, 1, TimeUnit.SECONDS);

            ArrayList<Future> list = new ArrayList<>();
            split(url, list);

            /*make sure all the threads is finished
            list.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });*/
            countDownLatch.await();//use this to make sure all finished

            if (merge(httpFileName)){
                clearTemp(httpFileName);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            System.out.print("\r");
            System.out.print("Finished download.");
            //disconnect http connection
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            scheduledExecutorService.shutdownNow();

            poolExecutor.shutdown();

        }
        }

    public void split(String url, ArrayList<Future> futureList){
        try {
            long contentLength = HttpUtils.getHttpFileContentLength(url);

            long size = contentLength / Constant.THREAD_NUM;

            for (int i=0; i<Constant.THREAD_NUM; i++){
                long startPos = i * size;

                long endPos;

                if (i == Constant.THREAD_NUM - 1){
                    endPos = 0;
                } else {
                    endPos = startPos + size;
                }

                if (startPos != 0){
                    startPos++;
                }

                DownloaderTask downloaderTask = new DownloaderTask(url, startPos, endPos, i, countDownLatch);

                //submit task
                Future<Boolean> future = poolExecutor.submit(downloaderTask);

                futureList.add(future);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean merge(String fileName){
        LogUtils.info("Start merge file: {}", fileName);
        byte[] buffer = new byte[Constant.BYTE_SIZE];
        int len = -1;
        try (RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw")){
            for (int i=0; i<Constant.THREAD_NUM; i++){
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName + ".temp" + i))) {
                    while ( (len = bis.read(buffer)) != -1){
                        accessFile.write(buffer, 0, len);}
                } }
            LogUtils.info("Finished merge file {}", fileName);
            } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
        }

    public boolean clearTemp(String fileName){
        for (int i=0; i<Constant.THREAD_NUM; i++){
            File file = new File(fileName + ".temp" + i);
            file.delete();
        }
        return true;
    }

}
