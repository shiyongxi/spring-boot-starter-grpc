package com.yx.grpc.server.service;

import com.google.protobuf.Descriptors;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-24 15:48
 * @Description: MethodDescriptorSupplier
 */
public class MethodDescriptorSupplier extends BaseDescriptorSupplier
        implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    MethodDescriptorSupplier(String serviceName, Descriptors.FileDescriptor fileDescriptor, String methodName) {
        super(serviceName, fileDescriptor);
        this.methodName = methodName;
    }

    @Override
    public Descriptors.MethodDescriptor getMethodDescriptor() {
        return getServiceDescriptor().findMethodByName(methodName);
    }
}
