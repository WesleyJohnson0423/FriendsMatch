package com.yanpeng.usercenterback.common;

import lombok.Data;


public enum ErrorCode {

    SUCCESS(0,"OK",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    USER_REPEAT(1,"用户重复",""),
    NO_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限",""),
    SYS_ERROR(50000,"系统内部异常","")
    ;
    /**
     * 状态码信息
     */

    private final int code;

    /**
     * 描述信息
     */
    private final String msg;

    private final String description;

    ErrorCode(int code, String msg, String description) {
        this.code = code;
        this.msg = msg;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getMsg() {
        return msg;
    }
}
