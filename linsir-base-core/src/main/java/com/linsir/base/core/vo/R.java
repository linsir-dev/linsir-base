package com.linsir.base.core.vo;

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
 */
@Deprecated
public interface R<Head,Body>{
    Head getHead();
    Body getBody();
}
