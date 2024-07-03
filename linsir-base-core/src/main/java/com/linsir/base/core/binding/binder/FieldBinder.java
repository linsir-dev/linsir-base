package com.linsir.base.core.binding.binder;


import com.linsir.base.core.binding.annotation.BindField;
import com.linsir.base.core.binding.binder.remote.RemoteBindingManager;
import com.linsir.base.core.constant.Cons;
import com.linsir.base.core.exception.InvalidUsageException;
import com.linsir.base.core.util.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 22:35
 * @description：关联字段绑定
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
public class FieldBinder<T> extends BaseBinder<T>{

    /**
     * VO对象绑定赋值的属性名列表
     */
    protected List<String> annoObjectSetterPropNameList;
    /**
     * DO/Entity对象对应的getter取值属性名列表
     */
    protected List<String> referencedGetterFieldNameList;

    /***
     * 构造方法
     * @param entityClass
     * @param voList
     */
    public FieldBinder(Class<T> entityClass, List voList){
        super(entityClass, voList);
    }

    /***
     * 构造方法
     * @param annotation
     * @param voList
     */
    public FieldBinder(BindField annotation, List voList){
        super(annotation.entity(), voList);
    }

    /***
     * 指定VO绑定属性赋值的setter和DO/Entity取值的getter方法
     * @param toVoSetter VO中调用赋值的setter方法
     * @param <T1> VO类型
     * @param <T2> DO类型
     * @param <R> set方法参数类型
     * @return
     */
    public <T1,T2,R> FieldBinder<T> link(IGetter<T2> fromDoGetter, ISetter<T1, R> toVoSetter){
        return link(BeanUtils.convertToFieldName(fromDoGetter), BeanUtils.convertToFieldName(toVoSetter));
    }

