package com.linsir.base.core.binding.helper;


import com.linsir.base.core.constant.Cons;
import com.linsir.base.core.util.BeanUtils;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author: linsir
 * @date: 2022/3/21 10:54
 * @description: 绑定关联数据组装器
 */
@Slf4j
public class ResultAssembler {

    /***
     * 从对象集合提取某个属性值到list中
     * @param setterFieldName
     * @param fromList
     * @param getterFields
     * @param valueMatchMap
     * @param <E>
     */
    public static <E> void bindPropValue(String setterFieldName, List<E> fromList, String[] getterFields, Map valueMatchMap, String splitBy){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        try{
            for(E object : fromList){
                sb.setLength(0);
                for(int i=0; i<getterFields.length; i++){
                    String fieldValue = BeanUtils.getStringProperty(object, getterFields[i]);
                    if(i > 0){
                        sb.append(Cons.SEPARATOR_COMMA);
                    }
                    sb.append(fieldValue);
                }
                // 查找匹配Key
                String matchKey = sb.toString();
                if(valueMatchMap.containsKey(matchKey)){
                    // 赋值
                    BeanUtils.setProperty(object, setterFieldName, valueMatchMap.get(matchKey));
                }
                else if(V.notEmpty(splitBy) && getterFields.length == 1 && matchKey.contains(splitBy)){
                    String[] keys = matchKey.split(splitBy);
                    List matchedValues = new ArrayList(keys.length);
                    for(String key : keys){
                        Object value = valueMatchMap.get(key);
                        if(value != null){
                            if(value instanceof Collection){
                                Collection valueList = (Collection)value;
                                for(Object obj : valueList){
                                    if(!matchedValues.contains(obj)){
                                        matchedValues.add(obj);
                                    }
                                }
                            }
                            else{
                                if(!matchedValues.contains(value)){
                                    matchedValues.add(value);
                                }
                            }
                        }
                    }
                    // 赋值
                    BeanUtils.setProperty(object, setterFieldName, matchedValues);
                }
            }
            sb.setLength(0);
        }
        catch (Exception e){
            log.warn("设置属性值异常, setterFieldName="+setterFieldName, e);
        }
    }

    /***
     * 从对象集合提取某个属性值到list中
     * @param setterFieldName
     * @param fromList
     * @param trunkObjColMapping
     * @param valueMatchMap
     * @param <E>
     */
    public static <E> void bindEntityPropValue(String setterFieldName, List<E> fromList, Map<String, String> trunkObjColMapping, Map valueMatchMap, Map<String, String> col2FieldMapping){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        try{
            for(E object : fromList){
                boolean appendComma = false;
                sb.setLength(0);
                for(Map.Entry<String, String> entry :trunkObjColMapping.entrySet()){
                    //转换为字段名
                    String getterField = col2FieldMapping.get(entry.getKey());
                    if(getterField == null){
                        getterField = S.toLowerCaseCamel(entry.getKey());
                    }
                    String fieldValue = BeanUtils.getStringProperty(object, getterField);
                    if(appendComma){
                        sb.append(Cons.SEPARATOR_COMMA);
                    }
                    sb.append(fieldValue);
                    if(appendComma == false){
                        appendComma = true;
                    }
                }
                // 查找匹配Key
                String matchKey = sb.toString();
                if(valueMatchMap.containsKey(matchKey)){
                    // 赋值
                    BeanUtils.setProperty(object, setterFieldName, valueMatchMap.get(matchKey));
                }
            }
            sb.setLength(0);
        }
        catch (Exception e){
            log.warn("设置属性值异常, setterFieldName="+setterFieldName, e);
        }
    }


    /***
     * 从对象集合提取某个属性值到list中
     * @param fromList
     * @param getterFields
     * @param valueMatchMap
     * @param <E>
     */
    public static <E> void bindFieldListPropValue(List<E> fromList, String[] getterFields, Map<String, List> valueMatchMap,
                                                  List<String> annoObjSetterPropNameList, List<String> refGetterFieldNameList, String splitBy){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        try{
            for(E object : fromList){
                sb.setLength(0);
                for(int i=0; i<getterFields.length; i++){
                    String val = BeanUtils.getStringProperty(object, getterFields[i]);
                    if(i>0){
                        sb.append(Cons.SEPARATOR_COMMA);
                    }
                    sb.append(val);
                }
                // 查找匹配Key
                String matchKey = sb.toString();
                List entityList = valueMatchMap.get(matchKey);
                if(entityList == null && V.notEmpty(splitBy) && matchKey.contains(splitBy)){
                    String[] keys = matchKey.split(splitBy);
                    List matchedValues = new ArrayList(keys.length);
                    for(String key : keys){
                        Object value = valueMatchMap.get(key);
                        if(value != null){
                            if(value instanceof Collection){
                                Collection valueList = (Collection)value;
                                for(Object obj : valueList){
                                    if(!matchedValues.contains(obj)){
                                        matchedValues.add(obj);
                                    }
                                }
                            }
                            else{
                                if(!matchedValues.contains(value)){
                                    matchedValues.add(value);
                                }
                            }
                        }
                    }
                    if(matchedValues != null){
                        entityList = matchedValues;
                    }
                }
                if(entityList != null){
                    // 赋值
                    for(int i = 0; i< annoObjSetterPropNameList.size(); i++){
                        List valObjList = BeanUtils.collectToList(entityList, refGetterFieldNameList.get(i));
                        BeanUtils.setProperty(object, annoObjSetterPropNameList.get(i), valObjList);
                    }
                }
            }
            sb.setLength(0);
        }
        catch (Exception e){
            log.warn("设置属性值异常", e);
        }
    }

