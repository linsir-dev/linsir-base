package com.linsir.base.core.data.access;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/25 11:38
 * @description：数据权限校验扩展接口
 * @modified By：
 * @version: 0.0.1
 */
public interface DataAccessInterface {
    /**
     * <h3>可访问的对象ID</h3>
     * <br/>
     * <table border="10">
     * <caption>添加条件规则</caption>
     * <tr>
     * <th>返回值</th>
     * <th>SQL</th>
     * <th>说明</th>
     * </tr>
     * <tr>
     * <td>null</td>
     * <td>-</td>
     * <td>为null不加入条件</td>
     * </tr>
     * <td>[]</td>
     * <td>IS NULL</td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>[10001]</td>
     * <td>= '10001'</td>
     * <td>长度等于1的列表</td>
     * </tr>
     * <tr>
     * <td>[10001, 10002] &nbsp</td>
     * <td>IN ('10001', '10002') &nbsp</td>
     * <td>长度大于1的列表</td>
     * </tr>
     * </table>
     */
    List<Serializable> getAccessibleIds(Class<?> entityClass, String fieldName);
}
