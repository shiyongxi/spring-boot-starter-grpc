package com.yx.grpc.client.interceptor;

import io.grpc.ClientInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 16:26
 * @Description: AnnotationGlobalClientInterceptorConfigurer
 */
@Slf4j
public class AnnotationGlobalClientInterceptorConfigurer implements GlobalClientInterceptorConfigurer {
    @Autowired
    private ApplicationContext context;

    @Override
    public void addClientInterceptors(final GlobalClientInterceptorRegistry registry) {
        this.context.getBeansWithAnnotation(GrpcGlobalClientInterceptor.class)
                .forEach((name, bean) -> {
                    ClientInterceptor interceptor = (ClientInterceptor) bean;
                    log.debug("Registering GlobalClientInterceptor: {} ({})", name, interceptor);
                    registry.addClientInterceptors(interceptor);
                });
    }
}
