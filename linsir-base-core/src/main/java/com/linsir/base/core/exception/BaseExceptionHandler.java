package com.linsir.base.core.exception;



import com.linsir.base.core.code.BaseCode;
import com.linsir.base.core.util.V;
import com.linsir.base.core.vo.Result;
import com.linsir.base.core.vo.jsonResults.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @title: CommonExceptionHandler
 * @projectName lins
 * @description:  系统中全局异常处理逻辑
 * @date 2022/1/11 17:14
 */

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    /**
     * @title validationExceptionHandler
     * @description  参数验证异常
     * @param
     * @return
     * @author linsir
     * @updateTime 2022/6/13 15:32
     * @throws
     */
    @ResponseBody
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public Result validationExceptionHandler(Exception ex)
    {
        BindingResult br = null;
        Result result=null;
        if(ex instanceof BindException){
            br = ((BindException)ex).getBindingResult();
        }
        if (br != null && br.hasErrors()) {
            result = new Result(BaseCode.FAIL_INVALID_PARAM);
            String validateErrorMsg = V.getBindingError(br);
            log.warn("数据校验失败, {}: {}", br.getObjectName(), validateErrorMsg);
        }
        return result;
    }



   /* @ResponseBody
    @ExceptionHandler(Exception.class)
    public RespResult exceptionHandler(Exception exception)
    {
        log.error("最古老的异常错误...{}，未知异常！原因是:",exception.getMessage());
        String exMsg = "最古老的异常错误...，未知异常！原因是:"+exception.getMessage();
        return  new RespResult(BaseExceptionCode.SYSTEM_ERROR,exMsg);
    }

    @ResponseBody
    @ExceptionHandler(SQLException.class)
    public RespResult sqlExceptionHandler(SQLException sqlException)
    {
        log.error("SQL 异常...");
        return new RespResult(BaseExceptionCode.SQL_ERROR,
                 "State:"+sqlException.getSQLState()+
                         "msg:"+sqlException.getMessage()+
                         "code:"+sqlException.getErrorCode());
    }



    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RespResult methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException)
    {

       for (FieldError error : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            log.error("参数异常信息:{}", error.getDefaultMessage());
        }
        return new RespResult(BaseExceptionCode.PARAM_ERROR,
                methodArgumentNotValidException.getBindingResult().getFieldErrors());
    }

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public RespResult  IBusinessExceptionHandler(BusinessException businessException)
    {
        IFeatures features = businessException.getIFeatures();
        log.error("功能编码：{};功能名称：{};功能方法名称：{};功能操作内容：{} 错误信息如下：",
                features.getCode(),
                features.getModuleName(),
                features.getMethodName(),
                features.getDesc());
        log.error("错误编码：{};异常信息：{};功能方法名称：{};异常状态：{}",
                businessException.getCode().getCode(),
                businessException.getCode().getMsg(),
                businessException.getCode().status());
        return new RespResult(businessException.getCode(),features);
    }*/

}
