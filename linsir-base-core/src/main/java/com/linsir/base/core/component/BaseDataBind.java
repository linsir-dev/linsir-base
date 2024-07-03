package com.linsir.base.core.component;

import lombok.extern.slf4j.Slf4j;
/*import mybatis.mate.annotation.FieldBind;
import mybatis.mate.sets.IDataBind;*/
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author Administrator
 * @title: DataBind
 * @projectName lins
 * @description: 实现系统字典 基类
 * @date 2022/1/6 10:51
 */
@Slf4j
public class BaseDataBind /*implements IDataBind*/ {

    /*private IBaseDictService baseDictService;

    public void setIBaseDictService(IBaseDictService baseDictService)
    {
        this.baseDictService = baseDictService;
    }

    @Override
    public void setMetaObject(FieldBind fieldBind, Object fieldValue, MetaObject metaObject) {
        log.info(">>>>开始绑定:{}",fieldBind.target());
        metaObject.setValue(fieldBind.target(),baseDictService.getText(fieldBind.type(),fieldValue));
    }*/
}
