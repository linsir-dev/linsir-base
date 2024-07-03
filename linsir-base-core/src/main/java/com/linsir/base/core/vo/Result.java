package com.linsir.base.core.vo;

import com.linsir.base.core.code.BaseCode;
import com.linsir.base.core.code.ICode;
import com.linsir.base.core.plugin.ResultFilter;
import com.linsir.base.core.util.ContextHelper;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口返回数据格式
 * @author scott
 * @email
 * @date  2019年1月19日
 */
@Deprecated
@Data
public class Result<T> implements R<ICode,T>,Serializable {

	private static final long serialVersionUID = 1L;

	private ICode head;

	private  T body;

	private long timestamp = System.currentTimeMillis();

	/**
	 * 默认成功，无返回数据
	 */
	public Result() {
	}

	/**
	 * 成功或者失败
	 * @param ok
	 */
	public Result(boolean ok){
		this(ok ? BaseCode.SUCCESS:BaseCode.FAIL_OPERATION);
	}

	/**
	 * 默认成功，有数据返回
	 * @param body
	 */
	public Result(T body){
		this.head = BaseCode.SUCCESS;
		this.body = body;
	}

	/**
	 * 默认成功，有返回数据、及附加提示信息
	 * @param body
	 * @param additionalMsg
	 */
	public Result(T body,String additionalMsg)
	{
		this.head = BaseCode.SUCCESS;
		initMsg(additionalMsg);
		this.body = body;
	}

	/**
	 * 非成功，指定状态
	 * @param head
	 */
	public Result(ICode head)
	{
		this.head = head;
	}

	/**
	 * 非成功，指定状态及附加提示信息
	 * @param code
	 * @param additionalMsg
	 */
	public Result(ICode code,String additionalMsg)
	{
		this.head = code;
		initMsg(additionalMsg);
		this.body =null;
	}


	/**
	 * 非成功，指定状态、返回数据
	 * @param head
	 * @param body
	 */
	public Result(ICode head,T body){
		this.head = head;
		this.body =body;
	}

	/**
	 * 非成功，指定状态、返回数据、及附加提示信息
	 * @param head
	 * @param body
	 * @param additionalMsg
	 */
	public Result(ICode head,T body,String additionalMsg)
	{
		this.head = head;
		initMsg(additionalMsg);
		this.body = body;
	}


	/**
	 * 设置code,如果msg为空则msg
	 * @param code
	 * @return
	 */
	public Result<T> code(ICode code)
	{
		this.head.setCode(code.getCode());
		if (this.head.getMsg()==null)
		{
			this.head.setMessage(code.getMsg());
		}
		this.head.setStatus(code.status());
		return this;
	}

	/**
	 * 设置返回数据
	 * @param body
	 * @return
	 */
	public Result<T> body(T body)
	{
		this.body =body;
		return this;
	}


	/**
	 * 设置返回消息
	 * @param additionalMsg
	 * @return
	 */
    public Result<T> msg(String additionalMsg)
	{
		initMsg(additionalMsg);
		return  this;
	}

	/***********************************************************/

	@Override
	public ICode getHead() {
		return this.head;
	}

	@Override
	public T getBody() {
		return this.body;
	}

	public int getHeadCode()
	{
		return this.head.getCode();
	}

	public String getHeadMsg()
	{
		return this.head.getMsg();
	}

	public boolean getHeadStaut()
	{
		return  this.head.status();
	}

	/***********************************************************/



	/**
	 * @Author linsir
	 * @Description 绑定分页
	 * @Date 14:50 2022/9/13
	 * @Param
	 * @return
	 **/


	/**
	 * 赋值msg（去掉重复前缀以支持与BusinessException嵌套使用）
	 * @param additionalMsg
	 */
	private void initMsg(String additionalMsg){

		if (V.notEmpty(additionalMsg))
		{
			if (S.startsWith(additionalMsg,this.head.getMsg()))
			{
				this.head.setMessage(additionalMsg);
			}
			else
			{
				this.head.setMessage(this.head.getMsg() + ":"+additionalMsg);
			}

		}
	}


