package com.linsir.base.core.data.encrypt;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.linsir.base.core.binding.parser.ParserCache;
import com.linsir.base.core.util.S;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/25 11:46
 * @description：数据保护拦截器
 * @modified By：
 * @version: 0.0.1
 */
public class ProtectInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (args.length > 1) {
            MappedStatement mappedStatement = (MappedStatement) args[0];
            SqlCommandType sqlType = mappedStatement.getSqlCommandType();
            if (SqlCommandType.INSERT == sqlType || SqlCommandType.UPDATE == sqlType) {
                Object entity = args[1];
                Configuration config = mappedStatement.getConfiguration();
                if (entity instanceof Map) {
                    Set<Integer> set = new HashSet<>();
                    for (Map.Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet()) {
                        Object value = entry.getValue();
                        if (value == null || value instanceof Wrapper || set.contains(value.hashCode())) {
                            continue;
                        }
                        set.add(value.hashCode());
                        if (value instanceof List) {
                            for (Object obj : (List<?>) value) {
                                if (!encryptor(config, obj, IEncryptStrategy::encrypt)) {
                                    break;
                                }
                            }
                        } else {
                            encryptor(config, value, IEncryptStrategy::encrypt);
                        }
                    }
                } else {
                    encryptor(config, entity, IEncryptStrategy::encrypt);
                }
            }
            return invocation.proceed();
        } else {
            ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
            Field field = resultSetHandler.getClass().getDeclaredField("mappedStatement");
            field.setAccessible(true);
            List<?> list = (List<?>) invocation.proceed();
            if (list.isEmpty()) {
                return list;
            }
            Configuration config = ((MappedStatement) field.get(resultSetHandler)).getConfiguration();
            for (Object obj : list) {
                if (!encryptor(config, obj, IEncryptStrategy::decrypt)) {
                    break;
                }
            }
            return list;
        }
    }

    @Override
    public Object plugin(Object obj) {
        return obj instanceof Executor || obj instanceof ResultSetHandler ? Plugin.wrap(obj, this) : obj;
    }

    /**
     * 数据处理
     *
     * @param config 配置
     * @param entity 对象
     * @param fun    函数
     * @return 是否进行了处理
     */
    private boolean encryptor(Configuration config, Object entity, BiFunction<IEncryptStrategy, String, String> fun) {
        Map<String, IEncryptStrategy> fieldEncryptorMap = ParserCache.getFieldEncryptorMap(entity.getClass());
        if (!fieldEncryptorMap.isEmpty()) {
            MetaObject metaObject = config.newMetaObject(entity);
            fieldEncryptorMap.forEach((k, v) -> {
                String value = S.valueOf(metaObject.getValue(k));
                metaObject.setValue(k, value == null ? null : fun.apply(v, value));
            });
        }
        return !fieldEncryptorMap.isEmpty();
    }
}
