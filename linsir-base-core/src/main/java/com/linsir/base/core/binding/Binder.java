package com.linsir.base.core.binding;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linsir.base.core.vo.Pagination;

import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 22:21
 * @description：绑定器统一调用入口类
 * @modified By：
 * @version: 0.0.1
 */
public class Binder {
    /**
     * 关联查询一条主表数据
     * @param queryWrapper
     * @param entityClazz 返回结果entity/vo类
     * @return
     * @throws Exception
     */
    public static <DTO,E> E joinQueryOne(QueryWrapper<DTO> queryWrapper, Class<E> entityClazz){
        return JoinsBinder.queryOne(queryWrapper, entityClazz);
    }

    /**
     * 关联查询符合条件的全部主表数据集合（不分页）
     * @param queryWrapper 调用QueryBuilder.to*QueryWrapper得到的实例
     * @param entityClazz 返回结果entity/vo类
     * @return
     * @throws Exception
     */
    public static <DTO,E> List<E> joinQueryList(QueryWrapper<DTO> queryWrapper, Class<E> entityClazz){
        return JoinsBinder.queryList(queryWrapper, entityClazz);
    }

    /**
     * 关联查询符合条件的指定页数据（分页）
     * @param queryWrapper 调用QueryBuilder.to*QueryWrapper得到的实例
     * @param entityClazz 返回结果entity/vo类
     * @param pagination 分页
     * @return
     * @throws Exception
     */
    public static <DTO,E> List<E> joinQueryList(QueryWrapper<DTO> queryWrapper, Class<E> entityClazz, Pagination pagination){
        return JoinsBinder.queryList(queryWrapper, entityClazz, pagination);
    }

    /**
     * 自动转换和绑定单个VO中的注解关联（禁止循环调用，多个对象请调用convertAndBind(voList, voClass)）
     * @param voClass 需要转换的VO class
     * @param <E>
     * @param <VO>
     * @return
     */
    public static <E, VO> VO convertAndBindRelations(E entity, Class<VO> voClass){
        return RelationsBinder.convertAndBind(entity, voClass);
    }

    /**
     * 自动转换和绑定多个VO中的注解关联
     * @param entityList 需要转换的VO list
     * @param voClass VO class
     * @param <E>
     * @param <VO>
     * @return
     */
    public static <E, VO> List<VO> convertAndBindRelations(List<E> entityList, Class<VO> voClass){
        return RelationsBinder.convertAndBind(entityList, voClass);
    }

    /**
     * 自动绑定单个VO的关联对象（禁止循环调用，多个对象请调用bind(voList)）
     * @param vo 需要注解绑定的对象
     * @return
     * @throws Exception
     */
    public static <VO> void bindRelations(VO vo){
        RelationsBinder.bind(vo);
    }

    /**
     * 自动绑定多个VO集合的关联对象
     * @param voList 需要注解绑定的对象集合
     * @return
     * @throws Exception
     */
    public static <VO> void bindRelations(List<VO> voList){
        RelationsBinder.bind(voList);
    }
}
