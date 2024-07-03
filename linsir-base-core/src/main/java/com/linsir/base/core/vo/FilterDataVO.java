package com.linsir.base.core.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/8/30 1:16
 * @description：筛选数据视图
 * @modified By：
 * @version: 0.0.1
 */
@Data
public class FilterDataVO {

    private  String title;

    private String key;

    /*是否多选*/
    private boolean multiple;


    private List<Option> options;


}
