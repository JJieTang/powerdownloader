package com.powernode.core;

import com.powernode.constant.Constant;
import com.powernode.util.HttpUtils;
import com.powernode.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class DownloaderTask implements Callable<Boolean> {

    private String url;

    private long startPos;

    private long endPos;

    //to show which part is downloading
    private int part;

    private CountDownLatch countDownLatch;

    public DownloaderTask(String url, long startPos, long endPos, int part, CountDownLatch countDownLatch) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws IOException {

        String httpFileName = HttpUtils.getHttpFileName(url);

        httpFileName = httpFileName + ".temp" + part;

        httpFileName = Constant.PATH + httpFileName;

        HttpURLConnection httpURLConnection = HttpUtils.getHttpURLConnection(url, startPos, endPos);

        try (
                InputStream input = httpURLConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(input);
                RandomAccessFile accessFile = new RandomAccessFile(httpFileName, "rw");
                ) {
            byte[] buffer = new byte[Constant.BYTE_SIZE];
            int len = -1;
            while ((len = bis.read(buffer)) != -1){
                DownloadInfoThread.downSize.add(len);
                accessFile.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            LogUtils.error("Cannot find file {}", url);
            return false;
        } catch (Exception e){
            LogUtils.error("There's an error with the download.");
            return false;
        } finally {
            httpURLConnection.disconnect();
            countDownLatch.countDown();
        }

        return true;
    }
}