	/**
	 * 判断结果是否OK
	 * @return
	 */
	public boolean isOK(){
		return this.head.getCode() == BaseCode.SUCCESS.getCode();
	}


	/**
	 * @Author linsir
	 * @Description 请求处理成功
	 * @Date 14:56 2022/9/13
	 * @Param []
	 * @return com.linsir.core.vo.Result<T>
	 **/
	public static <T> Result<T> SUCCESS()
	{
		return  new Result<>(BaseCode.SUCCESS);
	}


	/**
	 * 请求处理成功
	 * @param body
	 * @return
	 * @param <T>
	 */
	public static <T> Result<T> SUCCESS(T body){
		return new Result<>(BaseCode.SUCCESS, body);
	}


	/**
	 * 部分业务成功
	 * @return
	 * @param <T>
	 */
	public static <T> Result<T> WARN_PARTIAL_SUCCESS()
	{
		return new Result<>(BaseCode.WARN_PARTIAL_SUCCESS);
	}

	/***
	 * 有潜在的性能问题
	 */
	public static <T> Result<T> WARN_PERFORMANCE_ISSUE(String msg){
		return new Result<T>(BaseCode.WARN_PERFORMANCE_ISSUE).msg(msg);
	}
	/***
	 * 传入参数不对
	 */
	public static <T> Result<T> FAIL_INVALID_PARAM(String msg){
		return new Result<T>(BaseCode.FAIL_INVALID_PARAM).msg(msg);
	}
	/***
	 * Token无效或已过期
	 */
	public static <T> Result<T> FAIL_INVALID_TOKEN(String msg){
		return new Result<T>(BaseCode.FAIL_INVALID_TOKEN).msg(msg);
	}
	/***
	 * 没有权限执行该操作
	 */
	public static <T> Result<T> FAIL_NO_PERMISSION(String msg){
		return new Result<T>(BaseCode.FAIL_NO_PERMISSION).msg(msg);
	}
	/***
	 * 请求资源不存在
	 */
	public static <T> Result<T> FAIL_NOT_FOUND(String msg){
		return new Result<T>(BaseCode.FAIL_NOT_FOUND).msg(msg);
	}
	/***
	 * 数据校验不通过
	 */
	public static <T> Result<T> FAIL_VALIDATION(String msg){
		return new Result<T>(BaseCode.FAIL_VALIDATION).msg(msg);
	}
	/***
	 * 操作执行失败
	 */
	public static <T> Result<T> FAIL_OPERATION(String msg){
		return new Result<T>(BaseCode.FAIL_OPERATION).msg(msg);
	}
	/***
	 * 系统异常
	 */
	public static <T> Result<T> FAIL_EXCEPTION(String msg){
		return new Result<T>(BaseCode.FAIL_EXCEPTION).msg(msg);
	}
	/***
	 * 服务不可用
	 */
	public static <T> Result<T> FAIL_FAIL_REQUEST_TIMEOUT(String msg){
		return new Result<T>(BaseCode.FAIL_REQUEST_TIMEOUT).msg(msg);
	}
	/***
	 * 服务不可用
	 */
	public static <T> Result<T> FAIL_SERVICE_UNAVAILABLE(String msg){
		return new Result<T>(BaseCode.FAIL_SERVICE_UNAVAILABLE).msg(msg);
	}

	/***
	 * 认证不通过
	 */
	public static <T> Result<T> FAIL_AUTHENTICATION(String msg){
		return new Result<T>(BaseCode.FAIL_AUTHENTICATION,msg);
	}


	/**
	 *
	 * 过滤jsonResult结果，用于全局忽略某些字段等场景
	 */
	private static boolean resultFilterChecked = false;
	private static ResultFilter resultFilter;

	private static <T> T filterResultData(T data)
	{
		if (resultFilterChecked && resultFilter==null)
		{
			return data;
		}
		if (!resultFilterChecked){
			resultFilter = ContextHelper.getBean(ResultFilter.class);
		}
		if (resultFilter!=null)
		{
			resultFilter.filterData(data);
		}
		return data;
	}
	
}