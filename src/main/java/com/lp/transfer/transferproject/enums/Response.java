package com.lp.transfer.transferproject.enums;

public class Response<T> {

    private EmRequestStatus status;
    private String rspCode;
    private String rspMsg;
    private T rspData;

    public Response() {
    }

    public String getRspCode() {
        return rspCode;
    }


    public boolean isSuccess() {
        return EmRequestStatus.SUCCESS.equals(status);
    }

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setStatus(EmRequestStatus.SUCCESS);
        response.setRspCode(EmReturnCode.NORMAL_RETURN_CODE.getErrorCode());
        response.setRspMsg(EmReturnCode.NORMAL_RETURN_CODE.getMsgFormat());
        response.setRspData(data);
        return response;
    }

    public static <T> Response<T> fail(T data) {
        Response<T> response = new Response<>();
        response.setStatus(EmRequestStatus.FAILED);

        response.setRspData(data);
        return response;
    }


    public static <T> Response<T> copyNotSucess(Response other) {
        Response<T> response = new Response<>();
        response.setStatus(other.getStatus());
        response.setRspMsg(other.getRspMsg());
        response.setRspCode(other.getRspCode());

        return response;
    }


    public static <T> Response<T> fail(EmReturnCode errorCode, String errorMsg) {
        return notSuccess(EmRequestStatus.FAILED, errorCode, errorMsg);
    }

    public static <T> Response<T> exception(EmReturnCode errorCode, String errorMsg) {
        return notSuccess(EmRequestStatus.EXCEPTION, errorCode, errorMsg);
    }

    private static <T> Response notSuccess(EmRequestStatus status, EmReturnCode errorCode, String errorMsg) {
        Response<T> response = new Response<>();
        response.setStatus(status);
        response.setRspCode(errorCode.getErrorCode());
        if (null != errorMsg) {
            if (errorCode.getMsgFormat().contains("%")) {
                response.setRspMsg(String.format(errorCode.getMsgFormat(), errorMsg));
            } else {
                response.setRspMsg(errorMsg);
            }
        }
        return response;
    }



    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    public String getRspMsg() {
        return rspMsg;
    }

    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }

    public T getRspData() {
        return rspData;
    }

    public void setRspData(T rspData) {
        this.rspData = rspData;
    }

    public EmRequestStatus getStatus() {
        return status;
    }

    public void setStatus(EmRequestStatus status) {
        this.status = status;
    }
}