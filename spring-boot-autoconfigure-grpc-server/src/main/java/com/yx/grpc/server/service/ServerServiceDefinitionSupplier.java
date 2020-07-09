package com.yx.grpc.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.Descriptors;
import com.yx.grpc.server.interceptor.GlobalServerInterceptorRegistry;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.grpc.stub.ServerCalls.asyncUnaryCall;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-25 11:34
 * @Description: ServerServiceDefinitionSupplier
 */
@Slf4j
public class ServerServiceDefinitionSupplier {
    private static Map<String, List<MethodSuplier>> methodDescriptors = Maps.newConcurrentMap();
    private static Map<String, Descriptors.FileDescriptor> fileDescriptors = Maps.newConcurrentMap();

    public static void addMethodSuplier(String serviceName, MethodSuplier methodSuplier, Descriptors.FileDescriptor fileDescriptor) {
        fileDescriptors.put(serviceName, fileDescriptor);

        List<MethodSuplier> methodSupliers = methodDescriptors.get(serviceName);
        if (methodSupliers == null) {
            synchronized (ServerServiceDefinitionSupplier.class) {
                methodSupliers = methodDescriptors.get(serviceName);

                if (methodSupliers == null) {
                    methodSupliers = new ArrayList<>();
                    methodDescriptors.put(serviceName, methodSupliers);
                }
            }
        }

        methodSupliers.add(methodSuplier);
    }

    public static List<ServerServiceDefinition> getServerServiceDefinition(final GlobalServerInterceptorRegistry globalServerInterceptorRegistry) {
        List<ServerServiceDefinition> serverServiceDefinitions = Lists.newArrayListWithCapacity(methodDescriptors.size());

        for (Map.Entry<String, List<MethodSuplier>> entry : methodDescriptors.entrySet()) {
            final String serviceName = entry.getKey();

            final BaseDescriptorSupplier baseDescriptorSupplier = new BaseDescriptorSupplier(serviceName, fileDescriptors.get(serviceName));
            final ServiceDescriptor.Builder serviceBuilder = ServiceDescriptor.newBuilder(serviceName).setSchemaDescriptor(baseDescriptorSupplier);
            for (MethodSuplier methodSuplier : entry.getValue()) {
                serviceBuilder.addMethod(methodSuplier.getMethodDescriptor());
            }
            final ServiceDescriptor serviceDescriptor = serviceBuilder.build();
            final ServerServiceDefinition.Builder serverBuilder = ServerServiceDefinition.builder(serviceDescriptor);
            for (MethodSuplier methodSuplier : entry.getValue()) {
                final Method method = methodSuplier.getMethod();
                serverBuilder.addMethod(methodSuplier.getMethodDescriptor(), asyncUnaryCall(new MethodHandlers() {

                    @Override
                    public void invoke(Object req, StreamObserver streamObserver) {
                        try {
                            streamObserver.onNext(method.invoke(methodSuplier.getBean(), req));
                            streamObserver.onCompleted();
                        } catch (Exception e) {
                            streamObserver.onNext(e.getMessage());
                            streamObserver.onCompleted();

                            log.error(e.getMessage(), e);
                        }
                    }

                    @Override
                    public StreamObserver invoke(StreamObserver streamObserver) {
                        throw new AssertionError();
                    }
                }));
            }

            final ServerServiceDefinition serviceDefinition = bindInterceptors(serverBuilder.build(), globalServerInterceptorRegistry);
            serverServiceDefinitions.add(serviceDefinition);
        }

        return serverServiceDefinitions;
    }

    private static ServerServiceDefinition bindInterceptors(final ServerServiceDefinition serviceDefinition,
                                                     final GlobalServerInterceptorRegistry globalServerInterceptorRegistry) {
        final List<ServerInterceptor> interceptors = Lists.newArrayList();
        interceptors.addAll(globalServerInterceptorRegistry.getServerInterceptors());

        globalServerInterceptorRegistry.sortInterceptors(interceptors);

        return ServerInterceptors.interceptForward(serviceDefinition, interceptors);
    }
}
