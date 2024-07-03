package com.linsir.base.core.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author linsir
 * @title: AbstractEntity
 * @projectName linsir
 * @description: Entity抽象父类
 * @date 2022/3/19 23:05
 *
 * 下面介绍几个我常用的 lombok 注解：
 * @Data   ：注解在类上；提供类所有属性的 getting 和 setting 方法，此外还提供了equals、canEqual、hashCode、toString 方法
 * @Setter：注解在属性上:为属性提供 setting 方法,       注解再类上表示当前类中所有属性都生成setter方法
 * @Getter：注解在属性上：为属性提供 getting 方法， 注解再类上表示当前类中所有属性都生成getter方法
 * @Log4j ：注解在类上；为类提供一个 属性名为log 的 log4j 日志对象
 * @NoArgsConstructor：注解在类上；为类提供一个无参的构造方法
 * @AllArgsConstructor：注解在类上；为类提供一个全参的构造方法
 *
 *
 */
@Data
public abstract class AbstractEntity<T extends Serializable>  implements Serializable{

    private static final long serialVersionUID = -3213747504298736681L;
    /** 主键  默认主键字段id，类型为Long型雪花，转json时转换为String*/
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private T id;


    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy ;


    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createdTime ;
    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy ;

    /** 更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime ;

    public AbstractEntity setId(T id){
        this.id = id;
        return this;
    }

    public T getId(){
        return this.id;
    }


}
