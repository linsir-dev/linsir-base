package com.linsir.base.core.binding.binder.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.linsir.base.core.util.ContextHelper;
import com.linsir.base.core.util.JSON;
import com.linsir.base.core.vo.jsonResults.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 23:31
 * @description：远程绑定manager
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
public class RemoteBindingManager {


    /**
     * restTemplate 实例缓存
     */
    private static Map<String, RemoteBindingProvider> MODULE_PROVIDER_MAP;
    /**
     * feignClientBuilder 实例
     */
    private static FeignClientBuilder feignClientBuilder;

    /**
     * 从远程接口抓取 Map List
     * @param module
     * @param remoteBindDTO
     * @return
     */
    public static List<Map<String, Object>> fetchMapList(String module, RemoteBindDTO remoteBindDTO){
        remoteBindDTO.setResultType("Map");
        RemoteBindingProvider bindingProvider = getRemoteBindingProvider(module);
        JsonResult<String> jsonResult = bindingProvider.loadBindingData(remoteBindDTO);
        if(jsonResult.isOK()){
            log.debug("获取到绑定数据: {}", jsonResult.getData());
            List<Map<String, Object>> mapList = JSON.parseObject(jsonResult.getData(), new TypeReference<List<Map<String, Object>>>(){});
            return mapList;
        }
        else{
            log.warn("获取绑定数据失败: {}", jsonResult.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * 从远程接口抓取 Entity List
     * @param module
     * @param remoteBindDTO
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> List<T> fetchEntityList(String module, RemoteBindDTO remoteBindDTO, Class<T> entityClass) {
        remoteBindDTO.setResultType("Entity");
        RemoteBindingProvider bindingProvider = getRemoteBindingProvider(module);
        JsonResult<String> jsonResult = bindingProvider.loadBindingData(remoteBindDTO);
        if(jsonResult.isOK()){
            log.debug("获取到绑定数据: {}", jsonResult.getData());
            List<T> entityList = JSON.parseArray(jsonResult.getData(), entityClass);
            return entityList;
        }
        else{
            log.warn("获取绑定数据失败: {}", jsonResult.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * 获取实例
     * @return
     */
    private static RemoteBindingProvider getRemoteBindingProvider(String module){
        if(MODULE_PROVIDER_MAP == null){
            MODULE_PROVIDER_MAP = new ConcurrentHashMap<>();
        }
        return MODULE_PROVIDER_MAP.computeIfAbsent(module, key -> {
            if(feignClientBuilder == null){
                feignClientBuilder = new FeignClientBuilder(ContextHelper.getApplicationContext());
            }
            return feignClientBuilder.forType(RemoteBindingProvider.class, module).build();
        });
    }
}
