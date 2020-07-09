package com.yx.grpc.client.interceptor;

import io.grpc.ClientInterceptor;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 10:26
 * @Description: GlobalClientInterceptorConfigurer
 */
@FunctionalInterface
public interface GlobalClientInterceptorConfigurer {
    /**
     * Adds the {@link ClientInterceptor}s that should be registered globally to the given registry.
     *
     * @param registry The registry the interceptors should be added to.
     */
    void addClientInterceptors(GlobalClientInterceptorRegistry registry);
}
