package com.linsir.base.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/25 18:53
 * @description：基础CRUD的父类Mapper
 * @modified By：
 * @version: 0.0.1
 */
public interface BaseCrudMapper<T> extends BaseMapper<T> {
    /***
     * 通过id撤回当前记录的删除状态
     * @param tableName
     * @param id
     * @return
     */
    @Update("UPDATE `${tableName}` SET is_deleted=0 WHERE id=#{id}")
    int cancelDeletedById(@Param("tableName") String tableName, @Param("id") Serializable id);
}
