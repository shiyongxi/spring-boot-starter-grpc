package com.yx.grpc.client.interceptor;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 16:20
 * @Description: GrpcGlobalClientInterceptor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface GrpcGlobalClientInterceptor {
}
