package com.linsir.base.core.code;

/**
 * @author ：linsir
 * @date ：Created in 2022/6/8 19:37
 * @description：统一返回 统一接口模式
 * @modified By：
 * @version: 0.0.1
 */
public interface ICode {

    /**
     * 返回响应码
     *
     * @return 响应码
     */
    int getCode();

    /**
     * 返回响应信息
     *
     * @return
     */
    String getMsg();

    /**
     * 状态标识
     *
     * @return
     */
    boolean status();

    /**
     * 设置信息
     *
     * @param message
     */
    default void setMessage(String message) {
    }

    /**
     * 设置 code
     * @param code
     */
    default void  setCode(int code){
    }


    /**
     * 设置 状态
     * @param status
     */
    default void  setStatus(boolean status) {

    }

    /**
     * 填充信息
     *
     * @param field
     * @return
     */
    default ICode fillMessage(String field) {
        return this;
    }
}
