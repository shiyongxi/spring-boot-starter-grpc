package com.yx.grpc.server.service;

import com.google.protobuf.Descriptors;


/**
 * @Auther: shiyongxi
 * @Date: 2020-03-24 15:26
 * @Description: BaseDescriptorSupplier
 */
public class BaseDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    private String serviceName;
    private Descriptors.FileDescriptor fileDescriptor;

    public BaseDescriptorSupplier(String serviceName, Descriptors.FileDescriptor fileDescriptor) {
        this.serviceName = serviceName;
        this.fileDescriptor = fileDescriptor;
    }

    @Override
    public Descriptors.ServiceDescriptor getServiceDescriptor() {
        return getFileDescriptor().findServiceByName(serviceName);
    }

    @Override
    public Descriptors.FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }
}
