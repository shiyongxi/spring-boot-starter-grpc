package com.yx.grpc.server.serverfactory;

import io.grpc.ServerBuilder;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:47
 * @Description: GrpcServerConfigurer
 */
@FunctionalInterface
public interface GrpcServerConfigurer extends Consumer<ServerBuilder<?>> {

    @Override
    default GrpcServerConfigurer andThen(final Consumer<? super ServerBuilder<?>> after) {
        Objects.requireNonNull(after);
        return t -> {
            accept(t);
            after.accept(t);
        };
    }

}
