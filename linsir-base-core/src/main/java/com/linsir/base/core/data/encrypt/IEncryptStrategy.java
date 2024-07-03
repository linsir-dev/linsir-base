package com.linsir.base.core.data.encrypt;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 14:41
 * @description：加解密接口
 * @modified By：
 * @version: 0.0.1
 */
public interface IEncryptStrategy {

    /**
     * 加密
     *
     * @param content 内容
     * @return 密文
     */
    String encrypt(String content);

    /**
     * 解密
     *
     * @param content 内容
     * @return 明文
     */
    String decrypt(String content);
}
