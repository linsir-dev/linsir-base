package com.linsir.base.core.vo.jsonResults;

import com.linsir.base.core.vo.Pagination;
import lombok.Data;

import java.util.List;

/**
 * description:
 *
 * @author [linsir]
 * @version 0.0.1
 * @date 2022/08/16 20:37:00
 *
 *
 * 增加 字段 items for 前端
 * by linsir 2023.4.21
 */

@Data
public class PageVO<VO,S> {

    private Long total;

    private int page;

    private int pageSize;

    private S summary;

    private List<VO> rows;

    private List<VO> items;

    public PageVO(Pagination pagination ,List<VO> rows){

        this.total = pagination.getTotalCount();
        this.pageSize = pagination.getPageSize();
        this.page = pagination.getPageIndex();
        this.rows =rows;
        this.items =rows;
    }
}
