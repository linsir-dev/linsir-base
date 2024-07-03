package com.linsir.base.core.util;

import com.linsir.base.core.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author linsir
 * @title: Encryptor
 * @projectName linsir
 * @description: 加解密工具类 （提供AES加解密）
 * @date 2022/3/11 1:37
 */

@Slf4j
public class Encryptor {

    /**
     * 算法
     */
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5PADDING";

    private static final String KEY_FILL = "abcdefghijklmnop";

    /**
     * 加密Cipher缓存
     */
    private static Map<String, Cipher> encryptorMap = null;

    private static Map<String, Cipher> getEncryptorMap(){
        if(encryptorMap == null){
            encryptorMap = new ConcurrentHashMap<>();
        }
        return encryptorMap;
    }

    /**
     * 解密Cipher缓存
     */
    private static Map<String, Cipher> decryptorMap = null;
    private static Map<String, Cipher> getDecryptorMap(){
        if(decryptorMap == null){
            decryptorMap = new ConcurrentHashMap<>();
        }
        return decryptorMap;
    }

    /**
     * 加密字符串（可指定加密密钥）
     * @param input 待加密文本
     * @param key 密钥（可选）
     * @return
     * @throws Exception
     */
    public static String encrypt(String input, String... key){
        String seedKey = V.notEmpty(key)? key[0] : getDefaultKey();
        try{
            Cipher cipher = getEncryptor(seedKey);
            byte[] enBytes = cipher.doFinal(input.getBytes());
            return Base64.getEncoder().encodeToString(enBytes);
        }
        catch(Exception e){
            log.error("加密出错:"+input, e);
            return input;
        }
    }

    /**
     * 解密字符串
     * @param input 待解密文本
     * @param key 加密key（可选）
     * @return
     * @throws Exception
     */
    public static String decrypt(String input, String... key){
        if(V.isEmpty(input)){
            return input;
        }
        String seedKey = V.notEmpty(key)? key[0] : getDefaultKey();
        try{
            Cipher cipher = getDecryptor(seedKey);
            byte[] deBytes = Base64.getDecoder().decode(input.getBytes());
            return new String(cipher.doFinal(deBytes));
        }
        catch(Exception e){
            log.error("解密出错:"+input, e);
            return input;
        }
    }

    /***
     * 获取指定key的加密器
     * @param key 加密密钥
     * @return
     * @throws Exception
     */
    private static Cipher getEncryptor(String key) throws Exception{
        byte[] keyBytes = getKey(key);
        Cipher encryptor = getEncryptorMap().get(new String(keyBytes));
        if(encryptor == null){
            SecretKeySpec skeyspec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            encryptor = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptor.init(Cipher.ENCRYPT_MODE, skeyspec);
            // 放入缓存
            getEncryptorMap().put(key, encryptor);
        }
        return encryptor;
    }

    /***
     * 获取指定key的解密器
     * @param key 解密密钥
     * @return
     * @throws Exception
     */
    private static Cipher getDecryptor(String key) throws Exception{
        byte[] keyBytes = getKey(key);
        Cipher decryptor = getDecryptorMap().get(new String(keyBytes));
        if(decryptor == null){
            SecretKeySpec skeyspec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            decryptor = Cipher.getInstance(CIPHER_ALGORITHM);
            decryptor.init(Cipher.DECRYPT_MODE, skeyspec);
            // 放入缓存
            getDecryptorMap().put(key, decryptor);
        }
        return decryptor;
    }

    /***
     * 获取key，如非16位则调整为16位
     * @param seed
     * @return
     */
    private static byte[] getKey(String seed){
        if(V.isEmpty(seed)){
            seed = getDefaultKey();
        }
        if(seed.length() < 16){
            seed = seed + S.cut(KEY_FILL, 16-seed.length());
        }
        else if(seed.length() > 16){
            seed = S.cut(seed, 16);
        }
        return seed.getBytes();
    }

    /**
     * 默认加密seed（可通过配置文件）
     */
    private static String getDefaultKey(){
        String defaultKey = BaseConfig.getProperty("diboot.encryptor.seed");
        return V.notEmpty(defaultKey)? defaultKey : "DibootV2";
    }
}
