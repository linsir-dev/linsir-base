package com.linsir.base.core.service;

import com.linsir.base.core.entity.Dictionary;
import com.linsir.base.core.vo.DictionaryVO;
import com.linsir.base.core.vo.FilterDataVO;
import com.linsir.base.core.vo.LabelValue;

import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/24 10:34
 * @description：BindDict等字典服务绑定Service提供接口
 * @modified By：
 * @version: 0.0.1
 */
public interface DictionaryServiceExtProvider {
    /**
     * 绑定字典的label
     *
     * @param voList
     * @param setFieldName
     * @param getFieldName
     * @param type
     */
    void bindItemLabel(List voList, String setFieldName, String getFieldName, String type);

    /**
     * 获取字典类型对应的子项键值对
     *
     * @param dictType
     * @return
     */
    List<LabelValue> getLabelValueList(String dictType);

    /**
     * 是否存在某字典类型定义
     *
     * @param dictType
     * @return
     */
    boolean existsDictType(String dictType);

    /**
     * 创建字典及子项
     *
     * @param dictionaryVO
     * @return
     */
    boolean createDictAndChildren(DictionaryVO dictionaryVO);

    /**
     * 查询字典定义的List（不含子项）
     *
     * @return
     */
    List<Dictionary> getDictDefinitionList();

    /**
     * 查询字典VOList（含子项）
     *
     * @return
     */
    List<DictionaryVO> getDictDefinitionVOList();

    /**
     * 转化前端识别得数据结构
     * @param title
     * @param key
     * @param multiple
     * @param dictionaryList
     * @return
     */
    FilterDataVO conversionFilterDataVO(String title,String key,boolean multiple,List<Dictionary> dictionaryList);
}
