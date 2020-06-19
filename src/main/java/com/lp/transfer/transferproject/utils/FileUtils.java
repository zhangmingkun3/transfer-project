package com.lp.transfer.transferproject.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/6/11 15:36
 */
public class FileUtils {

    private static final String FILEPATH = "C:\\Users\\zhangmingkun3\\Desktop\\";

    public static void writeFile(String userId,String content){

        String absolutePath = FILEPATH + System.currentTimeMillis() + "_" + userId;
        File file = new File(absolutePath);
        if (!file.exists()){
            try {
                final boolean newFile = file.createNewFile();
                if (!newFile){
                    throw new RuntimeException("创建文件失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Writer writer = null;
        String outputString = null;
        try {
            outputString = content + "\r\n";
            // true表示追加
            writer = new FileWriter(file, true);
            writer.write(outputString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null){
                    writer.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        writeFile("zmk","1234546");
    }



}