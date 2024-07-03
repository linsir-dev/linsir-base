package com.linsir.base.core.binding.binder;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.base.core.binding.annotation.Module;
import com.linsir.base.core.binding.binder.remote.RemoteBindDTO;
import com.linsir.base.core.binding.cache.BindingCacheManager;
import com.linsir.base.core.binding.helper.ResultAssembler;
import com.linsir.base.core.binding.helper.WrapperHelper;
import com.linsir.base.core.binding.parser.FieldComparison;
import com.linsir.base.core.binding.parser.MiddleTable;
import com.linsir.base.core.binding.parser.PropInfo;
import com.linsir.base.core.binding.query.Comparison;
import com.linsir.base.core.config.BaseConfig;
import com.linsir.base.core.exception.InvalidUsageException;
import com.linsir.base.core.service.BaseService;
import com.linsir.base.core.util.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author: linsir
 * @date: 2022/3/21 9:51
 * @description: 关系绑定Binder父类
 */
@Slf4j
public abstract class BaseBinder<T> {

    /***
     * 需要绑定到的VO注解对象List
     */
    protected List<?> annoObjectList;
    /***
     * VO注解对象中的join on对象列名集合
     */
    protected List<String> annoObjJoinCols;

    /***
     * VO注解对象中的join on对象附加过滤条件
     */
    protected List<FieldComparison> annoObjJoinFieldComparisons;

    /***
     * DO对象中的关联join on对象列名集合
     */
    protected List<String> refObjJoinCols;


    /**
     * 被关联对象的Service实例
     */
    protected IService<T> referencedService;
    /**
     * 初始化QueryWrapper
     */
    protected QueryWrapper<T> queryWrapper;

    /**
     * 多对多关联的桥接表，如 user_role<br>
     * 多对多注解示例: id=user_role.user_id AND user_role.role_id=id
     */
    protected MiddleTable middleTable;

    protected Class<T> referencedEntityClass;
    /**
     * 注解宿主对象列名-字段名的映射map
     */
    private PropInfo annoObjPropInfo;
    /**
     * 被关联对象列名-字段名的映射map
     */
    private PropInfo refObjPropInfo;

    public static final String NOT_SUPPORT_MSG = "中间表关联暂不支持涉及目标表多列的情况!";

    /**
     * ,拼接的多个id值
     */
    protected String splitBy = null;
    /**
     * List 排序
     */
    protected String orderBy;
    /**
     * 模块/服务 名
     */
    protected String module;
    /**
     * 远程bind
     */
    protected RemoteBindDTO remoteBindDTO;

    /***
     * 构造方法
     * @param entityClass
     * @param voList
     */
    public BaseBinder(Class<T> entityClass, List voList){
        this.referencedEntityClass = entityClass;
        this.annoObjectList = voList;
        if(V.notEmpty(voList)){
            this.annoObjPropInfo = BindingCacheManager.getPropInfoByClass(voList.get(0).getClass());
        }
        this.queryWrapper = new QueryWrapper<>();
        this.refObjPropInfo = BindingCacheManager.getPropInfoByClass(this.referencedEntityClass);
        Module moduleAnno = AnnotationUtils.findAnnotation(this.referencedEntityClass, Module.class);
        this.referencedService = getService(entityClass, moduleAnno);
        if(moduleAnno != null){
            String applicationName = PropertiesUtils.get("spring.application.name");
            if(V.notEquals(moduleAnno.value(), applicationName)){
                this.module = moduleAnno.value();
                remoteBindDTO = new RemoteBindDTO(referencedEntityClass);
            }
        }
        // 列集合
        this.annoObjJoinCols = new ArrayList<>(4);
        this.refObjJoinCols = new ArrayList<>(4);
    }

