package com.linsir.base.core.vo;

import com.linsir.base.core.binding.annotation.BindEntityList;
import com.linsir.base.core.entity.Dictionary;
import lombok.Data;

import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 15:20
 * @description：数据字典的VO，附带子项定义children
 * @modified By：
 * @version: 0.0.1
 */
@Data
public class DictionaryVO extends Dictionary {

    @BindEntityList(entity= Dictionary.class, condition="this.type=type AND this.id=parent_id", orderBy = "sort_id:ASC")
    private List<Dictionary> children;
}
