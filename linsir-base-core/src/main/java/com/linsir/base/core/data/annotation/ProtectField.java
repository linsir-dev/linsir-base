package com.linsir.base.core.data.annotation;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 14:48
 * @description：保护字段 对字段进行 加密存储，脱敏展示
 * @modified By：
 * @version: 0.0.1
 */
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.linsir.base.core.data.encrypt.DoNothingEncryptStrategy;
import com.linsir.base.core.data.encrypt.IEncryptStrategy;
import com.linsir.base.core.data.mask.DoNothingMaskStrategy;
import com.linsir.base.core.data.mask.IMaskStrategy;
import com.linsir.base.core.data.mask.SensitiveInfoSerialize;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveInfoSerialize.class)
@Inherited
@Documented
public @interface ProtectField {
    /**
     * 加密策略
     */
    Class<? extends IEncryptStrategy> encryptor() default DoNothingEncryptStrategy.class;

    /**
     * 脱敏策略
     */
    Class<? extends IMaskStrategy> mask() default DoNothingMaskStrategy.class;
}
