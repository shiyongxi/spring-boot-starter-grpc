package com.yx.grpc.server.service;

import io.grpc.MethodDescriptor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-25 11:38
 * @Description: MethodSuplier
 */
@Getter
public class MethodSuplier {
    private MethodDescriptor methodDescriptor;

    private Object bean;

    private Method method;

    public MethodSuplier(Object bean, Method method, MethodDescriptor methodDescriptor) {
        this.bean = bean;
        this.method = method;
        this.methodDescriptor = methodDescriptor;
    }
}
