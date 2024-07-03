package com.linsir.base.core.vo.jsonResults;

import lombok.Data;

/**
 * description:
 *
 * @author [linsir]
 * @version 0.0.1
 * @date 2022/08/16 19:40:33
 */
@Data
public class Summary {

    private String num;

    private String progress;

    public Summary(String num,String progress)
    {
        this.num =num;
        this.progress =progress;
    }
}
