package com.linsir.base.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linsir.base.core.constant.CommonConstant;
import lombok.Data;

/**
 * @author linsir
 * @version 1.0.0
 * @title BaseModel
 * @description
 * @create 2024/7/6 13:09
 *
 * 有逻辑删除字段，没有租户概念
 *
 */

@Data
public class BaseModel extends AbstractEntity<Long> {

    private static final long serialVersionUID = 1L;


    @TableLogic
    @JsonIgnore
    @TableField(value = CommonConstant.COLUMN_IS_DELETED, select = false)
    private boolean deleted = false;

}
