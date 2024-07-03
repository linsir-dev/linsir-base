package com.linsir.base.core.data.mask;


import com.linsir.base.core.util.S;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/25 11:49
 * @description：脱敏策略默认实现
 * @modified By：
 * @version: 0.0.1
 */
public class DefaultMaskStrategy implements  IMaskStrategy{

    @Override
    public String mask(String content) {
        if (S.isBlank(content)) {
            return S.EMPTY;
        }
        int length = content.length();
        switch (length) {
            case 11:
                // 11位手机号，保留前3位和后4位
                return S.replace(content, 3, length - 4, '*');
            case 18:
                // 18位身份证号，保留前6位和后4位
                return S.replace(content, 6, length - 4, '*');
            default:
                // 其他长度，保留前0位和后4位，长度小于5位不脱敏
                return S.replace(content, 0, length - 4, '*');
        }
    }
}
