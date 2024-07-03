package com.linsir.base.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linsir.base.core.constant.CommonConstant;
import com.linsir.base.core.enums.FieldName;
import com.linsir.base.core.util.BeanUtils;
import com.linsir.base.core.util.ContextHelper;
import com.linsir.base.core.util.JSON;
import lombok.Data;

import java.util.Map;

/**
 * @author linsir
 * @title: BaseEntity
 * @projectName linsir
 * @description: Entity基础父类
 * @date 2022/3/19 23:18
 */
@Data
public abstract class BaseEntity extends AbstractEntity<Long>{
    private static final long serialVersionUID = -6776198764539598100L;
    /** 租户编码*/
    @TableField(fill = FieldFill.INSERT)
    private String tenantCode ;

    /**
     * 默认逻辑删除标记，is_deleted=0有效
     */
    @TableLogic
    @JsonIgnore
    @TableField(value = CommonConstant.COLUMN_IS_DELETED, select = false)
    private boolean deleted = false;

    /***
     * Entity对象转为map
     * @return
     */
    public Map<String, Object> toMap(){
        String jsonStr = JSON.stringify(this);
        return JSON.toMap(jsonStr);
    }



    /**
     * 获取主键值
     * @return
     */
    @JsonIgnore
    public Object getPrimaryKeyVal(){
        String pk = ContextHelper.getIdFieldName(this.getClass());
        if(pk == null){
            return null;
        }
        if(FieldName.id.name().equals(pk)){
            return getId();
        }
        return BeanUtils.getProperty(this, pk);
    }

}
