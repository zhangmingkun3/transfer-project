package com.lp.transfer.transferproject.controller;

import com.alibaba.fastjson.JSONObject;
import com.lp.transfer.transferproject.bean.ReceiveMessageRequest;
import com.lp.transfer.transferproject.enums.Response;
import com.lp.transfer.transferproject.utils.FileUtils;
import com.lp.transfer.transferproject.utils.OpenExe;
import com.lp.transfer.transferproject.utils.WriteExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;

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

    @RequestMapping("originalData")
    @ResponseBody
    public Response<String> receiveMessage(@RequestBody ReceiveMessageRequest receiveMessageRequest){

        // 数据写入excel文件
        WriteExcel.write(sourcePath,sourceName,null,receiveMessageRequest.getArray1(),receiveMessageRequest.getArray2(),receiveMessageRequest.getArray3());

        File file = null;
        try {
            log.info("执行exe程序解析。。。");
            OpenExe.runExe(programPath);
            log.info("休眠等待中。。。。。。");
            Thread.sleep(1000);

            //读取文件，判断文件是否存在
            file = new File(sinkPath);
            JSONObject jsonObject = null;
            if (file.exists()){
                // 文件存在,判断文件是都最近创建
                jsonObject = FileUtils.readExcel(sinkPath);
            }else{
                log.info("继续休眠等待中。。。。。。");
                Thread.sleep(1000);
                if (file.exists()){
                    jsonObject = FileUtils.readExcel(sinkPath);
                }else{
                    log.info("{} 文件不存在。。。。。。",sinkPath);
                    return Response.fail(sinkPath + "文件不存在");
                }
            }
            return Response.success(jsonObject.toJSONString());
        }catch (Exception e){
            return Response.fail("失败 " + e.getMessage());
        }finally {
            if (file != null){
                DataOutputStream dos = null;
                try {
                    dos = new DataOutputStream(new FileOutputStream(file));
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean delete = file.delete();
                log.info("删除生成的结果文件 {}",delete);
            }
        }




    }

    @RequestMapping("result")
    @ResponseBody
    public Response<String> receiveResult(@RequestBody ReceiveMessageRequest receiveMessageRequest){
        return Response.success("成功");
    }

}