package com.linsir.base.core.dto;

import lombok.Data;

/**
 * @author linsir
 * @version 1.0.0
 * @title PageQuery
 * @description
 * @create 2024/8/25 10:27
 */


@Data
public class PageQuery {

    private long pageNum;

    private long pageSize;
}
