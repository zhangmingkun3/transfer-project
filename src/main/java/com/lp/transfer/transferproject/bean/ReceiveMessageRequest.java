package com.lp.transfer.transferproject.bean;
import	java.util.Date;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/6/10 20:03
 */
@Data
public class ReceiveMessageRequest implements Serializable{

    private int[] array1;
    private int[] array2;
    private int[] array3;


}