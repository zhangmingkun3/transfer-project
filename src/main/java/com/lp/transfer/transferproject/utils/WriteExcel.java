package com.lp.transfer.transferproject.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.OutputStream;
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

    private static void write(String filepath,String fileName,String sheetName){

        boolean success = false;
        OutputStream outputStream = null;

        if (StringUtils.isEmpty(filepath)){
            throw new IllegalArgumentException("文件路径不能为空");
        }else{
//            String suffiex = getSuffiex(filepath);
//            if (StringUtils.isBlank(suffiex)) {
//                throw new IllegalArgumentException("文件后缀不能为空");
//            }
//            Workbook workbook;
//            if ("xls".equals(suffiex.toLowerCase())) {
//                workbook = new HSSFWorkbook();
//            } else {
//                workbook = new XSSFWorkbook();
//            }

            File NewxlsFile = new File("C:\\Users\\zhangmingkun3\\Desktop\\user_lable1.xlsx");

            // 创建一个工作簿
            XSSFWorkbook workbook = new XSSFWorkbook();
            // 创建一个工作表
            XSSFSheet sheet = workbook.createSheet("sheet1");

            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth((short) 15);
            // 生成样式
            Map<String, CellStyle> styles = createStyles(workbook);

            // 创建标题行
            Row row0 = sheet.createRow(0);
            Row row1 = sheet.createRow(1);
            // 存储标题在Excel文件中的序号
            Map<String, Integer> titleOrder = new HashMap<>();
            for (int i = 0; i < cellName.size(); i++) {
                Cell cell = row0.createCell(i);
                cell.setCellStyle(styles.get("header"));
                String title = cellName.get(i);
                cell.setCellValue(title);
                titleOrder.put(title, i);
            }

            for(Map.Entry entry : cellName.entrySet()){

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
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); // 前景色
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
        headerFont.setColor(IndexedColors.WHITE.getIndex());
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
        styles.put("cellA", cellStyleA);

        // 正文样式B:添加前景色为浅黄色
        XSSFCellStyle cellStyleB = (XSSFCellStyle) wb.createCellStyle();
        cellStyleB.setAlignment(HorizontalAlignment.CENTER);
        cellStyleB.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleB.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        cellStyleB.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyleB.setWrapText(true);
        cellStyleB.setBorderRight(BorderStyle.THIN);
        cellStyleB.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleB.setBorderLeft(BorderStyle.THIN);
        cellStyleB.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleB.setBorderTop(BorderStyle.THIN);
        cellStyleB.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleB.setBorderBottom(BorderStyle.THIN);
        cellStyleB.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleB.setFont(cellStyleFont);
        styles.put("cellB", cellStyleB);

        return styles;
    }


}