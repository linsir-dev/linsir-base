package com.linsir.base.core.controller;

import com.linsir.base.core.vo.R;

/**
 * @author linsir
 * @title: ControllerCallable
 * @projectName lins
 * @description: TODO
 * @date 2021/12/10 0:14
 */
@FunctionalInterface
public interface ControllerCallable {

    /**
     * 执行业务方法
     *
     * @return
     */
    R execute() throws Exception;

}
