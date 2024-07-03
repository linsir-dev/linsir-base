package com.linsir.base.core.binding.query.dynamic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.base.core.binding.helper.ServiceAdaptor;
import com.linsir.base.core.binding.parser.ParserCache;
import com.linsir.base.core.exception.InvalidUsageException;
import com.linsir.base.core.util.ContextHelper;
import com.linsir.base.core.vo.Pagination;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 12:41
 * @description：动态查询wrapper
 * @modified By：
 * @version:
 */
public class ExtQueryWrapper <DTO,E> extends QueryWrapper<DTO> {
    /**
     * 主实体class
     */
    @Getter
    @Setter
    private Class<E> mainEntityClass;

    /**
     * 获取entity表名
     * @return
     */
    public String getEntityTable(){
        return ParserCache.getEntityTableName(getMainEntityClass());
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    public E queryOne(Class<E> entityClazz){
        this.mainEntityClass = entityClazz;
        IService<E> iService = ContextHelper.getIServiceByEntity(this.mainEntityClass);
        if(iService != null){
            return ServiceAdaptor.getSingleEntity(iService, this);
        }
        else{
            throw new InvalidUsageException("查询对象无BaseService/IService实现: "+this.mainEntityClass.getSimpleName());
        }
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    public List<E> queryList(Class<E> entityClazz){
        this.mainEntityClass = entityClazz;
        IService iService = ContextHelper.getIServiceByEntity(entityClazz);
        if(iService != null){
            return ServiceAdaptor.queryList(iService, this);
        }
        else{
            throw new InvalidUsageException("查询对象无BaseService/IService实现: "+entityClazz.getSimpleName());
        }
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    public List queryList(Class<E> entityClazz, Pagination pagination){
        this.mainEntityClass = entityClazz;
        IService iService = ContextHelper.getIServiceByEntity(entityClazz);
        if(iService != null){
            return ServiceAdaptor.queryList(iService, this, pagination, entityClazz);
        }
        else{
            throw new InvalidUsageException("查询对象无BaseService/IService实现: "+entityClazz.getSimpleName());
        }
    }
}
