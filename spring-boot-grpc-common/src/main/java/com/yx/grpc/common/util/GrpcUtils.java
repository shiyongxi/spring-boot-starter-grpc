package com.yx.grpc.common.util;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-16 18:10
 * @Description: GrpcUtils
 */

import io.grpc.MethodDescriptor;

/**
 * Utility class that contains methods to extract some information from grpc classes.
 *
 * @author Daniel Theuke (daniel.theuke@heuboe.de)
 */
public final class GrpcUtils {

    /**
     * The cloud discovery metadata key used to identify the grpc port.
     */
    public static final String CLOUD_DISCOVERY_METADATA_PORT = "gRPC.port";

    /**
     * Extracts the service name from the given method.
     *
     * @param method The method to get the service name from.
     * @return The extracted service name.
     * @see MethodDescriptor#extractFullServiceName(String)
     * @see #extractMethodName(MethodDescriptor)
     */
    public static String extractServiceName(final MethodDescriptor<?, ?> method) {
        return MethodDescriptor.extractFullServiceName(method.getFullMethodName());
    }

    /**
     * Extracts the method name from the given method.
     *
     * @param method The method to get the method name from.
     * @return The extracted method name.
     * @see #extractServiceName(MethodDescriptor)
     */
    public static String extractMethodName(final MethodDescriptor<?, ?> method) {
        // This method is the equivalent of MethodDescriptor.extractFullServiceName
        final String fullMethodName = method.getFullMethodName();
        final int index = fullMethodName.lastIndexOf('/');
        if (index == -1) {
            return fullMethodName;
        }
        return fullMethodName.substring(index + 1);
    }

    private GrpcUtils() {}

}
