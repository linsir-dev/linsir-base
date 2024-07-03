package com.linsir.base.core.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: linsir
 * @date: 2022/3/21 10:43
 * @description: 无效使用异常类 InvalidUsageException
 */
public class InvalidUsageException extends RuntimeException{
    /**
     * 自定义内容提示
     *
     * @param msg
     */
    public InvalidUsageException(String msg) {
        super(msg);
    }

    /**
     * 自定义内容提示
     *
     * @param msg
     */
    public InvalidUsageException(String msg, Throwable ex) {
        super(msg, ex);
    }

    /**
     * 转换为Map
     *
     * @return
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(8);
        map.put("code", getCode());
        map.put("msg", getMessage());
        return map;
    }

    private int getCode() {
        return 5005;
    }
}
