package com.linsir.base.core.boot.context;

import com.linsir.base.core.util.ThreadLocalUtil;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @ClassName : LinsirRunnableWrapper
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-19 21:21
 */

public class LinsirRunnableWrapper implements Runnable{
    private final Runnable delegate;
    private final Map<String, Object> tlMap;
    /**
     * logback 下有可能为 null
     */
    @Nullable
    private final Map<String, String> mdcMap;

    public LinsirRunnableWrapper(Runnable runnable) {
        this.delegate = runnable;
        this.tlMap = ThreadLocalUtil.getAll();
        this.mdcMap = MDC.getCopyOfContextMap();
    }

    @Override
    public void run() {
        if (!tlMap.isEmpty()) {
            ThreadLocalUtil.put(tlMap);
        }
        if (mdcMap != null && !mdcMap.isEmpty()) {
            MDC.setContextMap(mdcMap);
        }
        try {
            delegate.run();
        } finally {
            tlMap.clear();
            if (mdcMap != null) {
                mdcMap.clear();
            }
            ThreadLocalUtil.clear();
            MDC.clear();
        }
    }
}