    /**
     * 合并为1-1的map结果
     * @param resultSetMapList
     * @param trunkObjColMapping
     * @param branchObjColMapping
     * @param <E>
     * @return
     */
    public static <E> Map<String, Object> convertToOneToOneResult(List<Map<String, E>> resultSetMapList, Map<String, String> trunkObjColMapping, Map<String, String> branchObjColMapping) {
        if(V.isEmpty(resultSetMapList)){
            return Collections.emptyMap();
        }
        // 获取valueName
        String valueName = branchObjColMapping.entrySet().iterator().next().getKey();
        // 合并list为map
        Map<String, Object> resultMap = new HashMap<>(resultSetMapList.size());
        StringBuilder sb = new StringBuilder();
        for(Map<String, E> row : resultSetMapList){
            boolean appendComma = false;
            sb.setLength(0);
            for(Map.Entry<String, String> entry : trunkObjColMapping.entrySet()){
                Object keyObj = getValueIgnoreKeyCase(row, entry.getValue());
                if(appendComma){
                    sb.append(Cons.SEPARATOR_COMMA);
                }
                sb.append(S.valueOf(keyObj));
                if(appendComma == false){
                    appendComma = true;
                }
            }
            String matchKeys = sb.toString();
            Object valueObj = row.containsKey(valueName)? row.get(valueName) : row.get(valueName.toUpperCase());
            resultMap.put(matchKeys, valueObj);
        }
        return resultMap;
    }

    /**
     * 合并为1-n的map结果
     * @param resultSetMapList
     * @param trunkObjColMapping
     * @param branchObjColMapping
     * @param <E>
     * @return
     */
    public static <E> Map<String, List> convertToOneToManyResult(List<Map<String, E>> resultSetMapList, Map<String, String> trunkObjColMapping, Map<String, String> branchObjColMapping){
        if(V.isEmpty(resultSetMapList)){
            return Collections.emptyMap();
        }
        // 获取valueName
        String valueName = branchObjColMapping.entrySet().iterator().next().getKey();
        valueName = S.removeEsc(valueName);
        // 合并list为map
        Map<String, List> resultMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for(Map<String, E> row : resultSetMapList){
            boolean appendComma = false;
            sb.setLength(0);
            for(Map.Entry<String, String> entry : trunkObjColMapping.entrySet()){
                Object keyObj = getValueIgnoreKeyCase(row, entry.getValue());
                if(appendComma){
                    sb.append(Cons.SEPARATOR_COMMA);
                }
                sb.append(S.valueOf(keyObj));
                if(appendComma == false){
                    appendComma = true;
                }
            }
            String matchKeys = sb.toString();
            Object valueObj = row.containsKey(valueName)? row.get(valueName) : row.get(valueName.toUpperCase());
            if(valueObj != null){
                List valueList = resultMap.get(matchKeys);
                if(valueList == null){
                    valueList = new ArrayList();
                    resultMap.put(matchKeys, valueList);
                }
                valueList.add(valueObj);
            }
        }
        sb.setLength(0);
        return resultMap;
    }


    /**
     * 从map中取值，如直接取为null尝试转换大写后再取，以支持ORACLE等大写命名数据库
     * @param map
     * @param key
     * @return
     */
    public static Object getValueIgnoreKeyCase(Map<String, ?> map, String key){
        if(map == null || key == null){
            return null;
        }
        key = S.removeEsc(key);
        if(map.containsKey(key)){
            return map.get(key);
        }
        if(map.containsKey(key.toUpperCase())){
            return map.get(key.toUpperCase());
        }
        return null;
    }

    /**
     * 拆解值列表
     * @param valueList
     * @param splitBy
     * @return
     */
    public static List unpackValueList(List valueList, String splitBy) {
        List newValueList = new ArrayList();
        valueList.forEach( value -> {
            if(value != null){
                String valueStr = S.valueOf(value);
                if(valueStr.contains(splitBy)){
                    for(String oneVal :valueStr.split(splitBy)){
                        if(!newValueList.contains(oneVal)){
                            newValueList.add(oneVal);
                        }
                    }
                }
                else if(!newValueList.contains(valueStr)){
                    newValueList.add(valueStr);
                }
            }
        });
        return newValueList;
    }
}
