package com.lp.transfer.transferproject.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/6/11 15:36
 */
@Slf4j
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


    public static JSONObject readExcel(String sinkPath) throws IOException {
        log.info("读取excel文件。。。。。。");
        long start = System.currentTimeMillis();
        List<Double> list1 = new ArrayList<>();
        List<Double> list2 = new ArrayList<>();
        List<Double> list3 = new ArrayList<>();

        FileInputStream fileInputStream = new FileInputStream(new File(sinkPath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
            XSSFRow row = sheet.getRow(j);
            if (row != null) {
                if (null != row.getCell(0)){
                    list1.add(row.getCell(0).getNumericCellValue());
                }else {
                    list1.add(0D);
                }

                if (null != row.getCell(1)){
                    list2.add(row.getCell(1).getNumericCellValue());
                }else {
                    list2.add(0D);
                }
                if (null != row.getCell(2)){
                    list3.add(row.getCell(2).getNumericCellValue());
                }else {
                    list3.add(0D);
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array1",list1.toArray(new Double[0]));
        jsonObject.put("array2",list2.toArray(new Double[0]));
        jsonObject.put("array3",list3.toArray(new Double[0]));

        log.info("读取excel文件完成 执行时间{} 数据结果{}", System.currentTimeMillis() - start,jsonObject.toJSONString());
        fileInputStream.close();
        return jsonObject;

    }

    public static void main(String[] args) throws IOException {
//        writeFile("zmk","1234546");

        System.out.println(JSON.toJSONString(readExcel("C:\\Users\\User\\Desktop\\Result.xlsx")));

    }



}