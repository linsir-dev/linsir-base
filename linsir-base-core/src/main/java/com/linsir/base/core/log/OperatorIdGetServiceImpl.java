package com.linsir.base.core.log;

import com.linsir.logRecord.service.IOperatorIdGetService;
import org.springframework.stereotype.Service;

/**
 * @author linsir
 * @version 1.0.0
 * @title IOperatorIdGetServiceImpl
 * @description TODO 当前为测试用户 ，以后需要从分布式session 中获取 当前用户信息
 * @create 2024/7/5 10:48
 */

@Service
public class OperatorIdGetServiceImpl implements IOperatorIdGetService {
    @Override
    public String getOperatorId() {
        return "test -user";
    }
}
