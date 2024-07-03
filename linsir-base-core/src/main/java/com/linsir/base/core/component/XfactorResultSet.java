package com.linsir.base.core.component;


import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

/**
 * @author yuxiaolin
 * @title: TenantInnerInterceptor
 * @projectName lins
 * @description:  脱敏插件
 *
 * 默认情况下，MyBatis 允许使用插件来拦截的方法调用包括以下四个对象的方法：
 *
 * Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
 * ParameterHandler (getParameterObject, setParameters)
 * ResultSetHandler (handleResultSets, handleOutputParameters)
 * StatementHandler (prepare, parameterize, batch, update, query)
 * 以上内容在官网包括网上一搜一大把，但是用的时候，应该怎么选择，什么时候用哪种，怎么入手呢？
 *
 * 我一开始想用的时候，也不知道什么时候拦截哪种对象，后来我就写了一个简单的demo，
 * 大家在用mybatis的时候，无非就是crud操作，那么我就提供四个plugin，
 * 分别来拦截Executor、ParameterHandler、ResultSetHandler、StatementHandler ；
 * 然后提供了一个controller暴露了五个接口分别是getUserInfo、listUserInfo、addUser、updateUser、deleteUser，
 * 来看下都走了那几个plugin（demo我会上传到码云上，项目架构是springboot+mybatis+mybatis-plus，数据库我用的是postgresql-14），
 * 我认为这五个接口涵盖了我们在开发中90%的场景，根据打印的日志得到的结论是：
 *
 * 两种查询、新增、修改、删除五个方法都会经过StatementHandler、ParameterHandler
 * 两种查询（单个查询、列表查询）都会经过Executor、StatementHandler、ParameterHandler、ResultSetHandler
 * 所以根据上面的结论，我们就可以来确定我们在开发中用哪种plugin，参考场景如下：
 *
 * 如果想改入参，比如postgresql据库字段值大小写敏感，那么我可以在ParameterHandler里面获取到入参，然后toUpperCase();
 * 如果想改sql语句，比如改postgresql的schema，那么我可以在StatementHandler（prepare）里面获取到connection修改；若是查询场景也可以在Executor的query方法中获取connection修改；
 * 如果想对数据进行脱敏处理，比如查询场景下的，查出的结果中身份证显示前4位后4位中间***填充，那么我们可以在ResultSetHandler的进行脱敏处理。
 *
 *
 *
 *
 * @date 2021/12/15 10:01 上午
 */

public class XfactorResultSet implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }
}
