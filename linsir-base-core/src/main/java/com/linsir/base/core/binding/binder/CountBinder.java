package com.linsir.base.core.binding.binder;


import com.linsir.base.core.binding.annotation.BindCount;
import com.linsir.base.core.binding.binder.remote.RemoteBindingManager;
import com.linsir.base.core.binding.cache.BindingCacheManager;
import com.linsir.base.core.binding.helper.ResultAssembler;
import com.linsir.base.core.constant.Cons;
import com.linsir.base.core.exception.InvalidUsageException;
import com.linsir.base.core.util.BeanUtils;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：linsir
 * @date ：Created in 2022/9/3 15:08
 * @description：统计绑定
 * @modified By：
 * @version: 0.0.1
 */
public class CountBinder<T> extends EntityListBinder<T> {

    private static final Logger log = LoggerFactory.getLogger(CountBinder.class);

    /***
     * 构造方法
     * @param annotation
     * @param voList
     */
    public CountBinder(BindCount annotation, List voList){
        super(annotation.entity(), voList);
    }


    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(V.isEmpty(refObjJoinCols)){
            throw new InvalidUsageException("调用错误：无法从condition中解析出字段关联.");
        }
        Map<String, Integer> valueListCountMap;
        if(middleTable == null){
            this.simplifySelectColumns();
            super.buildQueryWrapperJoinOn();
            // 查询条件为空时不进行查询
            if (queryWrapper.isEmptyOfNormal()) {
                return;
            }
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
                valueListCountMap = this.buildMatchKey2ListCountMap(entityList);
                ResultAssembler.bindPropValue(annoObjectField, super.getMatchedAnnoObjectList(), getAnnoObjJoinFlds(), valueListCountMap, null);
            }
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
            valueListCountMap = new HashMap<>();
            for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
                // List<roleId>
                List annoObjFKList = entry.getValue();
                if(V.isEmpty(annoObjFKList)){
                    continue;
                }
                Integer count = entry.getValue() != null? entry.getValue().size() : 0;
                valueListCountMap.put(entry.getKey(), count);
            }
            // 绑定结果
            ResultAssembler.bindPropValue(annoObjectField, super.getMatchedAnnoObjectList(), getAnnoObjJoinFlds(), valueListCountMap, null);
        }
    }

    /**
     * 简化select列，仅select主键
     */
    @Override
    protected void simplifySelectColumns() {
        List<String> selectColumns = new ArrayList<>(8);
        String idCol = BindingCacheManager.getPropInfoByClass(referencedEntityClass).getIdColumn();
        selectColumns.add(idCol);
        selectColumns.addAll(refObjJoinCols);
        String[] selectColsArray = S.toStringArray(selectColumns);
        if(remoteBindDTO != null){
            remoteBindDTO.setSelectColumns(selectColsArray);
        }
        this.queryWrapper.select(selectColsArray);
    }


    /**
     * 构建匹配key-count目标的map
     * @param list
     * @return
     */
    private Map<String, Integer> buildMatchKey2ListCountMap(List<T> list){
        Map<String, Integer> key2TargetCountMap = new HashMap<>(list.size());
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
            Integer entityCount = key2TargetCountMap.get(matchKey);
            if(entityCount == null){
                entityCount = 0;
            }
            entityCount++;
            key2TargetCountMap.put(matchKey, entityCount);
        }
        sb.setLength(0);
        return key2TargetCountMap;
    }

}
