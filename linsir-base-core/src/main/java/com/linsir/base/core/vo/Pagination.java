package com.linsir.base.core.vo;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linsir.base.core.binding.cache.BindingCacheManager;
import com.linsir.base.core.binding.parser.PropInfo;
import com.linsir.base.core.config.BaseConfig;
import com.linsir.base.core.constant.Cons;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 15:07
 * @description：分页 (属性以下划线开头以避免与提交参数字段冲突)
 * @modified By：
 * @version: Pagination
 */
@Data
@Slf4j
@Accessors(chain = true)
public class Pagination implements Serializable {
    private static final long serialVersionUID = 3725209192090503114L;

    /***
     * 当前页
     */
    private int pageIndex = 1;
    /***
     * 默认每页数量10
     */
    private int pageSize = BaseConfig.getPageSize();
    /***
     * count总数
     */
    private long totalCount = 0;
    /**
     * 默认排序
     */
    private static final String DEFAULT_ORDER_BY = Cons.FieldName.id.name() + ":" + Cons.ORDER_DESC;

    /**
     * 排序
     */
    private String orderBy = DEFAULT_ORDER_BY;

    private Class<?> entityClass;

    public Pagination() {
    }

    public Pagination(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    /***
     * 指定当前页数
     */
    public Pagination(int pageIndex) {
        setPageIndex(pageIndex);
    }

    public void setPageSize(int pageSize) {
        if (pageSize > 1000) {
            log.warn("分页pageSize过大，将被调整为默认限值，请检查调用是否合理！pageSize=" + pageSize);
            pageSize = 1000;
        }
        this.pageSize = pageSize;
    }

    /***
     * 获取总的页数
     * @return
     */
    public int getTotalPage() {
        if (totalCount <= 0) {
            return 0;
        }
        return (int) Math.ceil((float) totalCount / pageSize);
    }

    /**
     * 清空默认排序
     */
    public void clearDefaultOrder() {
        // 是否为默认排序
        if (isDefaultOrderBy()) {
            orderBy = null;
        }
    }

    /**
     * 是否为默认排序
     *
     * @return
     */
    @JsonIgnore
    public boolean isDefaultOrderBy() {
        return V.equals(orderBy, DEFAULT_ORDER_BY);
    }

    /**
     * 转换为IPage
     *
     * @param <T>
     * @return
     */
    public <T> Page<T> toPage() {
        List<OrderItem> orderItemList = null;
        // 解析排序
        if (V.notEmpty(this.orderBy)) {
            orderItemList = new ArrayList<>();
            // orderBy=shortName:DESC,age:ASC,birthdate
            String[] orderByFields = S.split(this.orderBy);
            for (String field : orderByFields) {
                V.securityCheck(field);
                if (field.contains(":")) {
                    String[] fieldAndOrder = S.split(field, ":");
                    String fieldName = fieldAndOrder[0];
                    String columnName = S.toSnakeCase(fieldName);
                    PropInfo propInfo = getEntityPropInfo();
                    if(propInfo != null){
                        // 前参数为字段名
                        if(propInfo.getFieldToColumnMap().containsKey(fieldName)){
                            columnName = propInfo.getFieldToColumnMap().get(fieldName);
                        }
                        // 前参数为列名
                        else if(propInfo.getColumnToFieldMap().containsKey(fieldName)){
                            columnName = fieldName;
                        }
                    }
                    if (Cons.ORDER_DESC.equalsIgnoreCase(fieldAndOrder[1])) {
                        orderItemList.add(OrderItem.desc(columnName));
                    } else {
                        orderItemList.add(OrderItem.asc(columnName));
                    }
                } else {
                    orderItemList.add(OrderItem.asc(S.toSnakeCase(field)));
                }
            }
        }
        Page<T> page = new Page<T>()
                .setCurrent(getPageIndex())
                .setSize(getPageSize())
                // 如果前端传递过来了缓存的总数，则本次不再count统计
                .setTotal(getTotalCount() > 0 ? -1 : getTotalCount());
        if (orderItemList != null) {
            page.addOrder(orderItemList);
        }
        return page;
    }

    /**
     * 当id不是主键的时候，默认使用创建时间排序
     *
     * @return
     */
    public String setDefaultCreateTimeOrderBy() {
        return this.orderBy = Cons.FieldName.createTime.name() + ":" + Cons.ORDER_DESC;
    }

    private PropInfo getEntityPropInfo(){
        if (this.entityClass != null) {
            return BindingCacheManager.getPropInfoByClass(this.entityClass);
        }
        return null;
    }
}
