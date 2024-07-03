package com.linsir.base.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.linsir.base.core.binding.query.BindQuery;
import com.linsir.base.core.binding.query.Comparison;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;



/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 15:17
 * @description：数据字典实体
 * @modified By：
 * @version: 0.0.1
 */
@Data
@Accessors(chain = true)
public class Dictionary extends AbstractEntity<Long>{
    private static final long serialVersionUID = -7514603697434856747L;

    @NotNull(message = "上级ID不能为空，如无请设为0")
    @TableField
    private Long parentId = 0L;

    /**
     * 应用模块
     */
    @TableField
    private String appModule;

    /***
     * 数据字典类型
     */
    @NotNull(message = "数据字典类型不能为空！")
    @Length(max = 50, message = "数据字典类型长度超长！")
    @TableField
    private String type;

    /***
     * 数据字典项的显示名称
     */
    @NotNull(message = "数据字典项名称不能为空！")
    @Length(max = 100, message = "数据字典项名称长度超长！")
    @BindQuery(comparison = Comparison.LIKE)
    @TableField
    private String itemName;

    /***
     * 数据字典项的存储值（编码）
     */
    @Length(max = 100, message = "数据字典项编码长度超长！")
    @TableField
    private String itemValue;

    /***
     * 备注信息
     */
    @Length(max = 200, message = "数据字典备注长度超长！")
    @TableField
    private String description;

    /***
     * 排序号
     */
    @TableField
    private Integer sortId;

    /***
     * 是否为系统预置（预置不可删除）
     */
    @TableField("is_deletable")
    private Boolean isDeletable;

    /***
     * 是否可编辑
     */
    @TableField("is_editable")
    private Boolean isEditable;
}
