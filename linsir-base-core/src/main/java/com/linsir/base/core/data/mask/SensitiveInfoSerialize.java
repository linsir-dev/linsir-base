package com.linsir.base.core.data.mask;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.linsir.base.core.binding.parser.ParserCache;
import com.linsir.base.core.data.annotation.ProtectField;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/25 11:51
 * @description：敏感信息序列化
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
public class SensitiveInfoSerialize<E> extends JsonSerializer<E> implements ContextualSerializer {
    /**
     * 脱敏策略
     */
    private IMaskStrategy maskStrategy;

    public SensitiveInfoSerialize() {
    }

    public SensitiveInfoSerialize(Class<? extends IMaskStrategy> clazz) {
        this.maskStrategy = ParserCache.getMaskStrategy(clazz);
    }

    @Override
    public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof List) {
            gen.writeObject(((List<String>) value).stream().map(e -> maskStrategy.mask(e)).collect(Collectors.toList()));
        } else {
            gen.writeObject(maskStrategy.mask((String) value));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (null == property) {
            return prov.findNullValueSerializer(null);
        }
        Class<?> rawClass = property.getType().getRawClass();
        if (rawClass == String.class || (rawClass == List.class && property.getType().getContentType().getRawClass() == String.class)) {
            ProtectField protect = property.getAnnotation(ProtectField.class);
            if (null == protect) {
                protect = property.getContextAnnotation(ProtectField.class);
            }
            if (null != protect) {
                return new SensitiveInfoSerialize(protect.mask());
            }
        } else {
            log.error("`@ProtectField` 只支持 String 与 List<String> 类型脱敏！");
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
