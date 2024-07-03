package com.linsir.base.core.binding.binder.remote;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.base.core.binding.helper.WrapperHelper;
import com.linsir.base.core.config.BaseConfig;
import com.linsir.base.core.service.BaseService;
import com.linsir.base.core.util.ContextHelper;
import com.linsir.base.core.util.JSON;
import com.linsir.base.core.util.V;
import com.linsir.base.core.vo.jsonResults.JsonResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/25 11:15
 * @description：远程绑定查询执行器
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
public class RemoteBindQueryExecutor {
    /**
     * 执行查询返回绑定数据
     * @param remoteBindDTO
     * @return
     * @throws Exception
     */
    public static JsonResult execute(RemoteBindDTO remoteBindDTO){
        Class entityClass = null;
        try{
            entityClass = Class.forName(remoteBindDTO.getEntityClassName());
        }
        catch (Exception e){
            log.error("无法找到Entity类: {}", remoteBindDTO.getEntityClassName(), e);
            return JsonResult.FAIL_INVALID_PARAM("模块下无Entity类: "+remoteBindDTO.getEntityClassName());
        }
        Collection<?> inConditionValues = remoteBindDTO.getInConditionValues();
        if(inConditionValues == null){
            return JsonResult.OK();
        }
        // 构建queryWrpper
        QueryWrapper<?> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntityClass(entityClass);
        queryWrapper.select(remoteBindDTO.getSelectColumns());
        // 构建查询条件
        String refJoinCol = remoteBindDTO.getRefJoinCol();
        if (inConditionValues.isEmpty()) {
            return JsonResult.OK(Collections.emptyList());
        } else {
            queryWrapper.in(refJoinCol, inConditionValues);
        }
        queryWrapper.and(V.notEmpty(remoteBindDTO.getAdditionalConditions()), e -> remoteBindDTO.getAdditionalConditions().forEach(e::apply));
        // 排序
        WrapperHelper.buildOrderBy(queryWrapper, remoteBindDTO.getOrderBy(), e -> e);
        // 执行查询返回结果List
        try{
            String jsonStr = null;
            if("Map".equals(remoteBindDTO.getResultType())){
                List<Map<String, Object>> resultMap = getMapList(entityClass, queryWrapper);
                jsonStr = JSON.stringify(resultMap);
            }
            else if("Entity".equals(remoteBindDTO.getResultType())){
                List<?> resultList = getEntityList(entityClass, queryWrapper);
                jsonStr = JSON.stringify(resultList);
            }
            return JsonResult.OK(jsonStr);
        }
        catch (Exception e){
            log.error("绑定查询执行异常", e);
            return JsonResult.FAIL_EXCEPTION("绑定查询执行异常: " + e.getMessage());
        }
    }

    /**
     * 获取Map结果
     * @param queryWrapper
     * @return
     */
    private static List<Map<String, Object>> getMapList(Class entityClass, Wrapper queryWrapper) {
        IService referencedService = ContextHelper.getIServiceByEntity(entityClass);
        if(referencedService instanceof BaseService){
            return ((BaseService)referencedService).getMapList(queryWrapper);
        }
        else{
            List<Map<String, Object>> list = referencedService.listMaps(queryWrapper);
            return checkedList(list);
        }
    }

    /**
     * 获取EntityList
     * @param queryWrapper
     * @return
     */
    private static <T> List<T> getEntityList(Class entityClass, Wrapper queryWrapper) {
        IService referencedService = ContextHelper.getIServiceByEntity(entityClass);
        if(referencedService instanceof BaseService){
            return ((BaseService)referencedService).getEntityList(queryWrapper);
        }
        else{
            List<T> list = referencedService.list(queryWrapper);
            return checkedList(list);
        }
    }

    /**
     * 检查list，结果过多打印warn
     * @param list
     * @return
     */
    private static List checkedList(List list){
        if(list == null){
            list = Collections.emptyList();
        }
        else if(list.size() > BaseConfig.getBatchSize()){
            log.warn("单次查询记录数量过大，返回结果数={}，请检查！", list.size());
        }
        return list;
    }
}
