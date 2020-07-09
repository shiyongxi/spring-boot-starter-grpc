package com.yx.grpc.server.security.interceptors;

import com.yx.grpc.common.util.InterceptorOrder;
import com.yx.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import com.yx.grpc.server.security.check.GrpcSecurityMetadataSource;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.AuthenticationException;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:34
 * @Description: AuthorizationCheckingServerInterceptor
 */
@Slf4j
@GrpcGlobalServerInterceptor
@Order(InterceptorOrder.ORDER_SECURITY_AUTHORISATION)
public class AuthorizationCheckingServerInterceptor extends AbstractSecurityInterceptor implements ServerInterceptor {

    private final GrpcSecurityMetadataSource securityMetadataSource;

    /**
     * Creates a new AuthorizationCheckingServerInterceptor with the given {@link AccessDecisionManager} and
     * {@link GrpcSecurityMetadataSource}.
     *
     * @param accessDecisionManager The access decision manager to use.
     * @param securityMetadataSource The security metadata source to use.
     */
    public AuthorizationCheckingServerInterceptor(final AccessDecisionManager accessDecisionManager,
                                                  final GrpcSecurityMetadataSource securityMetadataSource) {
        setAccessDecisionManager(requireNonNull(accessDecisionManager, "accessDecisionManager"));
        this.securityMetadataSource = requireNonNull(securityMetadataSource, "securityMetadataSource");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, final Metadata headers,
                                                                 final ServerCallHandler<ReqT, RespT> next) {
        final MethodDescriptor<ReqT, RespT> methodDescriptor = call.getMethodDescriptor();
        final InterceptorStatusToken token;
        try {
            token = beforeInvocation(methodDescriptor);
        } catch (final AuthenticationException | AccessDeniedException e) {
            log.debug("Access denied");
            throw e;
        }
        log.debug("Access granted");
        final ServerCall.Listener<ReqT> result;
        try {
            result = next.startCall(call, headers);
        } finally {
            finallyInvocation(token);
        }
        // TODO: Call that here or in onHalfClose?
        return (ServerCall.Listener<ReqT>) afterInvocation(token, result);
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return MethodDescriptor.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

}
