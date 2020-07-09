package com.yx.grpc.client.interceptor;

import io.grpc.*;
import org.springframework.core.Ordered;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 16:31
 * @Description: OrderedClientInterceptor
 */
public class OrderedClientInterceptor implements ClientInterceptor, Ordered {
    private final ClientInterceptor clientInterceptor;
    private final int order;

    /**
     * Creates a new OrderedClientInterceptor with the given client interceptor and order.
     *
     * @param clientInterceptor The client interceptor to delegate to.
     * @param order The order of this interceptor.
     */
    public OrderedClientInterceptor(ClientInterceptor clientInterceptor, int order) {
        this.clientInterceptor = requireNonNull(clientInterceptor, "clientInterceptor");
        this.order = order;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        return this.clientInterceptor.interceptCall(method, callOptions, next);
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public String toString() {
        return "OrderedClientInterceptor [interceptor=" + this.clientInterceptor + ", order=" + this.order + "]";
    }
}
