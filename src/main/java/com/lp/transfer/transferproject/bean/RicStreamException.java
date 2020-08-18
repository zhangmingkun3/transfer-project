package com.lp.transfer.transferproject.bean;

import com.lp.transfer.transferproject.enums.EmReturnCode;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/18 14:28
 */
public class RicStreamException extends RuntimeException {

    private EmReturnCode errorCode;

    private String message;

    public RicStreamException(EmReturnCode errorCode, String message) {
        this(errorCode, message, null);
    }


    public RicStreamException(EmReturnCode errorCode, String message, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.message = message;
    }

    public EmReturnCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(EmReturnCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
