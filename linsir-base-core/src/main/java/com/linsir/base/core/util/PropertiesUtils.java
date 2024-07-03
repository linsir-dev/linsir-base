package com.linsir.base.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * @author linsir
 * @title: PropertiesUtils
 * @projectName linsir
 * @description: 配置文件工具类
 * @date 2022/3/11 1:33
 */
@Slf4j
public class PropertiesUtils {

    /** 从spring启动中获取环境*/
    private static Environment environment;

    /**
     * 绑定Environment
     * @param env
     */
    public static void bindEnvironment(Environment env){
        environment = env;
    }

    /***
     *  读取配置项的值
     * @param key
     * @return
     */
    public static String get(String key){
        if(environment == null){
            try{
                environment = ContextHelper.getApplicationContext().getEnvironment();
            }
            catch (Exception e){
                log.warn("无法获取Environment，参数配置可能不生效");
            }
        }
        // 获取配置值
        if(environment == null){
            log.warn("无法获取上下文Environment，请在Spring初始化之后调用!");
            return null;
        }
        String value = environment.getProperty(key);
        // 任何password相关的参数需解密
        boolean isSensitiveConfig = key.contains(".password") || key.contains(".secret");
        if(value != null && isSensitiveConfig){
            value = Encryptor.decrypt(value);
        }
        return value;
    }

    /***
     *  读取int型的配置项
     * @param key
     * @return
             */
    public static Integer getInteger(String key){
        // 获取配置值
        String value = get(key);
        if(V.notEmpty(value)){
            return Integer.parseInt(value);
        }
        return null;
    }

    /***
     * 读取boolean值的配置项
     */
    public static boolean getBoolean(String key) {
        // 获取配置值
        String value = get(key);
        if(V.notEmpty(value)){
            return V.isTrue(value);
        }
        return false;
    }
}
