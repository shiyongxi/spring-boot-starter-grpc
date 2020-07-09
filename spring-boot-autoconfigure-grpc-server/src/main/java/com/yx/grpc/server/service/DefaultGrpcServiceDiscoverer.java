package com.yx.grpc.server.service;

import com.google.common.collect.Lists;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.yx.grpc.server.interceptor.GlobalServerInterceptorRegistry;
import com.yx.grpc.server.util.ReflectionUtils;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.protobuf.ProtoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:36
 * @Description: DefaultGrpcServiceDiscoverer
 */
@Slf4j
public class DefaultGrpcServiceDiscoverer implements ApplicationContextAware, GrpcServiceDiscoverer {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Collection<GrpcServiceDefinition> findGrpcServices() {
        Collection<String> beanNames = Arrays.asList(this.applicationContext.getBeanNamesForAnnotation(GrpcService.class));

        List<GrpcServiceDefinition> definitions = Lists.newArrayListWithCapacity(beanNames.size());
        GlobalServerInterceptorRegistry globalServerInterceptorRegistry = applicationContext.getBean(GlobalServerInterceptorRegistry.class);

        for (String beanName : beanNames) {
            Object bean = this.applicationContext.getBean(beanName);
            GrpcService grpcServiceAnnotation = applicationContext.findAnnotationOnBean(beanName, GrpcService.class);
            final String serviceName = grpcServiceAnnotation.serviceName();
            final Class<?> protoClass = grpcServiceAnnotation.protoClass();
            final Descriptors.FileDescriptor fileDescriptor = ReflectionUtils.invokeFileDescripto(protoClass);

            final Method[] methods = bean.getClass().getMethods();
            if (methods == null || methods.length == 0) {
                continue;
            }

            for (Method method: methods) {
                final GrpcMethod grpcMethodAnnotation = AnnotationUtils.findAnnotation(method, GrpcMethod.class);
                if (grpcMethodAnnotation != null) {
                    final String methodName = grpcMethodAnnotation.methodName();
                    final String fullMethodName = generateFullMethodName(serviceName, methodName);
                    final MethodDescriptor<Message, Message> methodDescriptor = MethodDescriptor.<Message, Message>newBuilder()
                            .setType(MethodDescriptor.MethodType.UNARY)
                            .setFullMethodName(fullMethodName)
                            .setSampledToLocalTracing(true)
                            .setRequestMarshaller(ProtoUtils.marshaller(
                                    (ReflectionUtils.invokeDefaultInstance(method.getParameterTypes()[0]))
                            ))
                            .setResponseMarshaller(ProtoUtils.marshaller(
                                    (ReflectionUtils.invokeDefaultInstance(method.getReturnType()))
                            ))
                            .setSchemaDescriptor(new MethodDescriptorSupplier(serviceName, fileDescriptor, methodName))
                            .build();

                    ServerServiceDefinitionSupplier.addMethodSuplier(serviceName, new MethodSuplier(bean, method, methodDescriptor), fileDescriptor);
                }

            }

        }

        final List<ServerServiceDefinition> serverServiceDefinitions = ServerServiceDefinitionSupplier.getServerServiceDefinition(globalServerInterceptorRegistry);
        for (ServerServiceDefinition serviceDefinition: serverServiceDefinitions) {
            definitions.add(new GrpcServiceDefinition(serviceDefinition));
        }

        return definitions;
    }
}
