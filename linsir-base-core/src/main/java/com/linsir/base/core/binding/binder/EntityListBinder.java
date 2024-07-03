package com.linsir.base.core.binding.binder;

import com.linsir.base.core.binding.annotation.BindEntityList;
import com.linsir.base.core.binding.binder.remote.RemoteBindingManager;
import com.linsir.base.core.binding.helper.ResultAssembler;
import com.linsir.base.core.constant.Cons;
import com.linsir.base.core.exception.InvalidUsageException;
import com.linsir.base.core.util.BeanUtils;
import com.linsir.base.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/24 10:41
 * @description：Entity集合绑定实现
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
public class EntityListBinder<T> extends EntityBinder<T> {


    /***
     * 构造方法
     * @param annotation
     * @param voList
     */
    public EntityListBinder(BindEntityList annotation, List voList){
        super(annotation.entity(), voList);
        if(V.notEmpty(annotation.splitBy())){
            this.splitBy = annotation.splitBy();
        }
        if(V.notEmpty(annotation.orderBy())){
            this.orderBy = annotation.orderBy();
        }
    }

    /***
     * 构造方法
     * @param entityClass
     * @param voList
     */
    public EntityListBinder(Class<T> entityClass, List voList){
        super(entityClass, voList);
    }

    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(V.isEmpty(refObjJoinCols)){
            throw new InvalidUsageException("调用错误：无法从condition中解析出字段关联.");
        }
        Map<String, List> valueEntityListMap = new HashMap<>();
        if(middleTable == null){
            //super.simplifySelectColumns();
            this.simplifySelectColumns();
            super.buildQueryWrapperJoinOn();
            // 查询条件为空时不进行查询
            if (queryWrapper.isEmptyOfNormal()) {
                return;
            }
            //处理orderBy，附加排序
            this.appendOrderBy(remoteBindDTO);
            List<T> entityList = null;
            // 查询entity列表: List<Role>
            if(V.isEmpty(this.module)){
                // 本地查询获取匹配结果的entityList
                entityList = getEntityList(queryWrapper);
            }
            else{
                // 远程调用获取
                entityList = RemoteBindingManager.fetchEntityList(module, remoteBindDTO, referencedEntityClass);
            }
            if(V.notEmpty(entityList)){
                valueEntityListMap = this.buildMatchKey2EntityListMap(entityList);
            }
            ResultAssembler.bindPropValue(annoObjectField, annoObjectList, getAnnoObjJoinFlds(), valueEntityListMap, this.splitBy);
        }
        else{
            if(refObjJoinCols.size() > 1){
                throw new InvalidUsageException(NOT_SUPPORT_MSG);
            }
            // 提取注解条件中指定的对应的列表
            Map<String, List> trunkObjCol2ValuesMap = super.buildTrunkObjCol2ValuesMap();
            Map<String, List> middleTableResultMap = middleTable.executeOneToManyQuery(trunkObjCol2ValuesMap);
            if(V.isEmpty(middleTableResultMap)){
                return;
            }
            //super.simplifySelectColumns();
            this.simplifySelectColumns();
            // 收集查询结果values集合
            List entityIdList = extractIdValueFromMap(middleTableResultMap);
            if(V.notEmpty(this.splitBy)){
                entityIdList = ResultAssembler.unpackValueList(entityIdList, this.splitBy);
            }
            //处理orderBy，附加排序
            this.appendOrderBy(remoteBindDTO);
            // 构建查询条件
            String refObjJoinOnCol = refObjJoinCols.get(0);
            List<T> entityList = null;
            // 查询entity列表: List<Role>
            if(V.isEmpty(this.module)){
                // 本地查询获取匹配结果的entityList
                queryWrapper.in(refObjJoinOnCol, entityIdList);
                entityList = getEntityList(queryWrapper);
            }
            else{
                // 远程调用获取
                remoteBindDTO.setRefJoinCol(refObjJoinOnCol).setInConditionValues(entityIdList);
                entityList = RemoteBindingManager.fetchEntityList(module, remoteBindDTO, referencedEntityClass);
            }
            if(V.isEmpty(entityList)){
                return;
            }
            String refObjJoinOnField = toRefObjField(refObjJoinOnCol);
            // 转换entity列表为Map<ID, Entity>
            Map<String, List<T>> entityMap = BeanUtils.convertToStringKeyObjectListMap(entityList, refObjJoinOnField);
            for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
                // List<roleId>
                List annoObjFKList = entry.getValue();
                if(V.isEmpty(annoObjFKList)){
                    continue;
                }
                List valueList = new ArrayList();
                for(Object obj : annoObjFKList){
                    if(obj == null){
                        continue;
                    }
                    String valStr = String.valueOf(obj);
                    List<T> ent = entityMap.get(valStr);
                    if(ent != null){
                        for (T item : ent) {
                            valueList.add(cloneOrConvertBean(item));
                        }
                    }
                    else if(V.notEmpty(splitBy) && valStr.contains(splitBy)){
                        for(String key : valStr.split(splitBy)){
                            ent = entityMap.get(key);
                            if(ent != null){
                                for (T item : ent) {
                                    valueList.add(cloneOrConvertBean(item));
                                }
                            }
                        }
                    }
                }
                valueEntityListMap.put(entry.getKey(), valueList);
            }
            // 绑定结果
            ResultAssembler.bindEntityPropValue(annoObjectField, annoObjectList, middleTable.getTrunkObjColMapping(), valueEntityListMap, getAnnoObjColumnToFieldMap());
        }
    }

    /**
     * 构建匹配key-entity目标的map
     * @param list
     * @return
     */
    private Map<String, List> buildMatchKey2EntityListMap(List<T> list){
        Map<String, List> key2TargetListMap = new HashMap<>(list.size());
        StringBuilder sb = new StringBuilder();
        for(T entity : list){
            sb.setLength(0);
            for(int i=0; i<refObjJoinCols.size(); i++){
                String refObjJoinOnCol = refObjJoinCols.get(i);
                String pkValue = BeanUtils.getStringProperty(entity, toRefObjField(refObjJoinOnCol));
                if(i > 0){
                    sb.append(Cons.SEPARATOR_COMMA);
                }
                sb.append(pkValue);
            }
            // 查找匹配Key
            String matchKey = sb.toString();
            // 获取list
            List entityList = key2TargetListMap.get(matchKey);
            if(entityList == null){
                entityList = new ArrayList<>();
                key2TargetListMap.put(matchKey, entityList);
            }
            Object target = entity;
            if(target instanceof Map == false){
                target = cloneOrConvertBean(entity);
            }
            entityList.add(target);
        }
        sb.setLength(0);
        return key2TargetListMap;
    }
}
