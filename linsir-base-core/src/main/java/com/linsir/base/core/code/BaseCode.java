package com.linsir.base.core.code;

/**
 * @author ：linsir
 * @date ：Created in 2022/6/11 14:21
 * @description：基础统一 代码
 * @modified By：
 * @version: 0.0.1
 */
public enum BaseCode implements ICode,Cloneable{
    
    /*成功返回的代码*/

    SUCCESS(200, "操作成功", Boolean.TRUE),

    /*部分成功（一般用于批量处理场景，只处理筛选后的合法数据）*/
    WARN_PARTIAL_SUCCESS(1001, "部分成功",Boolean.TRUE),
    /*有潜在的性能问题*/
    WARN_PERFORMANCE_ISSUE(1002, "潜在的性能问题",Boolean.FALSE),

    /*传入参数不对*/
    FAIL_INVALID_PARAM(4000, "请求参数不匹配",Boolean.FALSE),

    FAIL_INVALID_TOKEN(4001, "Token无效或已过期",Boolean.FALSE),

    TOKEN_ACCESS_FORBIDDEN(301, "token已被禁止访问",Boolean.FALSE),

    FAILED(400, "系统正忙，请稍后再试", Boolean.FALSE),
    UNAUTHORIZED(401, "没有认证", Boolean.FALSE),
    PAYMENT_REQUIRED(402, "Payment Required", Boolean.FALSE),
    FORBIDDEN(403, "访问被拒绝", Boolean.FALSE),
    NOT_FOUND(404, "Not Found", Boolean.FALSE),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", Boolean.FALSE),
    NOT_ACCEPTABLE(406, "Not Acceptable", Boolean.FALSE),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required", Boolean.FALSE),
    REQUEST_TIMEOUT(408, "Request Timeout", Boolean.FALSE),
    CONFLICT(409, "Conflict", Boolean.FALSE),
    GONE(410, "Gone", Boolean.FALSE),
    LENGTH_REQUIRED(411, "Length Required", Boolean.FALSE),
    PRECONDITION_FAILED(412, "Precondition Failed", Boolean.FALSE),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large", Boolean.FALSE),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long", Boolean.FALSE),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", Boolean.FALSE),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable", Boolean.FALSE),
    EXPECTATION_FAILED(417, "Expectation Failed", Boolean.FALSE),
    I_AM_A_TEAPOT(418, "I'm a teapot", Boolean.FALSE),


    /***
     * 没有权限执行该操作
     */
    FAIL_NO_PERMISSION(4003, "无权执行该操作",Boolean.FALSE),

    /***
     * 请求资源不存在
     */
    FAIL_NOT_FOUND(4004, "请求资源不存在",Boolean.FALSE),

    /***
     * 数据校验不通过
     */
    FAIL_VALIDATION(4005, "数据校验不通过",Boolean.FALSE),

    /***
     * 操作执行失败
     */
    FAIL_OPERATION(4006, "操作执行失败",Boolean.FALSE),

    /**
     * 请求连接超时
     */
    FAIL_REQUEST_TIMEOUT(4008, "请求连接超时",Boolean.FALSE),

    /***
     * 认证不通过（用户名密码错误等认证失败场景）
     */
    FAIL_AUTHENTICATION(4009, "认证不通过",Boolean.FALSE),

    /***
     * 系统异常
     */
    FAIL_EXCEPTION(5000, "系统异常",Boolean.FALSE),

    /**
     * 服务不可用
     */
    FAIL_SERVICE_UNAVAILABLE(5003, "服务不可用",Boolean.FALSE),


    FILE_TYPE_ERROR(5004,"文件类型错误",Boolean.FALSE);
    ;


    private  int code;
    private String msg;
    private Boolean status;

    BaseCode(int code, String msg, Boolean status){
        this.code = code;
        this.msg = msg;
        this.status = status;
    }


    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public boolean status() {
        return this.status;
    }

    @Override
    public void setMessage(String message) {
        this.msg = message;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public void setStatus(boolean status) {
       this.status = status;
    }
}
