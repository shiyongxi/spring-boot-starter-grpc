package com.yx.grpc.server.security.check;

import io.grpc.MethodDescriptor;
import org.springframework.security.access.ConfigAttribute;

import java.util.Collection;

/**
 * Abstract implementation of {@link GrpcSecurityMetadataSource} which resolves the secured object type to a
 * {@link MethodDescriptor}.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:35
 * @Description: GrpcSecurityMetadataSource
 * */
public abstract class AbstractGrpcSecurityMetadataSource implements GrpcSecurityMetadataSource {

    @Override
    public final Collection<ConfigAttribute> getAttributes(final Object object) throws IllegalArgumentException {
        if (object instanceof MethodDescriptor) {
            return getAttributes((MethodDescriptor<?, ?>) object);
        }
        throw new IllegalArgumentException("Object must be a non-null MethodDescriptor");
    }

    @Override
    public final boolean supports(final Class<?> clazz) {
        return MethodDescriptor.class.equals(clazz);
    }

}
