package com.linsir.base.core.vo.jsonResults;

import com.linsir.base.core.code.ICode;
import com.linsir.base.core.vo.R;
import lombok.Data;

/**
 * @ProjectName: linsir
 * @Package: com.linsir.core.vo.jsonResults
 * @ClassName: ResResult
 * @Description: 转化成前端所需要的Json$
 * @Author:Linsir
 * @CreateDate: 2022/9/13 16:59
 * @UpdateDate: 2022/9/13 16:59
 * @Version: 0.0.1$
 */

@Deprecated
@Data
public class ResResult<T> {

    private int code;

    private String message;

    private T data;

    public ResResult(R<ICode,T> result)
    {
        this.code = result.getHead().getCode();
        this.message =result.getHead().getMsg();
        this.data = result.getBody();
    }
}