    /**
     * join连接条件，指定当前VO的取值方法和关联entity的取值方法
     * @param annoObjectFkGetter 当前VO的取值方法
     * @param referencedEntityPkGetter 关联entity的取值方法
     * @param <T1> 当前VO的对象类型
     * @param <T2> 关联对象entity类型
     * @return
     */
    @Deprecated
    public <T1,T2> BaseBinder<T> joinOn(IGetter<T1> annoObjectFkGetter, IGetter<T2> referencedEntityPkGetter){
        String annoObjectForeignKey = toAnnoObjColumn(BeanUtils.convertToFieldName(annoObjectFkGetter));
        String referencedEntityPrimaryKey = toRefObjColumn(BeanUtils.convertToFieldName(referencedEntityPkGetter));
        return joinOn(annoObjectForeignKey, referencedEntityPrimaryKey);
    }

    /**
     * 附加条件
     *
     * @param condition
     * @return
     */
    public BaseBinder<T> additionalCondition(String condition) {
        if (remoteBindDTO == null) {
            return this;
        }
        List<String> bindDTOWhere = remoteBindDTO.getAdditionalConditions();
        if (bindDTOWhere == null) {
            bindDTOWhere = new ArrayList<>();
            remoteBindDTO.setAdditionalConditions(bindDTOWhere);
        }
        bindDTOWhere.add(condition);
        return this;
    }

    /**
     * join连接条件，指定当前VO的取值方法和关联entity的取值方法
     * @param annoObjectForeignKey 当前VO的取值属性名
     * @param referencedEntityPrimaryKey 关联entity的属性
     * @return
     */
    public BaseBinder<T> joinOn(String annoObjectForeignKey, String referencedEntityPrimaryKey){
        if(annoObjectForeignKey != null && referencedEntityPrimaryKey != null){
            annoObjJoinCols.add(annoObjectForeignKey);
            refObjJoinCols.add(referencedEntityPrimaryKey);
        }
        return this;
    }

    /**
     * join连接条件的附加条件，指定当前VO的取值方法和关联entity的取值方法
     * @param annoObjectFieldKey 当前VO的取值属性名
     * @param eqFilterConsVal 常量条件值
     * @return
     */
    public BaseBinder<T> joinOnFieldComparison(String annoObjectFieldKey, Comparison comparison, Object eqFilterConsVal){
        if(annoObjectFieldKey != null && eqFilterConsVal != null){
            if(annoObjJoinFieldComparisons == null){
                annoObjJoinFieldComparisons = new ArrayList<>(4);
            }
            String fieldName = this.annoObjPropInfo.getFieldByColumn(annoObjectFieldKey);
            if(fieldName == null && this.annoObjPropInfo.getFieldToColumnMap().containsKey(annoObjectFieldKey)) {
                fieldName = annoObjectFieldKey;
            }
            if(fieldName == null) {
                throw new InvalidUsageException("字段/列 "+ annoObjectFieldKey +" 不存在");
            }
            annoObjJoinFieldComparisons.add(new FieldComparison(fieldName, comparison, eqFilterConsVal));
        }
        return this;
    }

