package com.linsir.base.core.binding.binder.parallel;

import com.linsir.base.core.binding.annotation.*;
import com.linsir.base.core.binding.binder.*;
import com.linsir.base.core.binding.parser.ConditionManager;
import com.linsir.base.core.binding.parser.FieldAnnotation;
import com.linsir.base.core.exception.InvalidUsageException;
import com.linsir.base.core.service.DictionaryServiceExtProvider;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 11:01
 * @description：并行绑定Manager
 * @modified By：
 * @version: 0.0.1
 */

@Slf4j
@Component
public class ParallelBindingManager {
    @Autowired(required = false)
    private DictionaryServiceExtProvider dictionaryServiceExtProvider;

    /**
     * 绑定字典
     * @param voList
     * @param fieldAnno
     * @return
     */
    @Async
    public CompletableFuture<Boolean> doBindingDict(List voList, FieldAnnotation fieldAnno){
        if(dictionaryServiceExtProvider != null){
            BindDict annotation = (BindDict) fieldAnno.getAnnotation();
            String dictValueField = annotation.field();
            if(V.isEmpty(dictValueField)){
                dictValueField = S.replace(fieldAnno.getFieldName(), "Label", "");
                log.debug("BindDict未指定field，将默认取值为: {}", dictValueField);
            }
            // 字典绑定接口化
            dictionaryServiceExtProvider.bindItemLabel(voList, fieldAnno.getFieldName(), dictValueField, annotation.type());
        }
        else{
            throw new InvalidUsageException("BindDictService未实现，无法使用BindDict注解！");
        }
        return CompletableFuture.completedFuture(true);
    }

    /***
     * 绑定Field
     * @param voList
     * @param fieldAnnotations
     */
    @Async
    public CompletableFuture<Boolean> doBindingField(List voList, List<FieldAnnotation> fieldAnnotations){
        BindField bindAnnotation = (BindField) fieldAnnotations.get(0).getAnnotation();
        FieldBinder binder = new FieldBinder(bindAnnotation, voList);
        for(FieldAnnotation anno : fieldAnnotations){
            BindField bindField = (BindField) anno.getAnnotation();
            binder.link(bindField.field(), anno.getFieldName());
        }
        // 解析条件并且执行绑定
        return doBinding(binder, bindAnnotation.condition());
    }

    /***
     * 绑定FieldList
     * @param voList
     * @param fieldAnnotations
     */
    @Async
    public CompletableFuture<Boolean> doBindingFieldList(List voList, List<FieldAnnotation> fieldAnnotations){
        BindFieldList bindAnnotation = (BindFieldList) fieldAnnotations.get(0).getAnnotation();
        FieldListBinder binder = new FieldListBinder(bindAnnotation, voList);
        for(FieldAnnotation anno : fieldAnnotations){
            BindFieldList bindField = (BindFieldList) anno.getAnnotation();
            binder.link(bindField.field(), anno.getFieldName());
        }
        // 解析条件并且执行绑定
        return doBinding(binder, bindAnnotation.condition());
    }

    /***
     * 绑定Entity
     * @param voList
     * @param fieldAnnotation
     */
    @Async
    public CompletableFuture<Boolean> doBindingEntity(List voList, FieldAnnotation fieldAnnotation) {
        BindEntity annotation = (BindEntity) fieldAnnotation.getAnnotation();
        // 绑定关联对象entity
        EntityBinder binder = new EntityBinder(annotation, voList);
        // 构建binder
        binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
        // 解析条件并且执行绑定
        return doBinding(binder, annotation.condition());
    }

    /***
     * 绑定EntityList
     * @param voList
     * @param fieldAnnotation
     */
    @Async
    public CompletableFuture<Boolean> doBindingEntityList(List voList, FieldAnnotation fieldAnnotation) {
        BindEntityList annotation = (BindEntityList) fieldAnnotation.getAnnotation();
        // 构建binder
        EntityListBinder binder = new EntityListBinder(annotation, voList);
        binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
        // 解析条件并且执行绑定
        return doBinding(binder, annotation.condition());
    }

    /***
     * 绑定count计数
     * @param voList
     * @param fieldAnnotation
     */
    @Async
    public CompletableFuture<Boolean> doBindingCount(List voList, FieldAnnotation fieldAnnotation) {
        BindCount annotation = (BindCount) fieldAnnotation.getAnnotation();
        // 绑定关联对象entity
        CountBinder binder = new CountBinder(annotation, voList);
        // 构建binder
        binder.set(fieldAnnotation.getFieldName(), fieldAnnotation.getFieldClass());
        // 解析条件并且执行绑定
        return doBinding(binder, annotation.condition());
    }

    /**
     * 绑定表关联数据
     * @param binder
     * @param condition
     * @return
     */
    private CompletableFuture<Boolean> doBinding(BaseBinder binder, String condition){
        ConditionManager.parseConditions(condition, binder);
        binder.bind();
        return CompletableFuture.completedFuture(true);
    }
}
