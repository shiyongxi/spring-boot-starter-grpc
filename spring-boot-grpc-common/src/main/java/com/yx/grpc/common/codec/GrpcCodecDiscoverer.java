package com.yx.grpc.common.codec;

import java.util.Collection;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 15:53
 * @Description: GrpcCodecDiscoverer
 */
@FunctionalInterface
public interface GrpcCodecDiscoverer {

    /**
     * Find the grpc codecs that should uses by the client/server.
     *
     * @return The grpc codecs that should be provided. Never null.
     */
    Collection<GrpcCodecDefinition> findGrpcCodecs();

}