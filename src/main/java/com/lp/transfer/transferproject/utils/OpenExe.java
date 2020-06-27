package com.lp.transfer.transferproject.utils;
import lombok.extern.slf4j.Slf4j;

import	java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/6/17 11:52
 */
@Slf4j
public class OpenExe {

    public static void runExe(String receiveMessageRequest){
        long start = System.currentTimeMillis();
        BufferedReader br = null;
        BufferedReader brError = null;;
        try {
            Process p = Runtime.getRuntime().exec(receiveMessageRequest);
            String line = null;
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            brError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = br.readLine()) !=null || (line = brError.readLine()) != null){
                //输出exe输出的信息以及错误信息
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            log.info("执行exe程序解析完成,时间 {}",System.currentTimeMillis() - start);
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (brError != null){
                try {
                    brError.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {

//        runExe();

    }

}