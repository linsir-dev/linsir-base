package com.linsir.base.core.dto;

import com.linsir.base.core.util.BeanUtils;
import com.linsir.base.core.vo.Pagination;
import lombok.Data;

/**
 * @author yuxiaolin
 * @title: PageDto
 * @projectName linsir
 * @description: TODO
 * @date 2022/2/4 7:22 下午
 */
@Data
public class PageDto extends CommonBaseDto{

   private int pageSize;

   private int page;

   private  Pagination pagination;

   public PageDto()
   {
       this.pagination.setPageSize(pageSize);
       this.pagination.setPageIndex(page);
   }

}
