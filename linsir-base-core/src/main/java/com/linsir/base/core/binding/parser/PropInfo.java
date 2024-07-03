package com.linsir.base.core.binding.parser;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import com.linsir.base.core.util.BeanUtils;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: linsir
 * @date: 2022/3/19 15:02
 * @description: Bean相关信息缓存
 */
@Data
public class PropInfo implements Serializable {
    private static final long serialVersionUID = 5921667308129991326L;

    /**
     * 主键列
     */
    private String idColumn;
    /**
     * 主键字段类型
     */
    private Class<?> idFieldType;
    /**
     * 逻辑删除列
     */
    private String deletedColumn;
    /**
     * 列集合
     */
    private final List<String> columns = new ArrayList<>();
    /**
     * 字段-列的映射
     */
    private final Map<String, String> fieldToColumnMap = new HashMap<>();
    /**
     * 列-字段的映射
     */
    private final Map<String, String> columnToFieldMap = new HashMap<>();
    /**
     * 自动更新字段列表
     */
    private final List<String> fillUpdateFieldList = new ArrayList<>();

    public PropInfo(Class<?> beanClass){
        List<Field> fields = BeanUtils.extractAllFields(beanClass);
        if(V.notEmpty(fields)){
            for(Field fld : fields){
                String fldName = fld.getName();
                String columnName = null;
                TableField tableField = fld.getAnnotation(TableField.class);
                if(tableField != null){
                    if(tableField.exist() == false){
                        columnName = null;
                    }
                    else {
                        if (V.notEmpty(tableField.value())){
                            columnName = tableField.value();
                        }
                        else{
                            columnName = S.toSnakeCase(fldName);
                        }
                        FieldFill fill = tableField.fill();
                        if (FieldFill.UPDATE.equals(fill) || FieldFill.INSERT_UPDATE.equals(fill)) {
                            fillUpdateFieldList.add(fldName);
                        }
                    }
                }
                // 主键
                TableId tableId = fld.getAnnotation(TableId.class);
                if(tableId != null && this.idColumn == null){
                    if (V.notEmpty(tableId.value())){
                        columnName = tableId.value();
                    }
                    else if(columnName == null){
                        columnName = S.toSnakeCase(fldName);
                    }
                    this.idColumn = columnName;
                    this.idFieldType = fld.getType();
                }
                else{
                    TableLogic tableLogic = fld.getAnnotation(TableLogic.class);
                    if(tableLogic != null){
                        if (V.notEmpty(tableLogic.value())){
                            columnName = tableLogic.value();
                        }
                        else if(columnName == null){
                            columnName = S.toSnakeCase(fldName);
                        }
                        this.deletedColumn = columnName;
                    }
                }
                this.fieldToColumnMap.put(fldName, columnName);
                if(V.notEmpty(columnName)){
                    this.columnToFieldMap.put(columnName, fldName);
                    this.columns.add(columnName);
                }
            }
        }
    }

    /**
     * 根据列名获取字段
     * @return
     */
    public String getFieldByColumn(String columnName){
        if(V.isEmpty(this.columnToFieldMap)){
            return null;
        }
        return this.columnToFieldMap.get(columnName);
    }

    /**
     * 根据列名获取字段
     * @return
     */
    public String getColumnByField(String fieldName){
        if(V.isEmpty(this.fieldToColumnMap)){
            return null;
        }
        return this.fieldToColumnMap.get(fieldName);
    }

    /**
     * 获取主键属性名
     *
     * @return
     */
    public String getIdFieldName() {
        if (V.isEmpty(this.columnToFieldMap)) {
            return null;
        }
        return this.columnToFieldMap.get(idColumn);
    }
}
