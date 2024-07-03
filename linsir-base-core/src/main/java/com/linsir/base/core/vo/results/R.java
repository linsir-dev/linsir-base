package com.linsir.base.core.vo.results;

/**
 * @author Administrator
 * @title: IResult
 * @projectName linsir
 * @description: 所有返回统一接口
 *
 * 所有的返回接口，由head和body构成
 *
 * 定义 http tcp mq 消息等实现
 *
 * @date 2022/2/4 15:14
 *
 * 2024.7.3 升级版本
 */

public interface R<Code,Message,Data> {

    Code getCode();

    Message getMessage();

    Data getData();

    void setCode(Code code);

    void setMessage(Message message);

    void setData(Data data);
}
