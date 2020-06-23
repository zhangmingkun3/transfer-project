package com.lp.transfer.transferproject.controller;

import com.lp.transfer.transferproject.bean.ReceiveMessageRequest;
import com.lp.transfer.transferproject.enums.Response;
import com.lp.transfer.transferproject.utils.OpenExe;
import com.lp.transfer.transferproject.utils.WriteExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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

        try {
            OpenExe.runExe(programPath);
            return Response.success("成功");
        }catch (Throwable e){
            return Response.fail("失败 " + e.getMessage());
        }



    }

    @RequestMapping("result")
    @ResponseBody
    public Response<String> receiveResult(@RequestBody ReceiveMessageRequest receiveMessageRequest){
        return Response.success("成功");
    }

}