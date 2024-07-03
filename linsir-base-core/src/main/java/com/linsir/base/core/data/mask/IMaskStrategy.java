package com.linsir.base.core.data.mask;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 14:43
 * @description：脱敏策略接口
 * @modified By：
 * @version: 0.0.1
 */
public interface IMaskStrategy {
    /**
     * 脱敏处理
     *
     * @param content 字符串
     * @return 脱敏之后的字符串
     */
    String mask(String content);
}
