package com.yx.grpc.server.util;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-25 10:04
 * @Description: ReflectionUtils
 */
@Slf4j
public class ReflectionUtils {
    public static Descriptors.FileDescriptor invokeFileDescripto(Class<?> clazz) {
        Method method = null;
        try {
            method = clazz.getMethod("getDescriptor");
            return (Descriptors.FileDescriptor) method.invoke(null);
        } catch (ReflectiveOperationException e) {
            log.error("获取Descriptors.FileDescriptor失败，clazz={}", clazz.getName());
        }

        return null;
    }

    public static Message invokeDefaultInstance(Class<?> clazz) {
        Method method = null;
        final String getDefaultInstance = "getDefaultInstance";
        try {
            method = clazz.getMethod(getDefaultInstance);
            return (Message) method.invoke(null);
        } catch (ReflectiveOperationException e) {
            log.error("获取{}失败，clazz={}", getDefaultInstance, clazz.getName());
        }

        return null;
    }
}
