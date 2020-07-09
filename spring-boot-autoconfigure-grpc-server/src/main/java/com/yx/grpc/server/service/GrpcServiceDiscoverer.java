package com.yx.grpc.server.service;

import java.util.Collection;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:34
 * @Description: GrpcServiceDiscoverer
 */
@FunctionalInterface
public interface GrpcServiceDiscoverer {
    /**
     * Find the grpc services that should provided by the server.
     *
     * @return The grpc services that should be provided. Never null.
     */
    Collection<GrpcServiceDefinition> findGrpcServices();
}
