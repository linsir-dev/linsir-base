package com.linsir.base.core.vo.jsonResults;

import com.linsir.base.core.code.BaseCode;
import com.linsir.base.core.code.ICode;
import com.linsir.base.core.plugin.JsonResultFilter;
import com.linsir.base.core.util.ContextHelper;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import com.linsir.base.core.vo.Pagination;
import com.linsir.base.core.vo.Status;
import com.linsir.base.core.vo.results.R;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 23:33
 * @description：JSON返回结果
 * @modified By：
 * @version: 0.0.1
 */
@SuppressWarnings("JavaDoc")
public class JsonResult<T> implements Serializable, R<Integer,String,T> {
    @Serial
    private static final long serialVersionUID = 1001L;

    /***
     * 状态码
     */
    private int code;
    /***
     * 消息内容
     */
    private String message;
    /***
     * 返回结果数据
     */
    private T data;




    /**
     * 默认成功，无返回数据
     */
    public JsonResult(){
    }

    /**
     * 成功或失败
     */
    public JsonResult(boolean ok){
        this(ok? BaseCode.SUCCESS:BaseCode.FAIL_OPERATION);
    }

    /**
     * 默认成功，有返回数据
     */
    public JsonResult(T data){
        this.code = Status.OK.code();
        this.message = Status.OK.label();
        initMsg(null);
        this.data = data;
    }

    /**
     * 默认成功，有返回数据、及附加提示信息
     */
    public JsonResult(T data, String additionalMsg){
        this.code = BaseCode.SUCCESS.getCode();
        this.message = BaseCode.SUCCESS.getMsg();
        initMsg(additionalMsg);
        this.data = data;
    }

    /***
     * 非成功，指定状态
     * @param code
     */
    public JsonResult(ICode code){
        this.code = code.getCode();
        this.message = code.getMsg();
        initMsg(null);
        this.data = null;
    }

    /***
     * 非成功，指定状态及附加提示信息
     * @param code
     * @param additionalMsg
     */
    public JsonResult(ICode code, String additionalMsg){
        this.code = code.getCode();
        this.message = code.getMsg();
        initMsg(additionalMsg);
        this.data = null;
    }

    /**
     * 非成功，指定状态、返回数据
     * @param code
     * @param data
     */
    public JsonResult(ICode code, T data){
        this.code = code.getCode();
        this.message = code.getMsg();
        initMsg(null);
        this.data = data;
    }

    /**
     * 非成功，指定状态、返回数据、及附加提示信息
     */
    public JsonResult(ICode code, T data, String additionalMsg){
        this.code = code.getCode();
        this.message = code.getMsg();
        initMsg(additionalMsg);
        this.data = data;
    }

