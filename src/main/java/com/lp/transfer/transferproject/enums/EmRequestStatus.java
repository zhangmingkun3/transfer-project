package com.lp.transfer.transferproject.enums;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/3/4 16:48
 * @Version: 1.0
 */
public enum EmRequestStatus {

    /**成功*/
    SUCCESS(0,"成功"),

    /**
     * 异常
     * */
    EXCEPTION(2,"异常"),

    /**
     * 失败
     * */
    FAILED(3,"失败"),

    ;

    private Integer code;

    private String desc;
    EmRequestStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static EmRequestStatus codeOf(Integer code) {
        for (EmRequestStatus status : EmRequestStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}