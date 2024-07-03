package com.linsir.base.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.linsir.base.core.binding.Binder;
import com.linsir.base.core.binding.QueryBuilder;
import com.linsir.base.core.binding.cache.BindingCacheManager;
import com.linsir.base.core.binding.helper.WrapperHelper;
import com.linsir.base.core.binding.parser.PropInfo;
import com.linsir.base.core.config.BaseConfig;
import com.linsir.base.core.constant.Cons;
import com.linsir.base.core.dto.AttachMoreDTO;
import com.linsir.base.core.entity.ValidList;
import com.linsir.base.core.service.BaseService;
import com.linsir.base.core.service.DictionaryService;
import com.linsir.base.core.util.ContextHelper;
import com.linsir.base.core.util.JSON;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import com.linsir.base.core.vo.R;
import com.linsir.base.core.vo.LabelValue;
import com.linsir.base.core.vo.jsonResults.JsonResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 12:28
 * @description：Controller的父类
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
public class BaseController {

    @Autowired
    protected HttpServletRequest request;


    /**
     * 字典service
     */
    protected DictionaryService dictionaryService;

    protected R exec(String logName, ControllerCallable callable) throws Exception {
        log.info(">>>log:"+logName);
        R result =null;
        try {
            result = callable.execute();
        }
        catch (Exception e)
        {
            throw e;
        }
        return result;
    }

    protected JsonResult exec(JSONControllerCallable callable)
    {
        log.info(">>>URL:"+getRequestMappingURI());
        log.info(">>>");
        dumpParams();
        JsonResult jsonResult = null;
        try {
             jsonResult = callable.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }


    /***
     * 构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件)
     * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
     * @see #buildQueryWrapperByDTO #buildQueryWrapperByQueryParams
     * @return
     */
    @Deprecated
    protected <DTO> QueryWrapper<DTO> buildQueryWrapper(DTO entityOrDto) throws Exception{
        return buildQueryWrapperByQueryParams(entityOrDto);
    }

    /***
     * 根据DTO构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件，DTO中的非空属性均参与构建)
     * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
     * @return
     */
    protected <DTO> QueryWrapper<DTO> buildQueryWrapperByDTO(DTO entityOrDto) throws Exception{
        return QueryBuilder.toQueryWrapper(entityOrDto);
    }

    /***
     * 根据请求参数构建查询QueryWrapper (根据BindQuery注解构建相应的查询条件，url中的请求参数参与构建)
     * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
     * @return
     */
    protected <DTO> QueryWrapper<DTO> buildQueryWrapperByQueryParams(DTO entityOrDto) throws Exception{
        return QueryBuilder.toQueryWrapper(entityOrDto, extractQueryParams());
    }

    /***
     * 构建查询LambdaQueryWrapper (根据BindQuery注解构建相应的查询条件)
     * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
     * @see #buildLambdaQueryWrapperByDTO #buildLambdaQueryWrapperByQueryParams
     * @return
     */
    @Deprecated
    protected <DTO> LambdaQueryWrapper<DTO> buildLambdaQueryWrapper(DTO entityOrDto) throws Exception{
        return buildLambdaQueryWrapperByQueryParams(entityOrDto);
    }

    /***
     * 根据DTO构建查询LambdaQueryWrapper (根据BindQuery注解构建相应的查询条件，DTO中的非空属性均参与构建)
     * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
     * @return
     */
    protected <DTO> LambdaQueryWrapper<DTO> buildLambdaQueryWrapperByDTO(DTO entityOrDto) throws Exception{
        return QueryBuilder.toLambdaQueryWrapper(entityOrDto);
    }

    /***
     * 根据请求参数构建查询LambdaQueryWrapper (根据BindQuery注解构建相应的查询条件，url中的请求参数参与构建)
     * @param entityOrDto Entity对象或者DTO对象 (属性若无BindQuery注解，默认构建为为EQ相等条件)
     * @return
     */
    protected <DTO> LambdaQueryWrapper<DTO> buildLambdaQueryWrapperByQueryParams(DTO entityOrDto) throws Exception{
        return QueryBuilder.toLambdaQueryWrapper(entityOrDto, extractQueryParams());
    }

    /***
     * 获取请求参数Map
     * @return
     */
    protected Map<String, Object> getParamsMap() throws Exception{
        return getParamsMap(null);
    }

    /***
     * 获取请求参数Map
     * @return
     */
    private Map<String, Object> getParamsMap(List<String> paramList) throws Exception{
        Map<String, Object> result = new HashMap<>(8);
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()){
            String paramName = (String) paramNames.nextElement();
            // 如果非要找的参数，则跳过
            if(V.notEmpty(paramList) && !paramList.contains(paramName)){
                continue;
            }
            String[] values = request.getParameterValues(paramName);
            if(V.notEmpty(values)){
                if(values.length == 1){
                    if(V.notEmpty(values[0])){
                        String paramValue = java.net.URLDecoder.decode(values[0], Cons.CHARSET_UTF8);
                        result.put(paramName, paramValue);
                    }
                }
                else{
                    String[] valueArray = new String[values.length];
                    for(int i=0; i<values.length; i++){
                        valueArray[i] = java.net.URLDecoder.decode(values[i], Cons.CHARSET_UTF8);
                    }
                    // 多个值需传递到后台SQL的in语句
                    result.put(paramName, valueArray);
                }
            }
        }

        return result;
    }

