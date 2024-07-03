package com.linsir.base.core.exception;

import com.linsir.base.core.code.ICode;

/**
 * @author linsir
 * @title: ValidExeption
 * @projectName linsir
 * @description: 验证异常
 * @date 2022/3/4 13:28
 */
public class ValidExeption extends RuntimeException implements IException{

    private ICode code;

    @Override
    public ICode getCode() {
        return this.code;
    }

    public ValidExeption(ICode code)
    {
        this.code = code;
    }

    public ValidExeption(String msg)
    {
        super(msg);
    }

    public ValidExeption(ICode code, String msg)
    {
        super(msg);
        this.code = code;
    }
}
