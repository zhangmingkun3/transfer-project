package com.lp.transfer.transferproject.enums;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/2/24 17:04
 * @Version: 1.0
 */
public enum EmReturnCode {

    NORMAL_RETURN_CODE("000000000", "成功"),


    DATA_INVALID_EXCEPTION("000000002", "数据非法:%s"),

    ;


    private String errorCode;
    private String msgFormat;

    EmReturnCode(String errorCode, String msgFormat) {
        this.errorCode = errorCode;
        this.msgFormat = msgFormat;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }
}