    /***
     * 获取请求URI (去除contextPath)
     * @return
     */
    protected String getRequestMappingURI(){
        String contextPath = request.getContextPath();
        if(V.notEmpty(contextPath)){
            return S.replace(request.getRequestURI(), contextPath, "");
        }
        return request.getRequestURI();
    }

    /**
     * 提取请求参数名集合
     * @return
     */
    protected Set<String> extractQueryParams(){
        Map<String, Object> paramValueMap = convertParams2Map();
        if(V.notEmpty(paramValueMap)){
            return paramValueMap.keySet();
        }
        return Collections.EMPTY_SET;
    }

    /***
     * 将请求参数值转换为Map
     * @return
     */
    protected Map<String, Object> convertParams2Map(){
        Map<String, Object> result = new HashMap<>(8);
        if(request == null){
            return result;
        }
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()){
            String paramName = (String) paramNames.nextElement();
            String[] values = request.getParameterValues(paramName);
            if(V.notEmpty(values)){
                if(values.length == 1){
                    if(V.notEmpty(values[0])){
                        result.put(paramName, values[0]);
                    }
                }
                else{
                    // 多个值需传递到后台SQL的in语句
                    result.put(paramName, values);
                }
            }
        }
        return result;
    }

    /**
     * 自动转换为VO并绑定关联关系
     *
     * @param entityList
     * @param voClass
     * @param <VO>
     * @return
     */
    @Deprecated
    protected <VO> List<VO> convertToVoAndBindRelations(List entityList, Class<VO> voClass) {
        // 转换为VO
        List<VO> voList = Binder.convertAndBindRelations(entityList, voClass);
        return voList;
    }

    /**
     * 通用的attachMore获取数据
     *
     * @param attachMoreDTOList
     * @return
     */
    protected Map<String, List<LabelValue>> attachMoreRelatedData(ValidList<AttachMoreDTO> attachMoreDTOList) {
        if (V.isEmpty(attachMoreDTOList)) {
            return Collections.emptyMap();
        }
        Map<String, List<LabelValue>> result = new HashMap<>(attachMoreDTOList.size());
        for (AttachMoreDTO attachMoreDTO : attachMoreDTOList) {
            // 请求参数安全检查
            V.securityCheck(attachMoreDTO.getTarget(), attachMoreDTO.getValue(), attachMoreDTO.getLabel(), attachMoreDTO.getExt());
            result.computeIfAbsent(S.getIfEmpty(attachMoreDTO.getAlias(), () -> S.toLowerCaseCamel(attachMoreDTO.getTarget()) + "Options"),
                    key -> V.isEmpty(attachMoreDTO.getLabel()) ? attachMoreRelatedData(attachMoreDTO.getTarget()) : attachMoreRelatedData(attachMoreDTO));
        }
        return result;
    }

    /**
     * 获取字典指定类型的元素集合
     *
     * @param dictType 字典类型
     * @return labelValue集合
     */
    protected List<LabelValue> attachMoreRelatedData(String dictType) {
        return dictionaryService.getLabelValueList(dictType);
    }

    /**
     * 通用的attachMore获取相应对象数据
     *
     * @param attachMoreDTO
     * @return labelValue集合
     */
    protected List<LabelValue> attachMoreRelatedData(AttachMoreDTO attachMoreDTO) {
        if (!attachMoreDTO.isTree()) {
            attachMoreDTO.setParent(null);
        }
        return attachMoreRelatedData(attachMoreDTO, null, null);
    }

    /**
     * 获取attachMore指定层级的数据集合
     * <p>
     * 用于异步加树形数据
     *
     * @param attachMoreDTO
     * @param parentValue   父级值
     * @param parentType    父级类型
     * @return labelValue集合
     */
    protected List<LabelValue> attachMoreRelatedData(AttachMoreDTO attachMoreDTO, String parentValue, String parentType)  {
        if (V.notEmpty(parentType)) {
            attachMoreDTO = findAttachMoreByType(attachMoreDTO, parentType);
            if (attachMoreDTO != null && !attachMoreDTO.isTree()) {
                attachMoreDTO = attachMoreDTO.getNext().setTree(false);
            }
        }
        if (attachMoreDTO == null) {
            return Collections.emptyList();
        }
        List<LabelValue> labelValueList = attachMoreRelatedData(attachMoreDTO, parentValue);
        if (attachMoreDTO.isTree() && attachMoreDTO.getNext() != null) {
            labelValueList.addAll(attachMoreRelatedData(attachMoreDTO.getNext().setTree(false), parentValue));
        }
        return labelValueList;
    }

    /**
     * 根据type查找AttachMore
     *
     * @param attachMore
     * @param type       类型，与attachMore.target对应
     * @return labelValue集合
     */
    private AttachMoreDTO findAttachMoreByType(AttachMoreDTO attachMore, String type) {
        return attachMore.getTarget().equals(type) ? attachMore
                : (attachMore.getNext() == null ? null : findAttachMoreByType(attachMore.getNext(), type));
    }

    /**
     * 通用的attachMore获取数据
     *
     * @param attachMore  相应的attachMore
     * @param parentValue 父级值
     * @return labelValue集合
     */
    protected List<LabelValue> attachMoreRelatedData(AttachMoreDTO attachMore, String parentValue)  {
        if (!attachMoreSecurityCheck(attachMore)) {
            log.warn("attachMore安全检查不通过: {}", JSON.stringify(attachMore));
            return null;
        }
        if (V.notEmpty(parentValue) && !attachMore.isTree() && V.isEmpty(attachMore.getParent())) {
            //TODO 需要做业务异常
            //throw new /*Business*/Exception("attachMore跨表级联中的 " + attachMore.getTarget() + " 未指定关联父级属性 parent: ?");
            log.error("attachMore跨表级联中的 " + attachMore.getTarget() + " 未指定关联父级属性 parent: ?");
        }
        String entityClassName = attachMore.getTargetClassName();
        Class<?> entityClass = BindingCacheManager.getEntityClassBySimpleName(entityClassName);
        if (V.isEmpty(entityClass)) {
            //TODO 需要做业务异常
            //throw new /*Business*/Exception("attachMore: " + attachMore.getTarget() + " 不存在");
            log.error("attachMore: " + attachMore.getTarget() + " 不存在");
        }
        BaseService<?> baseService = ContextHelper.getBaseServiceByEntity(entityClass);
        if (baseService == null) {
            //TODO 需要做业务异常
            //throw new /*Business*/Exception("attachMore: " + attachMore.getTarget() + " 的Service不存在 ");
            log.error("attachMore: " + attachMore.getTarget() + " 的Service不存在 ");
        }
        PropInfo propInfoCache = BindingCacheManager.getPropInfoByClass(entityClass);
        Function<String, String> field2column = field -> {
            if (V.notEmpty(field)) {
                String column = propInfoCache.getColumnByField(field);
                if (V.notEmpty(column)) {
                    return column;
                } else {
                    //TODO 需要做业务异常
                    log.error("attachMore: " + attachMore.getTarget() + " 无 `" + field + "` 属性");
                    /*throw new BusinessException("attachMore: " + attachMore.getTarget() + " 无 `" + field + "` 属性");*/
                }
            }
            return null;
        };
        String label = field2column.apply(S.defaultIfBlank(attachMore.getLabel(), "label"));
        String value = S.defaultString(field2column.apply(attachMore.getValue()), propInfoCache.getIdColumn());
        String ext = field2column.apply(attachMore.getExt());

        // 构建查询条件
        QueryWrapper<?> queryWrapper = Wrappers.query()
                .select(V.isEmpty(ext) ? new String[]{label, value} : new String[]{label, value, ext})
                .like(V.notEmpty(attachMore.getKeyword()), label, attachMore.getKeyword());
        // 构建排序
        WrapperHelper.buildOrderBy(queryWrapper, attachMore.getOrderBy(), field2column);
        // 父级限制
        String parentColumn = field2column.apply(S.defaultIfBlank(attachMore.getParent(), attachMore.isTree() ? "parentId" : null));
        if (V.notEmpty(parentColumn)) {
            if (V.notEmpty(parentValue)) {
                queryWrapper.eq(parentColumn, parentValue);
            } else {
                queryWrapper.and(e -> e.isNull(parentColumn).or().eq(parentColumn, 0));
            }
        }
        // 构建附加条件
        buildAttachMoreCondition(attachMore, queryWrapper, field2column);
        // 获取数据并做相应填充
        List<LabelValue> labelValueList = baseService.getLabelValueList(queryWrapper);
        if (V.isEmpty(attachMore.getKeyword()) && labelValueList.size() > BaseConfig.getBatchSize()) {
            log.warn("attachMore: {} 数据量超过 {} 条, 建议采用远程搜索接口过滤数据", entityClassName, BaseConfig.getBatchSize());
        }
        if (V.notEmpty(parentColumn) || attachMore.getNext() != null) {
            Boolean leaf = !attachMore.isTree() && attachMore.getNext() == null ? true : null;
            // 第一层tree与最后一层无需返回type
            String type = attachMore.isTree() || Boolean.TRUE.equals(leaf) ? null : attachMore.getTarget();
            labelValueList.forEach(item -> {
                item.setType(type);
                item.setLeaf(leaf);
                // 非异步加载
                if (!Boolean.TRUE.equals(leaf) && !attachMore.isLazy()) {
                    List<LabelValue> children = attachMoreRelatedData(attachMore, S.valueOf(item.getValue()), type);
                    item.setChildren(children.isEmpty() ? null : children);
                    item.setDisabled(children.isEmpty() && attachMore.getNext() != null ? true : null);
                }
            });
        }
        return labelValueList;
    }

    /**
     * attachMore 安全检查
     *
     * @param attachMore
     * @return 是否允许访问该类型接口
     */
    protected boolean attachMoreSecurityCheck(AttachMoreDTO attachMore) {
        return true;
    }

    /**
     * 构建AttachMore的筛选条件
     *
     * @param attachMore
     * @param queryWrapper
     * @param field2column attachMore.target对象的属性名转列名
     */
    protected void buildAttachMoreCondition(AttachMoreDTO attachMore, QueryWrapper<?> queryWrapper, Function<String, String> field2column) {
        if (attachMore.getCondition() == null) {
            return;
        }
        for (Map.Entry<String, Object> item : attachMore.getCondition().entrySet()) {
            String columnName = field2column.apply(item.getKey());
            Object itemValue = item.getValue();
            if (itemValue == null) {
                queryWrapper.isNull(columnName);
            } else {
                if (itemValue instanceof Collection) {
                    queryWrapper.in(columnName, (Collection<?>) itemValue);
                } else {
                    queryWrapper.eq(columnName, itemValue);
                }
            }
        }
    }

    /***
     * 打印所有参数信息
     */
    protected void dumpParams(){
        Map<String, String[]> params = request.getParameterMap();
        if(params != null && !params.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String, String[]> entry : params.entrySet()){
                String[] values = entry.getValue();
                if(values != null && values.length > 0){
                    sb.append(entry.getKey() + "=" + S.join(values)+"; ");
                }
            }
            log.debug(sb.toString());
        }
    }

    /**
     * 从request获取Long参数
     * @param param
     * @return
     */
    protected Long getLong(String param){
        return S.toLong(request.getParameter(param));
    }

    /**
     * 从request获取Long参数
     * @param param
     * @param defaultValue
     * @return
     */
    protected long getLong(String param, Long defaultValue){
        return S.toLong(request.getParameter(param), defaultValue);
    }

    /**
     * 从request获取Int参数
     * @param param
     * @return
     */
    protected Integer getInteger(String param){
        return S.toInt(request.getParameter(param));
    }

    /**
     * 从request获取Int参数
     * @param param
     * @param defaultValue
     * @return
     */
    protected int getInt(String param, Integer defaultValue){
        return S.toInt(request.getParameter(param), defaultValue);
    }

    /***
     * 从request中获取boolean值
     * @param param
     * @return
     */
    protected boolean getBoolean(String param){
        return S.toBoolean(request.getParameter(param));
    }

    /***
     * 从request中获取boolean值
     * @param param
     * @param defaultBoolean
     * @return
     */
    protected boolean getBoolean(String param, boolean defaultBoolean){
        return S.toBoolean(request.getParameter(param), defaultBoolean);
    }

    /**
     * 从request获取Double参数
     * @param param
     * @return
     */
    protected Double getDouble(String param){
        if(V.notEmpty(request.getParameter(param))){
            return Double.parseDouble(request.getParameter(param));
        }
        return null;
    }

    /**
     * 从request获取Double参数
     * @param param
     * @param defaultValue
     * @return
     */
    protected Double getDouble(String param, Double defaultValue){
        if(V.notEmpty(request.getParameter(param))){
            return Double.parseDouble(request.getParameter(param));
        }
        return defaultValue;
    }

    /**
     * 从request获取String参数
     * @param param
     * @return
     */
    protected String getString(String param){
        if(V.notEmpty(request.getParameter(param))){
            return request.getParameter(param);
        }
        return null;
    }

    /**
     * 从request获取String参数
     * @param param
     * @param defaultValue
     * @return
     */
    protected String getString(String param, String defaultValue){
        if(V.notEmpty(request.getParameter(param))){
            return request.getParameter(param);
        }
        return defaultValue;
    }

    /**
     * 从request获取String[]参数
     * @param param
     * @return
     */
    protected String[] getStringArray(String param){
        if(request.getParameterValues(param) != null){
            return request.getParameterValues(param);
        }
        return null;
    }

    /***
     * 从request里获取String列表
     * @param param
     * @return
     */
    protected List<String> getStringList(String param){
        String[] strArray = getStringArray(param);
        if(V.isEmpty(strArray)){
            return null;
        }
        return Arrays.asList(strArray);
    }

    /***
     * 从request里获取Long列表
     * @param param
     * @return
     */
    protected List<Long> getLongList(String param){
        String[] strArray = getStringArray(param);
        if(V.isEmpty(strArray)){
            return null;
        }
        List<Long> longList = new ArrayList<>();
        for(String str : strArray){
            if(V.notEmpty(str)){
                longList.add(Long.parseLong(str));
            }
        }
        return longList;
    }
}
