package com.linsir.base.core.data.copy;

import com.linsir.base.core.util.BeanUtils;
import com.linsir.base.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/21 19:14
 * @description：Accept注解拷贝器
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
public class AcceptAnnoCopier {
    /**
     * 注解缓存
     */
    private static final Map<String, List<String[]>> CLASS_ACCEPT_ANNO_CACHE_MAP = new ConcurrentHashMap<>();
    // 下标
    private static final int IDX_TARGET_FIELD = 0, IDX_SOURCE_FIELD = 1, IDX_OVERRIDE = 2;

    /**
     * 基于注解拷贝属性
     * @param source
     * @param target
     */
    public static void copyAcceptProperties(Object source, Object target){
        String key = target.getClass().getName();
        // 初始化
        if(!CLASS_ACCEPT_ANNO_CACHE_MAP.containsKey(key)){
            List<Field> annoFieldList = BeanUtils.extractFields(target.getClass(), Accept.class);
            if(V.isEmpty(annoFieldList)){
                CLASS_ACCEPT_ANNO_CACHE_MAP.put(key, Collections.EMPTY_LIST);
            }
            else{
                List<String[]> annoDefList = new ArrayList<>(annoFieldList.size());
                for(Field fld : annoFieldList){
                    Accept accept = fld.getAnnotation(Accept.class);
                    String[] annoDef = {fld.getName(), accept.name(), accept.override()? "1":"0"};
                    annoDefList.add(annoDef);
                }
                CLASS_ACCEPT_ANNO_CACHE_MAP.put(key, annoDefList);
            }
        }
        // 解析copy
        List<String[]> acceptAnnos = CLASS_ACCEPT_ANNO_CACHE_MAP.get(key);
        if(V.isEmpty(acceptAnnos)){
            return;
        }
        for(String[] annoDef : acceptAnnos){
            boolean override = !"0".equals(annoDef[IDX_OVERRIDE]);
            if(!override){
                Object targetValue = BeanUtils.getProperty(target, annoDef[IDX_TARGET_FIELD]);
                if(targetValue != null){
                    log.debug("目标对象{}已有值{}，copyAcceptProperties将忽略.", key, targetValue);
                    continue;
                }
            }
            Field sourceField = BeanUtils.extractField(source.getClass(), annoDef[IDX_SOURCE_FIELD]);
            if(sourceField != null){
                Object sourceValue = BeanUtils.getProperty(source, annoDef[IDX_SOURCE_FIELD]);
                if(sourceValue != null){
                    BeanUtils.setProperty(target, annoDef[IDX_TARGET_FIELD], sourceValue);
                }
            }
        }
    }
}
