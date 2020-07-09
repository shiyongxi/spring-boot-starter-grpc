package com.yx.grpc.server.interceptor;

import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 11:55
 * @Description: AnnotationGlobalServerInterceptorConfigurer
 */
@Slf4j
public class AnnotationGlobalServerInterceptorConfigurer implements GlobalServerInterceptorConfigurer {
    @Autowired
    private ApplicationContext context;

    @Override
    public void addServerInterceptors(final GlobalServerInterceptorRegistry registry) {
        this.context.getBeansWithAnnotation(GrpcGlobalServerInterceptor.class)
                .forEach((name, bean) -> {
                    ServerInterceptor interceptor = (ServerInterceptor) bean;
                    log.debug("Registering GlobalServerInterceptor: {} ({})", name, interceptor);
                    registry.addServerInterceptors(interceptor);
                });
    }
}
