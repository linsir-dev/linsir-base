package com.linsir.base.api;

/**
 * @author ：linsir
 * @date ：Created in 2022/5/28 15:21
 * @description：
 *
 *  1、cloud接口数量43  local：35 common：9  额外一个特殊queryAllRole一个当两个用
 *  *  - 相比较local版
 *  *  - 去掉了一些方法：addLog、getDatabaseType、queryAllDepart、queryAllUser(Wrapper wrapper)、queryAllUser(String[] userIds, int pageNo, int pageSize)
 *  *  - 修改了一些方法：createLog、sendSysAnnouncement（只保留了一个，其余全部干掉）
 *  * 2、@ConditionalOnMissingClass("org.jeecg.modules.system.service.impl.SysBaseApiImpl")=> 有实现类的时候，不实例化Feign接口
 * @modified By：
 * @version:
 */
public interface ISysBaseAPI /*extends CommonAPI*/{
}
