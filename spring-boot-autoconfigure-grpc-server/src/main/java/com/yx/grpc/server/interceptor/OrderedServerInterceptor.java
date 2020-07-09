package com.yx.grpc.server.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.springframework.core.Ordered;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 14:41
 * @Description: OrderedServerInterceptor
 */
public class OrderedServerInterceptor implements ServerInterceptor, Ordered {
    private final ServerInterceptor serverInterceptor;
    private final int order;

    /**
     * Creates a new OrderedServerInterceptor with the given server interceptor and order.
     *
     * @param serverInterceptor The server interceptor to delegate to.
     * @param order The order of this interceptor.
     */
    public OrderedServerInterceptor(ServerInterceptor serverInterceptor, int order) {
        this.serverInterceptor = requireNonNull(serverInterceptor, "serverInterceptor");
        this.order = order;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        return this.serverInterceptor.interceptCall(call, headers, next);
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public String toString() {
        return "OrderedServerInterceptor [interceptor=" + this.serverInterceptor + ", order=" + this.order + "]";
    }
}
