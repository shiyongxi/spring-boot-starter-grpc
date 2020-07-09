package com.yx.grpc.client.inject;

import io.grpc.stub.AbstractStub;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 18:06
 * @Description: StubTransformer
 */
@FunctionalInterface
public interface StubTransformer {

    /**
     * Transform the given stub using {@code AbstractStub#with...} methods.
     *
     * @param name The name that was used to create the stub.
     * @param stub The stub that should be transformed.
     * @return The transformed stub.
     */
    AbstractStub<?> transform(String name, AbstractStub<?> stub);

}
