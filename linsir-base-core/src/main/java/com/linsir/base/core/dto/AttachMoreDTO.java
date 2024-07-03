package com.linsir.base.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linsir.base.core.util.S;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/24 10:54
 * @description：用于加载关联数据传递的DTO格式
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
@Data
@Accessors(chain = true)
public class AttachMoreDTO implements Serializable {
    private static final long serialVersionUID = -3355099326235569721L;

    /**
     * 关联类型
     */
    @Deprecated
    public enum REF_TYPE {
        /**
         * 绑定的是对象
         */
        T,
        /**
         * 绑定的是字典
         */
        D
    }

    /**
     * 关联的类型
     */
    @Deprecated
    private REF_TYPE type;

    /**
     * <h3>需要查询的目标数据<br/>当label 为空时，则为获取字典，否则获取对象</h3>
     * target应为 实体名 或 字典的type{@link //Dictionary#type}
     */
    @NotNull(message = "查询类型不能为空！")
    private String target;

    /**
     * <h3>别名</h3>
     * 仅在批量获取选项时生效
     */
    private String alias;

    /**
     * <h3>需要查询的label字段</h3>
     * 当为空时，则为获取字典，label为{@link //Dictionary#itemName}
     */
    private String label;

    /**
     * <h3>需要查询的value字段</h3>
     * 当获取对象时，value默认为主键字段；<br/>
     * 当获取字典时，value为{@link //Dictionary#itemValue}
     */
    private String value;

    /**
     * <h3>需要查询的ext字段</h3>
     * 当获取字典时，ext为表中{@link //Dictionary#extdata}
     */
    private String ext;

    /**
     * <h3>筛选条件</h3>
     * 可重写{@link //BaseController#buildAttachMoreCondition(AttachMoreDTO, QueryWrapper, Function)}进行自定义筛选条件规则
     */
    private Map<String, Object> condition;

    /**
     * <h3>关键字</h3>
     * 用于前端远程搜索label
     */
    private String keyword;

    /**
     * <h3>排序</h3>
     * 示例 `id:DESC,age:ASC`
     */
    private String orderBy;

    /**
     * <h3>父级关联属性</h3>
     * 存储关联数据的属性
     */
    private String parent;

    /**
     * <h3>是否构建树</h3>
     * 仅且第一层生效
     */
    private boolean tree;

    /**
     * <h3>异步加载</h3>
     * 推荐异步加载，默认为true；为false时会同步加载下一级，且当为树时会加载整个树
     */
    private boolean lazy = true;

    /**
     * <h3>下一层</h3>
     */
    private AttachMoreDTO next;

    @JsonIgnore
    public String getTargetClassName(){
        return S.capFirst(S.toLowerCaseCamel(this.target));
    }
}
