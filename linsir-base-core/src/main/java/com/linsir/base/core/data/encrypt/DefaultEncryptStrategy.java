package com.linsir.base.core.data.encrypt;

import com.linsir.base.core.util.Encryptor;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/25 11:39
 * @description：加密算法默认实现
 * @modified By：
 * @version: 0.0.1
 */
public class DefaultEncryptStrategy implements IEncryptStrategy{
    @Override
    public String encrypt(String content) {
        return Encryptor.encrypt(content);
    }

    @Override
    public String decrypt(String content) {
        return Encryptor.decrypt(content);
    }
}
