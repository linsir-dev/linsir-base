package com.linsir.base.core.vo;


import lombok.Data;

/**
 * @author admin
 * @version 1.0.0
 * @ClassName ResResult.java
 * @Description   为模拟 数据定义 返回的 mock
 * @createTime 2022年07月13日 16:56:00
 */
@Data
public class MockResult<T> {

    private  int code;

    private T data;

    public MockResult(T data)
    {
        this.code = 200;
        this.data = data;
    }

}
