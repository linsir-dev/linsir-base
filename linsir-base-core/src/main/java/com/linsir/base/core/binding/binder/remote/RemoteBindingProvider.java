package com.linsir.base.core.binding.binder.remote;

import com.linsir.base.core.vo.jsonResults.JsonResult;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 23:32
 * @description：远程绑定Provider接口
 * @modified By：
 * @version: 0.0.1
 */
public interface RemoteBindingProvider {
    /**
     * 加载请求数据
     * @param remoteBindDTO
     * @return
     */
    @PostMapping("/common/remoteBinding")
    JsonResult<String> loadBindingData(RemoteBindDTO remoteBindDTO);
}
