package com.linsir.base.core.exception;


import com.linsir.base.core.code.ICode;


/**
 * @author linsir
 * @title: BusinessException
 * @projectName lins
 * @description: 业务异常
 * @date 2021/12/9 9:58
 */
public class BusinessException extends RuntimeException  implements IException{

    private ICode code;

    @Override
    public ICode getCode() {
        return this.code;
    }

    public BusinessException(ICode code)
    {
        this.code = code;
    }

    public BusinessException(ICode code, String e)
    {
        super(e);
        this.code = code;
    }

    public BusinessException(ICode code, Throwable ex) {
        super(code.getMsg(), ex);
        this.code = code;
    }
}
