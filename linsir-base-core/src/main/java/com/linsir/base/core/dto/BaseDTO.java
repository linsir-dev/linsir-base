package com.linsir.base.core.dto;

import lombok.Data;

/**
 * @author: linsir
 * @date: 2022/2/14 11:39
 * @description:  baseDTO 为DTO基础类型，不继承Entity
 */

@Data
public class BaseDTO {
    //操作日志的业务类型
    private String bizType;
}
