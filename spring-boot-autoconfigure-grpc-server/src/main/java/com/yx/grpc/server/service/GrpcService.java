package com.yx.grpc.server.service;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-24 16:21
 * @Description: GrpcService
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface GrpcService {
    String serviceName();

    Class<?> protoClass();
}