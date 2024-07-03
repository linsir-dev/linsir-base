package com.linsir.base.core.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/2/14 11:38
 * @description:
 */
@Data
public class ParameterDto extends CommonBaseDto{

    /*操作数据 参数*/
    private Map<String,Object> parameters;
}
