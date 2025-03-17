package com.yanpeng.usercenterback.common;

/**
 *
 * 返回工具类
 *
 */
public class ResultUtils {
    //static后面的<T>是函数的参数T的声名，如何函数参数里还有K，则要在static后面再加个K
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }

    public static  BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static  BaseResponse error(int code,String message,String description){
        return new BaseResponse<>(code,null,message,description);
    }

    public static  BaseResponse error(ErrorCode errorCode,String message,String description){
        return new BaseResponse<>(errorCode.getCode(),message,description);
    }

    public static  BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse<>(errorCode.getCode(),errorCode.getMsg(),description);
    }
}
