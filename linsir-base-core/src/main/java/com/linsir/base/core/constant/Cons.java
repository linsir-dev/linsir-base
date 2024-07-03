package com.linsir.base.core.constant;

/**
 * @author linsir
 * @title: Cons
 * @projectName linsir
 * @description: 基础常量定义
 * @date 2022/3/4 12:50
 */
@Deprecated
public class Cons {

    /**
     * 默认字符集UTF-8
     */
    public static final String CHARSET_UTF8 = "UTF-8";
    /**
     * 逗号分隔符 ,
     */
    public static final String SEPARATOR_COMMA = ",";
    /**
     * 下划线分隔符_
     */
    public static final String SEPARATOR_UNDERSCORE = "_";
    /**
     * 冒号分隔符
     */
    public final static  String SEPARATOR_COLON = ":";
    /**
     * 斜杠路径分隔符
     */
    public final static  String SEPARATOR_SLASH = "/";
    /**
     * 竖线分隔符，or
     */
    public final static  String SEPARATOR_OR = "|";
    /**
     * 排序 - 降序标记
     */
    public static final String ORDER_DESC = "DESC";
    /**
     * 逻辑删除列名
     */
    public static final String COLUMN_IS_DELETED = "is_deleted";
    /**
     * 创建时间列名
     */
    public static final String COLUMN_CREATE_TIME = "create_time";

     /**
     * 默认字段名定义
     */
    public enum FieldName{
        /**
         * 主键属性名
         */
        id,
        /**
         * 默认的上级ID属性名
         */
        parentId,
        /**
         * 子节点属性名
         */
        children,
        /**
         * 逻辑删除标记字段
         */
        deleted,
        /**
         * 创建时间字段
         */
        createTime,
        /**
         * 更新时间
         */
        updateTime,
        /**
         * 创建人
         */
        createBy,
        /**
         * 更新人
         */
        updateBy
    }

    /**
     * 字典Entity相关属性名定义
     */
    public static final String FIELD_ITEM_NAME = "itemName";
    public static final String FIELD_ITEM_VALUE = "itemValue";
    public static final String COLUMN_ITEM_VALUE = "item_value";
    public static final String FIELD_TYPE = "type";


    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX_BEARER = "Bearer";
    /**
     * token header头名称
     */
    public static final String TOKEN_HEADER_NAME = "Authorization";

    /**
     * 启用/停用 状态字典定义
     */
    public enum ENABLE_STATUS{
        /**
         * 正常
         */
        A("正常"),
        /**
         * 停用
         */
        I("停用");

        private String label;
        ENABLE_STATUS(String label){
            this.label = label;
        }

        public String label(){
            return label;
        }
        public static String getLabel(String val){
            if(val.equalsIgnoreCase(A.name())){
                return A.label;
            }
            return I.label;
        }
    }

    /**
     * 成功/失败 结果状态字典定义
     */
    public enum RESULT_STATUS{
        /**
         * 正常
         */
        S("成功"),
        /**
         * 停用
         */
        F("失败");

        private String label;
        RESULT_STATUS(String label){
            this.label = label;
        }

        public String label(){
            return label;
        }
        public static String getLabel(String val){
            if(val.equalsIgnoreCase(S.name())){
                return S.label;
            }
            return F.label;
        }
    }
}