    public BaseBinder<T> andEQ(String fieldName, Object value){
        queryWrapper.eq(toRefObjColumn(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andNE(String fieldName, Object value){
        queryWrapper.ne(toRefObjColumn(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andGT(String fieldName, Object value){
        queryWrapper.gt(toRefObjColumn(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andGE(String fieldName, Object value){
        queryWrapper.ge(toRefObjColumn(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andLT(String fieldName, Object value){
        queryWrapper.lt(toRefObjColumn(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andLE(String fieldName, Object value){
        queryWrapper.le(toRefObjColumn(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andIsNotNull(String fieldName){
        queryWrapper.isNotNull(toRefObjColumn(fieldName));
        return this;
    }
    public BaseBinder<T> andIsNull(String fieldName){
        queryWrapper.isNull(toRefObjColumn(fieldName));
        return this;
    }
    public BaseBinder<T> andBetween(String fieldName, Object begin, Object end){
        queryWrapper.between(toRefObjColumn(fieldName), formatValue(fieldName, begin), formatValue(fieldName, end));
        return this;
    }
    public BaseBinder<T> andLike(String fieldName, String value){
        value = (String)formatValue(fieldName, value);
        fieldName = toRefObjColumn(fieldName);
        if(S.startsWith(value, "%")){
            value = S.substringAfter(value, "%");
            if(S.endsWith(value, "%")){
                value = S.substringBeforeLast(value, "%");
                queryWrapper.like(fieldName, value);
            }
            else{
                queryWrapper.likeLeft(fieldName, value);
            }
        }
        else if(S.endsWith(value, "%")){
            value = S.substringBeforeLast(value, "%");
            queryWrapper.likeRight(fieldName, value);
        }
        else{
            queryWrapper.like(fieldName, value);
        }
        return this;
    }
    public BaseBinder<T> andIn(String fieldName, Collection valueList){
        queryWrapper.in(toRefObjColumn(fieldName), valueList);
        return this;
    }
    public BaseBinder<T> andNotIn(String fieldName, Collection valueList){
        queryWrapper.notIn(toRefObjColumn(fieldName), valueList);
        return this;
    }
    public BaseBinder<T> andNotBetween(String fieldName, Object begin, Object end){
        queryWrapper.notBetween(toRefObjColumn(fieldName), formatValue(fieldName, begin), formatValue(fieldName, end));
        return this;
    }
    public BaseBinder<T> andNotLike(String fieldName, String value){
        queryWrapper.notLike(toRefObjColumn(fieldName), formatValue(fieldName, value));
        return this;
    }
    public BaseBinder<T> andApply(String applySql){
        queryWrapper.apply(applySql);
        return this;
    }
    public BaseBinder<T> withMiddleTable(MiddleTable middleTable){
        this.middleTable = middleTable;
        return this;
    }

    /**
     * 返回MiddleTable
     * @return
     */
    public MiddleTable getMiddleTable(){
        return middleTable;
    }

    public List<String> getAnnoObjJoinCols(){
        return this.annoObjJoinCols;
    }
    public List<String> getRefObjJoinCols(){
        return this.refObjJoinCols;
    }

    /***
     * 执行绑定, 交由子类实现
     */
    public abstract void bind();

    /**
     * 构建join on
     */
    protected void buildQueryWrapperJoinOn() {
        for (int i = 0; i < annoObjJoinCols.size(); i++) {
            String annoObjJoinOnCol = annoObjJoinCols.get(i);
            List<?> annoObjectJoinOnList = BeanUtils.collectToList(annoObjectList, toAnnoObjField(annoObjJoinOnCol));
            // 构建查询条件
            String refObjJoinOnCol = refObjJoinCols.get(i);
            if (V.notEmpty(annoObjectJoinOnList)) {
                List<?> unpackAnnoObjectJoinOnList = V.notEmpty(this.splitBy) ? ResultAssembler.unpackValueList(annoObjectJoinOnList, this.splitBy)
                        : annoObjectJoinOnList;
                queryWrapper.in(refObjJoinOnCol, unpackAnnoObjectJoinOnList);
                if (remoteBindDTO != null) {
                    remoteBindDTO.setRefJoinCol(refObjJoinOnCol).setInConditionValues(unpackAnnoObjectJoinOnList);
                }
            }
        }
    }

    /**
     * 获取匹配的
     * @return
     */
    protected List<?> getMatchedAnnoObjectList() {
        return filterObjectList(annoObjectList, annoObjJoinFieldComparisons);
    }

    /**
     * 简化select列，仅select必需列
     */
    protected abstract void simplifySelectColumns();

    /**
     * 获取EntityList
     * @param queryWrapper
     * @return
     */
    protected List<T> getEntityList(Wrapper queryWrapper) {
        if(referencedService instanceof BaseService){
            return ((BaseService)referencedService).getEntityList(queryWrapper);
        }
        else{
            List<T> list = referencedService.list(queryWrapper);
            return checkedList(list);
        }
    }

    /**
     * 获取Map结果
     * @param queryWrapper
     * @return
     */
    protected List<Map<String, Object>> getMapList(Wrapper queryWrapper) {
        if(referencedService instanceof BaseService){
            return ((BaseService)referencedService).getMapList(queryWrapper);
        }
        else{
            List<Map<String, Object>> list = referencedService.listMaps(queryWrapper);
            return checkedList(list);
        }
    }

    /**
     * 构建中间表查询参数map
     * @return
     */
    protected Map<String, List> buildTrunkObjCol2ValuesMap(){
        // 提取注解条件中指定的对应的列表
        Map<String, List> trunkObjCol2ValuesMap = new HashMap<>();
        for(Map.Entry<String, String> entry : middleTable.getTrunkObjColMapping().entrySet()){
            String annoObjFld = toAnnoObjField(entry.getKey());
            List valueList = BeanUtils.collectToList(annoObjectList, annoObjFld);
            trunkObjCol2ValuesMap.put(entry.getValue(), valueList);
        }
        return trunkObjCol2ValuesMap;
    }

    /**
     * 从map中取值，如直接取为null尝试转换大写后再取，以支持ORACLE等大写命名数据库
     * @param map
     * @param key
     * @return
     */
    protected Object getValueIgnoreKeyCase(Map<String, Object> map, String key){
        return ResultAssembler.getValueIgnoreKeyCase(map, key);
    }

    /**
     * 从Map中提取ID的值
     * @param middleTableResultMap
     * @return
     */
    protected List extractIdValueFromMap(Map<String, List> middleTableResultMap) {
        List entityIdList = new ArrayList();
        for(Map.Entry<String, List> entry : middleTableResultMap.entrySet()){
            if(V.isEmpty(entry.getValue())){
                continue;
            }
            for(Object id : entry.getValue()){
                if(!entityIdList.contains(id)){
                    entityIdList.add(id);
                }
            }
        }
        return entityIdList;
    }

    /**
     * 注解宿主对象的列名转换为字段名
     * @return
     */
    public String[] getAnnoObjJoinFlds(){
        String[] fields = new String[annoObjJoinCols.size()];
        for(int i=0; i<annoObjJoinCols.size(); i++){
            fields[i] = toAnnoObjField(annoObjJoinCols.get(i));
        }
        return fields;
    }

    /**
     * 获取宿主对象的列名-字段名映射map
     * @return
     */
    public Map<String, String> getAnnoObjColumnToFieldMap(){
        return this.annoObjPropInfo.getColumnToFieldMap();
    }

    /**
     * 注解宿主对象的列名转换为字段名
     * @return
     */
    public String toAnnoObjField(String annoObjColumn){
        String field = this.annoObjPropInfo.getColumnToFieldMap().get(annoObjColumn);
        if(field == null){
            field = S.toLowerCaseCamel(annoObjColumn);
        }
        return field;
    }

    /**
     * 注解对宿主对象的字段名转换为列名
     * @return
     */
    public String toAnnoObjColumn(String annoObjField){
        String column = this.annoObjPropInfo.getFieldToColumnMap().get(annoObjField);
        if(column == null){
            column = S.toSnakeCase(annoObjField);
        }
        return column;
    }

    /**
     * 被关联对象的列名转换为字段名
     * @return
     */
    public String toRefObjField(String refObjColumn){
        String field = this.refObjPropInfo.getColumnToFieldMap().get(refObjColumn);
        if(field == null){
            field = S.toLowerCaseCamel(refObjColumn);
        }
        return field;
    }

    /**
     * 被关联对象的字段名转换为列名
     * @return
     */
    public String toRefObjColumn(String refObjField){
        String column = this.refObjPropInfo.getFieldToColumnMap().get(refObjField);
        if(column == null){
            column = S.toSnakeCase(refObjField);
        }
        return column;
    }

    /**
     * 附加排序字段，支持格式：orderBy=short_name:DESC,age:ASC,birthdate
     * @param remoteBindDTO
     */
    protected void appendOrderBy(RemoteBindDTO remoteBindDTO){
        if(V.isEmpty(this.orderBy)){
            return;
        }
        if(remoteBindDTO != null){
            remoteBindDTO.setOrderBy(this.orderBy);
            return;
        }
        // 构建排序
        WrapperHelper.buildOrderBy(queryWrapper, this.orderBy, this::toRefObjColumn);
    }

    /**
     * 通过Entity获取对应的Service实现类
     * @param entityClass
     * @return
     */
    private IService<T> getService(Class<T> entityClass, Module moduleAnno){
        // 根据entity获取Service
        IService iService = ContextHelper.getIServiceByEntity(entityClass);
        if(iService == null){
            // 本地绑定需确保有Service实现类
            if(moduleAnno == null){
                throw new InvalidUsageException(entityClass.getSimpleName() + " 无 BaseService/IService实现类，无法执行注解绑定！");
            }
        }
        return iService;
    }

    /**
     * 检查list，结果过多打印warn
     * @param list
     * @return
     */
    private List checkedList(List list){
        if(list == null){
            list = Collections.emptyList();
        }
        else if(list.size() > BaseConfig.getBatchSize()){
            log.warn("单次查询记录数量过大，返回结果数={}，请检查！", list.size());
        }
        return list;
    }

    /**
     * 格式化条件值
     * @param fieldName 属性名
     * @param value 值
     * @return
     */
    private Object formatValue(String fieldName, Object value){
        if(value instanceof String && S.contains((String)value, "'")){
            return S.replace((String)value, "'", "");
        }
        // 转型
        if(this.referencedEntityClass != null){
            Field field = BeanUtils.extractField(this.referencedEntityClass, toRefObjField(fieldName));
            if(field != null){
                return BeanUtils.convertValueToFieldType(value, field);
            }
        }
        return value;
    }

    /***
     * 筛选list
     * @param objectList
     * @param filterConditions 附加过滤条件
     * @param <E>
     * @return
     */
    private <E> List filterObjectList(List<E> objectList, List<FieldComparison> filterConditions) {
        if(V.isEmpty(objectList)){
            return Collections.emptyList();
        }
        if(V.isEmpty(filterConditions)) {
            return objectList;
        }
        List<E> newObjectList = new ArrayList<>();
        for(E object : objectList){
            boolean matched = true;
            for(FieldComparison fieldCompare : filterConditions) {
                Object fieldValue = BeanUtils.getProperty(object, fieldCompare.getFieldName());
                if(Comparison.EQ.name().equals(fieldCompare.getComparison().name())){
                    if(!V.fuzzyEqual(fieldValue, fieldCompare.getValue())) {
                        matched = false;
                        break;
                    }
                }
                else if(Comparison.NOT_EQ.name().equals(fieldCompare.getComparison().name())) {
                    if(V.fuzzyEqual(fieldValue, fieldCompare.getValue())) {
                        matched = false;
                        break;
                    }
                }
                else if(Comparison.CONTAINS.name().equals(fieldCompare.getComparison().name())){
                    if(!S.valueOf(fieldValue).contains((String)fieldCompare.getValue())) {
                        matched = false;
                        break;
                    }
                }
                else if(Comparison.IN.name().equals(fieldCompare.getComparison().name())) {
                    List inValues = (List)fieldCompare.getValue();
                    boolean in = false;
                    for(Object value : inValues) {
                        if(V.fuzzyEqual(fieldValue, value)) {
                            in = true;
                            break;
                        }
                    }
                    if(!in) {
                        matched = false;
                        break;
                    }
                }
                else if(Comparison.NOT_IN.name().equals(fieldCompare.getComparison().name())) {
                    List notInValues = (List)fieldCompare.getValue();
                    boolean notIn = true;
                    for(Object value : notInValues) {
                        if(V.fuzzyEqual(fieldValue, value)) {
                            notIn = false;
                            break;
                        }
                    }
                    if(!notIn) {
                        matched = false;
                        break;
                    }
                }
                else{
                    log.warn("暂不支持此类型对比: {}", fieldCompare.getComparison().name());
                }
            }
            if(!matched) {
                log.debug("{} 不匹配 {}， 忽略收集该字段", object, filterConditions);
                continue;
            }
            newObjectList.add(object);
        }
        return newObjectList;
    }
}
