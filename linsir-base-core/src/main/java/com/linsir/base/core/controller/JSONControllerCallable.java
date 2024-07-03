package com.linsir.base.core.controller;

import com.linsir.base.core.vo.jsonResults.JsonResult;

/**
 * description:
 *
 * @author [linsir]
 * @version 0.0.1
 * @date 2022/08/08 16:31:37
 */
@FunctionalInterface
public interface JSONControllerCallable {

    JsonResult execute() throws Exception;
}