    /***
     * 自定义JsonResult
     * @param code
     * @param message
     * @param data
     */
    public JsonResult(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 设置status，如果msg为空则msg设置为status.label
     * @param code
     * @return
     */
    public JsonResult<T> status(ICode code){
        this.code = code.getCode();
        if(this.message == null){
            this.message = code.getMsg();
        }
        return this;
    }

    /**
     * 设置返回数据
     * @param data
     * @return
     */
    public JsonResult<T> data(T data){
        this.data = data;
        return this;
    }

    /**
     * 设置msg
     * @param additionalMsg
     * @return
     */
    public JsonResult<T> msg(String additionalMsg){
        initMsg(additionalMsg);
        return this;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
    public T getData() {
        return filterJsonResultData(data);
    }

    @Override
    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setData(T t) {
        this.data = t;
    }

    /***
     * 绑定分页信息
     * @param pagination
     */
    @SuppressWarnings("unchecked")
    public PagingJsonResult<T> bindPagination(Pagination pagination){
        return new PagingJsonResult<T>(this, pagination);
    }

    /**
     * 赋值msg（去掉重复前缀以支持与BusinessException嵌套使用）
     * @param additionalMsg
     */
    private void initMsg(String additionalMsg){
        if(V.notEmpty(additionalMsg)){
            if(S.startsWith(additionalMsg, this.message)){
                this.message = additionalMsg;
            }
            else{
                this.message += ": " + additionalMsg;
            }
        }
    }

    /**
     * 判断结果是否OK
     * @return
     */
    public boolean isOK(){
        return this.code == Status.OK.code();
    }

    /***
     * 请求处理成功
     */
    public static <T> JsonResult<T> OK(){
        return new JsonResult<>(BaseCode.SUCCESS);
    }
    /***
     * 请求处理成功
     */
    public static <T> JsonResult<T> OK(T data){
        return new JsonResult<>(BaseCode.SUCCESS, data);
    }

    /***
     * 部分成功（一般用于批量处理场景，只处理筛选后的合法数据）
     */
    public static <T> JsonResult<T> WARN_PARTIAL_SUCCESS(String msg){
        return new JsonResult<T>(BaseCode.WARN_PARTIAL_SUCCESS).msg(msg);
    }
    /***
     * 有潜在的性能问题
     */
    public static <T> JsonResult<T> WARN_PERFORMANCE_ISSUE(String msg){
        return new JsonResult<T>(BaseCode.WARN_PERFORMANCE_ISSUE).msg(msg);
    }
    /***
     * 传入参数不对
     */
    public static <T> JsonResult<T> FAIL_INVALID_PARAM(String msg){
        return new JsonResult<T>(BaseCode.FAIL_INVALID_PARAM).msg(msg);
    }
    /***
     * Token无效或已过期
     */
    public static <T> JsonResult<T> FAIL_INVALID_TOKEN(String msg){
        return new JsonResult<T>(BaseCode.FAIL_INVALID_TOKEN).msg(msg);
    }
    /***
     * 没有权限执行该操作
     */
    public static <T> JsonResult<T> FAIL_NO_PERMISSION(String msg){
        return new JsonResult<T>(BaseCode.FAIL_NO_PERMISSION).msg(msg);
    }
    /***
     * 请求资源不存在
     */
    public static <T> JsonResult<T> FAIL_NOT_FOUND(String msg){
        return new JsonResult<T>(BaseCode.FAIL_NOT_FOUND).msg(msg);
    }
    /***
     * 数据校验不通过
     */
    public static <T> JsonResult<T> FAIL_VALIDATION(String msg){
        return new JsonResult<T>(BaseCode.FAIL_VALIDATION).msg(msg);
    }
    /***
     * 操作执行失败
     */
    public static <T> JsonResult<T> FAIL_OPERATION(String msg){
        return new JsonResult<T>(BaseCode.FAIL_OPERATION).msg(msg);
    }
    /***
     * 系统异常
     */
    public static <T> JsonResult<T> FAIL_EXCEPTION(String msg){
        return new JsonResult<T>(BaseCode.FAIL_EXCEPTION).msg(msg);
    }
    /***
     * 服务不可用
     */
    public static <T> JsonResult<T> FAIL_FAIL_REQUEST_TIMEOUT(String msg){
        return new JsonResult<T>(BaseCode.FAIL_REQUEST_TIMEOUT).msg(msg);
    }
    /***
     * 服务不可用
     */
    public static <T> JsonResult<T> FAIL_SERVICE_UNAVAILABLE(String msg){
        return new JsonResult<T>(BaseCode.FAIL_SERVICE_UNAVAILABLE).msg(msg);
    }

    /***
     * 认证不通过
     */
    public static <T> JsonResult<T> FAIL_AUTHENTICATION(String msg){
        return new JsonResult<T>(BaseCode.FAIL_AUTHENTICATION).msg(msg);
    }

    /**
     * 过滤jsonResult结果，用于全局忽略某些字段等场景
     * @param data
     * @param <T>
     * @return
     */
    private static boolean jsonResultFilterChecked = true;
    private static JsonResultFilter jsonResultFilter;
    private static <T> T filterJsonResultData(T data){
        // 不启用过滤
        if(jsonResultFilterChecked && jsonResultFilter == null){
            return data;
        }
        if(!jsonResultFilterChecked){
            jsonResultFilter = ContextHelper.getBean(JsonResultFilter.class);
            jsonResultFilterChecked = true;
        }
        if(jsonResultFilter != null){
            jsonResultFilter.filterData(data);
        }
        return data;
    }
}