    /***
     * 指定VO绑定赋值的setter属性名和DO/Entity取值的getter属性名
     * @param toVoField VO中调用赋值的setter属性名
     * @return
     */
    public FieldBinder<T> link(String fromDoField, String toVoField){
        if(annoObjectSetterPropNameList == null){
            annoObjectSetterPropNameList = new ArrayList<>(4);
        }
        annoObjectSetterPropNameList.add(toVoField);
        if(referencedGetterFieldNameList == null){
            referencedGetterFieldNameList = new ArrayList<>(4);
        }
        referencedGetterFieldNameList.add(fromDoField);
        return this;
    }

    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(V.isEmpty(refObjJoinCols)){
            throw new InvalidUsageException("调用错误：无法从condition中解析出字段关联.");
        }
        if(referencedGetterFieldNameList == null){
            throw new InvalidUsageException("调用错误：字段绑定必须指定字段field");
        }
        // 直接关联
        if(middleTable == null){
            List<Map<String, Object>> mapList = null;
            this.simplifySelectColumns();
            super.buildQueryWrapperJoinOn();
            // 查询条件为空时不进行查询
            if (queryWrapper.isEmptyOfNormal()) {
                return;
            }
            if(V.isEmpty(this.module)){
                // 本地查询获取匹配结果的mapList
                mapList = getMapList(queryWrapper);
            }
            else{
                // 远程调用获取
                mapList = RemoteBindingManager.fetchMapList(module, remoteBindDTO);
            }
            if(V.isEmpty(mapList)){
                return;
            }
            // 将结果list转换成map
            Map<String, Map<String, Object>> key2DataMap = this.buildMatchKey2ResultMap(mapList);
            // 遍历list并赋值
            for(Object annoObject : annoObjectList){
                String matchKey = buildMatchKey(annoObject);
                setFieldValueToTrunkObj(key2DataMap, annoObject, matchKey);
            }
        }
        else{
            if(refObjJoinCols.size() > 1){
                throw new InvalidUsageException(NOT_SUPPORT_MSG);
            }
            // 提取注解条件中指定的对应的列表
            Map<String, List> trunkObjCol2ValuesMap = super.buildTrunkObjCol2ValuesMap();
            // 中间表查询结果map
            Map<String, Object> middleTableResultMap = middleTable.executeOneToOneQuery(trunkObjCol2ValuesMap);
            if(V.isEmpty(middleTableResultMap)){
                return;
            }
            // 收集查询结果values集合
            Collection refObjValues = middleTableResultMap.values().stream().distinct().collect(Collectors.toList());
            this.simplifySelectColumns();
            // 构建查询条件
            String refObjJoinOnCol = refObjJoinCols.get(0);
            // 获取匹配结果的mapList
            List<Map<String, Object>> mapList = null;
            if(V.isEmpty(this.module)){
                // 本地查询获取匹配结果的mapList
                queryWrapper.in(refObjJoinOnCol, refObjValues);
                mapList = getMapList(queryWrapper);
            }
            else{
                // 远程调用获取
                remoteBindDTO.setRefJoinCol(refObjJoinOnCol).setInConditionValues(refObjValues);
                mapList = RemoteBindingManager.fetchMapList(module, remoteBindDTO);
            }
            if(V.isEmpty(mapList)){
                return;
            }
            // 将结果list转换成map
            Map<String, Map<String, Object>> key2DataMap = this.buildMatchKey2ResultMap(mapList);
            // 遍历list并赋值
            for(Object annoObject : annoObjectList){
                String matchKey = buildMatchKey(annoObject, middleTableResultMap);
                setFieldValueToTrunkObj(key2DataMap, annoObject, matchKey);
            }
        }

    }

    /**
     * 设置字段值
     * @param key2DataMap
     * @param annoObject
     * @param matchKey
     */
    private void setFieldValueToTrunkObj(Map<String, Map<String, Object>> key2DataMap, Object annoObject, String matchKey) {
        Map<String, Object> relationMap = key2DataMap.get(matchKey);
        if (relationMap != null) {
            for (int i = 0; i < annoObjectSetterPropNameList.size(); i++) {
                Object valObj = getValueIgnoreKeyCase(relationMap, toRefObjColumn(referencedGetterFieldNameList.get(i)));
                BeanUtils.setProperty(annoObject, annoObjectSetterPropNameList.get(i), valObj);
            }
        }
    }

    /**
     * 构建匹配key-map目标的map
     * @param mapList
     * @return
     */
    protected Map<String, Map<String, Object>> buildMatchKey2ResultMap(List<Map<String, Object>> mapList){
        Map<String, Map<String, Object>> key2TargetMap = new HashMap<>(mapList.size());
        for(Map<String, Object> map : mapList){
            List<String> joinOnValues = new ArrayList<>(refObjJoinCols.size());
            for(String refObjJoinOnCol : refObjJoinCols){
                Object valObj = getValueIgnoreKeyCase(map, refObjJoinOnCol);
                joinOnValues.add(S.valueOf(valObj));
            }
            String matchKey = S.join(joinOnValues);
            if(matchKey != null){
                key2TargetMap.put(matchKey, map);
            }
        }
        return key2TargetMap;
    }

    /**
     * 构建匹配Key
     * @param annoObject
     * @return
     */
    private String buildMatchKey(Object annoObject){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<annoObjJoinCols.size(); i++){
            String col = annoObjJoinCols.get(i);
            // 将数子类型转换成字符串，以便解决类型不一致的问题
            String val = BeanUtils.getStringProperty(annoObject, toAnnoObjField(col));
            if(i > 0){
                sb.append(Cons.SEPARATOR_COMMA);
            }
            sb.append(val);
        }
        return sb.toString();
    }

    /**
     * 构建匹配Key
     * @param annoObject
     * @param middleTableResultMap
     * @return
     */
    private String buildMatchKey(Object annoObject, Map<String, Object> middleTableResultMap){
        StringBuilder sb = new StringBuilder();
        boolean appendComma = false;
        for(Map.Entry<String, String> entry : middleTable.getTrunkObjColMapping().entrySet()){
            String getterField = toAnnoObjField(entry.getKey());
            String fieldValue = BeanUtils.getStringProperty(annoObject, getterField);
            // 通过中间结果Map转换得到OrgId
            if(V.notEmpty(middleTableResultMap)){
                Object value = middleTableResultMap.get(fieldValue);
                fieldValue = String.valueOf(value);
            }
            if(appendComma){
                sb.append(Cons.SEPARATOR_COMMA);
            }
            sb.append(fieldValue);
            if(appendComma == false){
                appendComma = true;
            }
        }
        // 查找匹配Key
        return sb.toString();
    }

    @Override
    protected void simplifySelectColumns() {
        List<String> selectColumns = new ArrayList<>(8);
        selectColumns.addAll(refObjJoinCols);
        if(V.notEmpty(referencedGetterFieldNameList)){
            for(String referencedGetterField : referencedGetterFieldNameList){
                String refObjCol = toRefObjColumn(referencedGetterField);
                if(!selectColumns.contains(refObjCol)){
                    selectColumns.add(refObjCol);
                }
            }
        }
        // 添加orderBy排序
        if(V.notEmpty(this.orderBy)){
            // 解析排序
            String[] orderByFields = S.split(this.orderBy);
            for(String field : orderByFields){
                String colName = field.toLowerCase();
                if(colName.contains(":")){
                    colName = S.split(colName, ":")[0];
                }
                colName = toRefObjColumn(colName);
                if(!selectColumns.contains(colName)){
                    selectColumns.add(colName);
                }
            }
        }
        String[] selectColsArray = S.toStringArray(selectColumns);
        if(remoteBindDTO != null){
            remoteBindDTO.setSelectColumns(selectColsArray);
        }
        this.queryWrapper.select(selectColsArray);
    }
}
