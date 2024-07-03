package com.linsir.base.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @title: JacksonUtils
 * @projectName linsir
 * @description: 基于 Jackson 的 json 工具类
 * @date 2021/12/17 15:20
 */

@SuppressWarnings({"unchecked", "JavaDoc", "UnnecessaryLocalVariable"})
@Slf4j
public class JSON {

    private  static ObjectMapper objectMapper;

    @Resource
    private static MappingJackson2HttpMessageConverter jacksonMessageConvertor;

    /*static {
        // 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 允许不带引号的字段名称
        objectMapper.configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true);
        // 允许单引号
        objectMapper.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
        // allow int startWith 0
        objectMapper.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true);
        // 允许字符串存在转义字符：\r \n \t
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        // 排除空值字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 使用驼峰式
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        // 使用bean名称
        objectMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
        // 所有日期格式都统一为固定格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }*/

    /**
     * 初始化ObjectMapper
     * @return
     */
    private static ObjectMapper getObjectMapper(){

        //ToDO 相关属性，在jacksonMessageConvertor 设置
        if(objectMapper != null){
            return objectMapper;
        }
        // 获取全局的ObjectMapper, 避免重复配置
        //MappingJackson2HttpMessageConverter jacksonMessageConvertor = ContextHelper.getBean(MappingJackson2HttpMessageConverter.class);
        if(jacksonMessageConvertor != null){
            objectMapper = jacksonMessageConvertor.getObjectMapper();
            log.debug("初始化ObjectMapper完成（复用全局定义）");
        }
        else{
            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // 设置时区和日期转换
            //String defaultDatePattern = BaseConfig.getProperty("spring.jackson.date-format", D.FORMAT_DATETIME_Y4MDHMS);
            /*SimpleDateFormat dateFormat = new SimpleDateFormat(defaultDatePattern) {
                @Override
                public Date parse(String dateStr) {
                    return D.fuzzyConvert(dateStr);
                }
            };*/
            /*objectMapper.setDateFormat(dateFormat);*/
            /*String timeZone = BaseConfig.getProperty("spring.jackson.time-zone", "GMT+8");*/
            /*objectMapper.setTimeZone(TimeZone.getTimeZone(timeZone));*/
            // 不存在的属性，不转化，否则报错：com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            log.debug("初始化ObjectMapper完成（new新实例）");
        }
        return objectMapper;
    }

    /**
     * 将Java对象转换为Json String
     *
     * @param object
     * @return
     */
    public static String stringify(Object object) {
        return toJSONString(object);
    }

    /**
     * 转换对象为JSON字符串
     *
     * @param model
     * @return
     */
    public static String toJSONString(Object model) {
        try {
            String json = getObjectMapper().writeValueAsString(model);
            return json;
        } catch (Exception e) {
            log.error("Java转Json异常", e);
            return null;
        }
    }

    /***
     * 将JSON字符串转换为java对象
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T toJavaObject(String jsonStr, Class<T> clazz) {
        try {
            T model = getObjectMapper().readValue(jsonStr, clazz);
            return model;
        } catch (Exception e) {
            log.error("Json转Java异常", e);
            return null;
        }
    }

    /***
     * 将JSON字符串转换为Map<String, Object></>对象
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> parseObject(String jsonStr) {
        try {
            JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            return getObjectMapper().readValue(jsonStr, javaType);
        } catch (Exception e) {
            log.error("Json转Java异常", e);
            return null;
        }
    }

    /***
     * 将JSON字符串转换为java对象
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        return toJavaObject(jsonStr, clazz);
    }

    /***
     * 将JSON字符串转换为复杂类型的Java对象
     * @param jsonStr
     * @param typeReference
     * @return
     */
    public static <T> T parseObject(String jsonStr, TypeReference<T> typeReference) {
        try {
            T model = getObjectMapper().readValue(jsonStr, typeReference);
            return model;
        } catch (Exception e) {
            log.error("Json转Java异常", e);
            return null;
        }
    }


    /***
     * 将JSON字符串转换为list对象
     * @param jsonStr
     * @return
     */
    public static <T> List<T> parseArray(String jsonStr, Class<T> clazz) {
        try {
            JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(List.class, clazz);
            return getObjectMapper().readValue(jsonStr, javaType);
        } catch (Exception e) {
            log.error("Json转Java异常", e);
            return null;
        }
    }

    /***
     * 将JSON字符串转换为java对象
     * @param jsonStr
     * @return
     */

    public static <K, T> Map<K, T> toMap(String jsonStr) {
        return (Map<K, T>) toJavaObject(jsonStr, Map.class);
    }

    /***
     * 将JSON字符串转换为Map对象
     * @param jsonStr
     * @return
     */
    public static<K, T> LinkedHashMap<K, T> toLinkedHashMap(String jsonStr) {
        if (V.isEmpty(jsonStr)) {
            return null;
        }
        return (LinkedHashMap<K, T>)toJavaObject(jsonStr, LinkedHashMap.class);
    }

}
