package com.yx.grpc.server.service;

import io.grpc.ServerServiceDefinition;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:35
 * @Description: GrpcServiceDefinition
 */
public class GrpcServiceDefinition {
    private final ServerServiceDefinition definition;

    /**
     * Creates a new GrpcServiceDefinition.
     *
     * @param definition The grpc service definition.
     */
    public GrpcServiceDefinition(final ServerServiceDefinition definition) {
        this.definition = definition;
    }

    /**
     * Gets the grpc service definition.
     *
     * @return The grpc service definition.
     */
    public ServerServiceDefinition getDefinition() {
        return this.definition;
    }
}
