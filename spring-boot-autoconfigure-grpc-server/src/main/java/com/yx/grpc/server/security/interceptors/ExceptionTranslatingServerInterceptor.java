package com.yx.grpc.server.security.interceptors;

import com.yx.grpc.common.util.InterceptorOrder;
import com.yx.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:22
 * @Description: ExceptionTranslatingServerInterceptor
 */
@Slf4j
@GrpcGlobalServerInterceptor
@Order(InterceptorOrder.ORDER_SECURITY_EXCEPTION_HANDLING)
public class ExceptionTranslatingServerInterceptor implements ServerInterceptor {

    /**
     * A constant that contains the response message for unauthenticated calls.
     */
    public static final String UNAUTHENTICATED_DESCRIPTION = "Authentication failed";
    /**
     * A constant that contains the response message for calls with insufficient permissions.
     */
    public static final String ACCESS_DENIED_DESCRIPTION = "Access denied";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call,
                                                                 final Metadata headers,
                                                                 final ServerCallHandler<ReqT, RespT> next) {
        try {
            // Streaming calls error out here
            return new ExceptionTranslatorServerCallListener<>(next.startCall(call, headers), call);
        } catch (final AuthenticationException aex) {
            closeCallUnauthenticated(call, aex);
            return noOpCallListener();
        } catch (final AccessDeniedException aex) {
            closeCallAccessDenied(call, aex);
            return noOpCallListener();
        }
    }

    /**
     * Creates a new no-op call listener because you can neither return null nor throw an exception in
     * {@link #interceptCall(ServerCall, Metadata, ServerCallHandler)}.
     *
     * @param <ReqT> The type of the request.
     * @return The newly created dummy listener.
     */
    protected <ReqT> ServerCall.Listener<ReqT> noOpCallListener() {
        return new ServerCall.Listener<ReqT>() {};
    }

    /**
     * Close the call with {@link Status#UNAUTHENTICATED}.
     *
     * @param call The call to close.
     * @param aex The exception that was the cause.
     */
    protected void closeCallUnauthenticated(final ServerCall<?, ?> call, final AuthenticationException aex) {
        log.debug(UNAUTHENTICATED_DESCRIPTION, aex);
        call.close(Status.UNAUTHENTICATED.withCause(aex).withDescription(UNAUTHENTICATED_DESCRIPTION), new Metadata());
    }

    /**
     * Close the call with {@link Status#PERMISSION_DENIED}.
     *
     * @param call The call to close.
     * @param aex The exception that was the cause.
     */
    protected void closeCallAccessDenied(final ServerCall<?, ?> call, final AccessDeniedException aex) {
        log.debug(ACCESS_DENIED_DESCRIPTION, aex);
        call.close(Status.PERMISSION_DENIED.withCause(aex).withDescription(ACCESS_DENIED_DESCRIPTION), new Metadata());
    }

    /**
     * Server call listener that catches and handles exceptions in {@link #onHalfClose()}.
     *
     * @param <ReqT> The type of the request.
     * @param <RespT> The type of the response.
     */
    private class ExceptionTranslatorServerCallListener<ReqT, RespT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

        private final ServerCall<ReqT, RespT> call;

        protected ExceptionTranslatorServerCallListener(final ServerCall.Listener<ReqT> delegate,
                                                        final ServerCall<ReqT, RespT> call) {
            super(delegate);
            this.call = call;
        }

        @Override
        // Unary calls error out here
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (final AuthenticationException aex) {
                closeCallUnauthenticated(this.call, aex);
            } catch (final AccessDeniedException aex) {
                closeCallAccessDenied(this.call, aex);
            }
        }

    }
}
