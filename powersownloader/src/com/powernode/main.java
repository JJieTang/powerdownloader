package com.powernode;

import com.powernode.core.Downloader;
import com.powernode.util.LogUtils;

import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        String url = null;

        if (args == null || args.length == 0){
            for(;;){
                LogUtils.info("Please input the url to download from:");
                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
                if (url != null){
                    break;
                }
            }
        }
        else{
            url = args[0];
        }

        Downloader downloader = new Downloader();
        downloader.download(url);
    }
}
