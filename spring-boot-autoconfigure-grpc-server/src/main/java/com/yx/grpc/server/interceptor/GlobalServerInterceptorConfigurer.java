package com.yx.grpc.server.interceptor;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 11:56
 * @Description: GlobalServerInterceptorConfigurer
 */
@FunctionalInterface
public interface GlobalServerInterceptorConfigurer {
    void addServerInterceptors(GlobalServerInterceptorRegistry registry);
}
