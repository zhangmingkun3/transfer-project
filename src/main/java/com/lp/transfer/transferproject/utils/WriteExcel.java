package com.lp.transfer.transferproject.utils;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/6/13 17:37
 */
public class WriteExcel {

    private static Map<String,String> cellName= new HashMap<>();

    static {
        cellName.put("1","z1_cun_fu");cellName.put("2","z1_cun_zhong");
        cellName.put("3","z1_cun_chen");cellName.put("4","z1_guan_fu");
        cellName.put("5","z1_guan_zhong");cellName.put("6","z1_guan_chen");
        cellName.put("7","z1_chi_fu");cellName.put("8","z1_chi_zhong");
        cellName.put("9","z1_chi_chen");cellName.put("10","z3_cun_fu");
        cellName.put("11","z3_cun_zhong");cellName.put("12","z3_cun_chen");
        cellName.put("13","z3_guan_fu");cellName.put("14","z3_guan_zhong");
        cellName.put("15","z3_guan_chen");cellName.put("16","z3_chi_fu");
        cellName.put("17","z3_chi_zhong");cellName.put("18","z3_chi_chen");
        cellName.put("19","y1_cun_fu");cellName.put("20","y1_cun_zhong");
        cellName.put("21","y1_cun_chen");cellName.put("22","y1_guan_fu");
        cellName.put("23","y1_guan_zhong");cellName.put("24","y1_guan_chen");
        cellName.put("25","y1_chi_fu");cellName.put("26","y1_chi_zhong");
        cellName.put("27","y1_chi_chen");cellName.put("28","y3_cun_fu");
        cellName.put("29","y3_cun_zhong");cellName.put("30","y3_cun_chen");
        cellName.put("31","y3_guan_fu");cellName.put("32","y3_guan_zhong");
        cellName.put("33","y3_guan_chen");cellName.put("34","y3_chi_fu");
        cellName.put("35","y3_chi_zhong");cellName.put("36","y3_chi_chen");

    }

    public static void main(String[] args) {

        int[] array1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        int[] array2 = {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};
        int[] array3 = {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,};

        write("C:\\Users\\User\\","填充.xlsx","sheet1",array1,array2,array3);


//        checkPath("/export/servers/");
    }




    /**
     *
     * @param filePath  excel存储路径
     * @param fileName  excel文件名称
     * @param sheetName sheet名称
     */
    public static void write(String filePath,String fileName,String sheetName,int[] array1,int[] array2,int[] array3){

        OutputStream outputStream = null;

        if (StringUtils.isEmpty(filePath)){
            throw new IllegalArgumentException("文件路径不能为空");
        }else if (checkPath(filePath)){
            throw new IllegalArgumentException("文件路径不符合格式");
        }

        if (StringUtils.isEmpty(sheetName)){
            sheetName = "sheet1";
        }
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件后缀不能为空");
        }

        if (null == array1 || null == array2 || null == array3){
            throw new IllegalArgumentException("excel填充内容不能是空");
        }else{
            if (array1.length != array2.length || array1.length != array3.length){
                throw new IllegalArgumentException("excel填充内容必须数量相同");
            }
        }

        Workbook workbook;
        if ("xls".equals(fileName.toLowerCase())) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        File newxlsFile = new File(filePath + fileName);

        // 创建一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(sheetName);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成样式
        Map<String, CellStyle> styles = createStyles(workbook);


        for (int j = 0; j < array1.length; j ++){
            if (j < 2){
                Row row0 = sheet.createRow(0);
                Row row1 = sheet.createRow(1);
                for (int i = 0; i < cellName.size(); i++) {
                    Cell cell = row0.createCell(i);
                    Cell cell1 = row1.createCell(i);
                    cell.setCellStyle(styles.get("header"));
                    cell1.setCellStyle(styles.get("header"));
                    String titleKey = String.valueOf(i+1);
                    String title = cellName.get(titleKey);
                    cell.setCellValue(titleKey);
                    cell1.setCellValue(title);
                }
            }else{
                Row rowNumber = sheet.createRow(j);
                for (int i = 0; i < cellName.size(); i ++){

                    int index = i + 1;
                    Cell cellNumber = rowNumber.createCell(i);
                    if (index % 3 == 1){
                        cellNumber.setCellValue(array1[j-2]);
                    }else if (index % 3 == 2){
                        cellNumber.setCellValue(array2[j-2]);
                    }else if (index % 3 == 0){
                        cellNumber.setCellValue(array3[j-2]);
                    }
                    cellNumber.setCellStyle(styles.get("cell"));
                }
            }
        }
        try {
            outputStream = new FileOutputStream(newxlsFile);
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取后缀
     * @param filepath filepath 文件全路径
     */
    private static String getSuffiex(String filepath) {
        if (StringUtils.isBlank(filepath)) {
            return "";
        }
        int index = filepath.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filepath.substring(index + 1, filepath.length());
    }


    /**
     * 设置格式
     */
    private static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<> ();

        // 标题样式
        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER); // 水平对齐
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐
        titleStyle.setLocked(true); // 样式锁定
        titleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleFont.setFontName("微软雅黑");
        titleStyle.setFont(titleFont);
        styles.put("title", titleStyle);

        // 文件头样式
        XSSFCellStyle headerStyle = (XSSFCellStyle) wb.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex()); // 前景色
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 颜色填充方式
        headerStyle.setWrapText(true);
        headerStyle.setBorderRight(BorderStyle.THIN); // 设置边界
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        Font headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        titleFont.setFontName("微软雅黑");
        headerStyle.setFont(headerFont);
        styles.put("header", headerStyle);

        Font cellStyleFont = wb.createFont();
        cellStyleFont.setFontHeightInPoints((short) 12);
        cellStyleFont.setColor(IndexedColors.BLUE_GREY.getIndex());
        cellStyleFont.setFontName("微软雅黑");

        // 正文样式A
        XSSFCellStyle cellStyleA = (XSSFCellStyle) wb.createCellStyle();
        cellStyleA.setAlignment(HorizontalAlignment.CENTER); // 居中设置
        cellStyleA.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleA.setWrapText(true);
        cellStyleA.setBorderRight(BorderStyle.THIN);
        cellStyleA.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleA.setBorderLeft(BorderStyle.THIN);
        cellStyleA.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleA.setBorderTop(BorderStyle.THIN);
        cellStyleA.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleA.setBorderBottom(BorderStyle.THIN);
        cellStyleA.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleA.setFont(cellStyleFont);
        styles.put("cell", cellStyleA);

        return styles;
    }


    /**
     * @param path  文件路径  /export/servers/   main中测试是windows！！！
     * @return
     */
    private static boolean checkPath(String path) {
        java.util.regex.Pattern p=java.util.regex.Pattern.compile("(^//.|^/|^[a-zA-Z])?:?/.+(/$)?");
        java.util.regex.Matcher m=p.matcher(path);
        //不符合要求直接返回
        return m.matches();
    }

}