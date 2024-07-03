package com.linsir.base.core.vo.jsonResults;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.linsir.base.core.constant.CommonConstant;
import com.linsir.base.core.constant.Cons;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import com.linsir.base.core.vo.Pagination;
import lombok.Getter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 23:47
 * @description：JSON返回结果
 * @modified By：
 * @version:
 */
@Getter
public class PagingJsonResult<T> extends JsonResult {

    @Serial
    private static final long serialVersionUID = -3062032234729254533L;

    /***
     * 分页相关信息
     */
    private Pagination page;

    public PagingJsonResult(){
    }

    /**
     * 默认成功，无返回数据
     */
    public PagingJsonResult(JsonResult<T> jsonResult, Pagination pagination){
        super(jsonResult.getCode(), jsonResult.getMessage(), jsonResult.getData());
        this.page = pagination;
    }

    /**
     * 基于IPage<T>转换为PagingJsonResult
     * @param iPage
     * @param <T>
     */
    public <T> PagingJsonResult(IPage<T> iPage){
        Pagination pagination = new Pagination();
        pagination.setPageIndex((int)iPage.getCurrent());
        pagination.setPageSize((int)iPage.getSize());
        pagination.setTotalCount(iPage.getTotal());

        if(V.notEmpty(iPage.orders())){
            List<String> orderByList = new ArrayList<>();
            iPage.orders().stream().forEach(o ->{
                if(o.isAsc()){
                    orderByList.add(o.getColumn());
                }
                else{
                    orderByList.add(o.getColumn() + ":" + CommonConstant.ORDER_DESC);
                }
            });
            pagination.setOrderBy(S.join(orderByList));
        }
        this.page = pagination;

        this.data(iPage.getRecords());
    }


    public PagingJsonResult<T> setPage(Pagination pagination){
        this.page = pagination;
        return this;
    }

}
