package com.linsir.base.core.binding.query.dynamic;

import com.linsir.base.core.binding.JoinsBinder;
import com.linsir.base.core.binding.parser.ParserCache;
import com.linsir.base.core.vo.Pagination;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 13:22
 * @description：动态查询wrapper
 * @modified By：
 * @version:
 */
public class DynamicJoinQueryWrapper<DTO,E> extends ExtQueryWrapper<DTO,E> {
    public DynamicJoinQueryWrapper(Class<DTO> dtoClass, Collection<String> fields){
        this.dtoClass = dtoClass;
        this.fields = fields;
    }

    /**
     * DTO类
     */
    @Getter
    private Class<DTO> dtoClass;
    /**
     * 字段
     */
    private Collection<String> fields;

    /**
     * dto字段和值
     */
    public List<AnnoJoiner> getAnnoJoiners(){
        return ParserCache.getAnnoJoiners(this.dtoClass, fields);
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    @Override
    public E queryOne(Class<E> entityClazz){
        return JoinsBinder.queryOne(this, entityClazz);
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    @Override
    public List<E> queryList(Class<E> entityClazz){
        return JoinsBinder.queryList(this, entityClazz);
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    @Override
    public List<E> queryList(Class<E> entityClazz, Pagination pagination){
        return JoinsBinder.queryList(this, entityClazz, pagination);
    }
}
