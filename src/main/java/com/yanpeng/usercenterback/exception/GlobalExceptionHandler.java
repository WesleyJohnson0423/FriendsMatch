package com.yanpeng.usercenterback.exception;

import com.yanpeng.usercenterback.common.BaseResponse;
import com.yanpeng.usercenterback.common.ErrorCode;
import com.yanpeng.usercenterback.common.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author yp
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionExceptionHandler(BusinessException e) {
        log.error("BusinessException"+e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionExceptionHandler(BusinessException e) {
        log.error("runtimeException",e);
        return ResultUtils.error(ErrorCode.SYS_ERROR,e.getMessage(),"");
    }
}
