package com.lp.transfer.transferproject.controller;

import com.alibaba.fastjson.JSONObject;
import com.lp.transfer.transferproject.bean.ReceiveMessageRequest;
import com.lp.transfer.transferproject.enums.Response;
import com.lp.transfer.transferproject.utils.FileUtils;
import com.lp.transfer.transferproject.utils.HttpClientUtils;
import com.lp.transfer.transferproject.utils.OpenExe;
import com.lp.transfer.transferproject.utils.WriteExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/6/10 18:35
 */
@Slf4j
@RestController
@RequestMapping(value = "receive", method = {RequestMethod.POST})
public class ReceiveMessageController {

    @Value("${transfer.programPath}")
    private String programPath;

    @Value("${transfer.sourcePath}")
    private String sourcePath;

    @Value("${transfer.sourceName}")
    private String sourceName;

    @Value("${transfer.sinkPath}")
    private String sinkPath;

    @Value("${request.httpUrl}")
    private String httpUrl;


    @RequestMapping("originalData")
    @ResponseBody
    public Response<String> receiveMessage(@RequestBody ReceiveMessageRequest receiveMessageRequest){

//        // 数据写入excel文件
//        WriteExcel.write(sourcePath,sourceName,null,receiveMessageRequest.getArray1(),receiveMessageRequest.getArray2(),receiveMessageRequest.getArray3());
//
//        File file = null;
//        try {
//            log.info("执行exe程序解析。。。");
//            OpenExe.runExe(programPath);
//            log.info("休眠等待中。。。。。。");
//            Thread.sleep(1000);
//
//            //读取文件，判断文件是否存在
//            file = new File(sinkPath);
//            JSONObject jsonObject = null;
//            if (file.exists()){
//                // 文件存在,判断文件是都最近创建
//                jsonObject = FileUtils.readExcel(sinkPath);
//            }else{
//                log.info("继续休眠等待中。。。。。。");
//                Thread.sleep(1000);
//                if (file.exists()){
//                    jsonObject = FileUtils.readExcel(sinkPath);
//                }else{
//                    log.info("{} 文件不存在。。。。。。",sinkPath);
//                    return Response.fail(sinkPath + "文件不存在");
//                }
//            }
//            return Response.success(jsonObject.toJSONString());
//        }catch (Exception e){
//            return Response.fail("失败 " + e.getMessage());
//        }finally {
//            if (file != null){
//                DataOutputStream dos = null;
//                try {
//                    dos = new DataOutputStream(new FileOutputStream(file));
//                    dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                boolean delete = file.delete();
//                log.info("删除生成的结果文件 {}",delete);
//            }
//        }


        return Response.success(null);

    }

    @RequestMapping("result")
    @ResponseBody
    public Response<String> receiveResult(){

        String deviceId = "20200000000001";

        // 调用exe
        File file = null;
        try {
            log.info("执行exe程序解析。。。");
            OpenExe.runExe(programPath);
            log.info("休眠等待中。。。。。。");
            Thread.sleep(30000);

            //读取文件，判断文件是否存在
            file = new File(sinkPath);
            String response = null;
            if (file.exists() && file.length() > 0) {
                log.info("文件存在。。。。。。");
                // 文件存在,判断文件是都最近创建
                response = doHttpPost(FileUtils.readExcel(sinkPath), deviceId);
            } else {
                log.info("继续休眠等待中。。。。。。");
                Thread.sleep(5000);
                if (file.exists() && file.length() > 0) {
                    response = doHttpPost(FileUtils.readExcel(sinkPath), deviceId);
                } else {
                    log.info("{} 文件不存在或大小为0。。。。。。", sinkPath);
                }
            }
            log.info("调用http接口完成，返回消息{}", response);
        } catch (Exception e) {
            e.getStackTrace();
            log.error("失败 {}" , e.getMessage());
        } finally {
            if (file != null) {
                DataOutputStream dos = null;
                try {
                    dos = new DataOutputStream(new FileOutputStream(file));
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean delete = file.delete();
                log.info("删除生成的结果文件 {}", delete);
            }
        }
        return Response.success("成功");
    }

    private String doHttpPost(JSONObject jsonObject,String deviceId){
        Map<String,Object> param = new HashMap<>(4);
        param.put("result",jsonObject);
        param.put("deviceId",deviceId);
        try {
            return HttpClientUtils.post(httpUrl,param);
        } catch (IOException e) {
            log.info("调用http接口出现异常 {},{}",httpUrl,e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}