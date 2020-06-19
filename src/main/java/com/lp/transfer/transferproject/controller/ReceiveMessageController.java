package com.lp.transfer.transferproject.controller;

import com.lp.transfer.transferproject.bean.ReceiveMessageRequest;
import com.lp.transfer.transferproject.enums.Response;
import lombok.extern.slf4j.Slf4j;
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

    @RequestMapping("originalData")
    @ResponseBody
    public Response<String> receiveMessage(@RequestBody ReceiveMessageRequest receiveMessageRequest){



        return Response.success("成功");
    }

    @RequestMapping("result")
    @ResponseBody
    public Response<String> receiveResult(@RequestBody ReceiveMessageRequest receiveMessageRequest){
        return Response.success("成功");
    }

}