package com.linsir.base.core.enums;

/**
 * @author Administrator
 * @title: Features
 * @projectName lins
 * @description: 功能模块、方法枚举
 *
 *
 * code
 * 1位数，代表以及功能
 * 2位数代表，二级功能
 * 3位数代表三级功能
 *
 * 例如：用户中心 1
 *         用户 11
 *         角色 12
 *           角色列表 121
 *           角色添加 122
 * @date 2022/1/5 11:04
 */
public interface IFeatures {

     String getCode();

     String getModuleName();

     String getMethodName();

     String getDesc();
}
