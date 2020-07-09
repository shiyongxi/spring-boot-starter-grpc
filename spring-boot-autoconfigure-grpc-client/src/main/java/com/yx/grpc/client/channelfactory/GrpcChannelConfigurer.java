package com.yx.grpc.client.channelfactory;

import io.grpc.ManagedChannelBuilder;

import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 10:27
 * @Description: GrpcChannelConfigurer
 */
public interface GrpcChannelConfigurer extends BiConsumer<ManagedChannelBuilder<?>, String> {
    @Override
    default GrpcChannelConfigurer andThen(final BiConsumer<? super ManagedChannelBuilder<?>, ? super String> after) {
        requireNonNull(after, "after");
        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
