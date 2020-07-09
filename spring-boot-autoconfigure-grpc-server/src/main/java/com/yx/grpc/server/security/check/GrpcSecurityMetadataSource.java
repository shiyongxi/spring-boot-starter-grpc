package com.yx.grpc.server.security.check;

import io.grpc.MethodDescriptor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;

import java.util.Collection;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:35
 * @Description: GrpcSecurityMetadataSource
 */
public interface GrpcSecurityMetadataSource extends SecurityMetadataSource {

    /**
     * Accesses the {@code ConfigAttribute}s that apply to a given secure object.
     *
     * @param method The grpc method being secured.
     * @return The attributes that apply to the passed in secured object. Should return an empty collection if there are
     *         no applicable attributes.
     */
    Collection<ConfigAttribute> getAttributes(final MethodDescriptor<?, ?> method);

}
