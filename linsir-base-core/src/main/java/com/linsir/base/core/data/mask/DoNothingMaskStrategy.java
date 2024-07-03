package com.linsir.base.core.data.mask;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 15:02
 * @description：脱敏策略空实现
 * @modified By：
 * @version: 0.0.1
 */
public class DoNothingMaskStrategy implements IMaskStrategy{
    @Override
    public String mask(String content) {
        return content;
    }
}
