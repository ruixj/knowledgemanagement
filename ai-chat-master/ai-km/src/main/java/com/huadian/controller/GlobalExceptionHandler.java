package com.huadian.controller;



import com.google.common.base.Throwables;
import com.huadian.exception.BaseException2;
import com.huadian.messages.ErrorConstant;
import com.huadian.util.ErrorUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import java.sql.SQLException;

/**
 * Created by xiongjian on 2018/10/24.
 * 全局异常处理，捕获所有Controller中抛出的异常。
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    //处理自定义的异常
    @ExceptionHandler(BaseException2.class)
    @ResponseBody
    public Object customHandler(BaseException2 e){

        if(e.getCause() != null) {
            log.error(e.getCause().getMessage());
            log.error(Throwables.getStackTraceAsString(e.getCause()));
        }
        log.error(Throwables.getStackTraceAsString(e));

        if(e.getArguements() != null) {
            return ErrorUtil.retErrMsg(e.getCode(),e.getArguements());
        } else if(e.getArguements() != null && e.getExtraInfo() != null) {
            return ErrorUtil.retErrMsg(e.getCode(),e.getExtraInfo(),e.getArguements());
        }else if ( e.getExtraInfo() != null){
            return ErrorUtil.retErrMsg(e.getCode(),e.getExtraInfo());
        }else{
            return ErrorUtil.retErrMsg(e.getCode());
        }

    }

    //其他未处理的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e){
        log.error(e.getMessage());

        log.error(Throwables.getStackTraceAsString(e));

        if( e instanceof RestClientException)
        {
            return ErrorUtil.retErrMsg(ErrorConstant.REST_CALL_FAILED);
        }

//        if( e instanceof AuthenticationException)
//        {
//            return ErrorUtil.retErrMsg(ErrorConstant.INVALID_TOKEN);
//        }

        if( e instanceof SQLException){
            return ErrorUtil.retErrMsg(ErrorConstant.FAILED_TO_EXCUTESQL);
        }
        return ErrorUtil.retErrMsg(ErrorConstant.SERVER_UNCATCH_ERROR,new Object[]{e.getMessage()});
    }
}
