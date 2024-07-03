package com.linsir.base.core.vo;

import lombok.Data;

/**
 * @author linsir
 * @version v0.0.2
 * @package com.linsir.core.vo
 * @project linsir
 * @description $
 * @createTime 2024/1/18 13:34
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */

@Data
public class UserInfoModel {

    private Long userId;

    private String username;

    private String realName;

    private String avatar;

    private String desc;
}
