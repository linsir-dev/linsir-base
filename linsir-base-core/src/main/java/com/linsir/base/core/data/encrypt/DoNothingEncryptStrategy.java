package com.linsir.base.core.data.encrypt;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 15:01
 * @description：加密策略空实现
 * @modified By：
 * @version: 0.0.1
 */
public class DoNothingEncryptStrategy implements IEncryptStrategy{
    @Override
    public String encrypt(String content) {
        return content;
    }

    @Override
    public String decrypt(String content) {
        return content;
    }
}
