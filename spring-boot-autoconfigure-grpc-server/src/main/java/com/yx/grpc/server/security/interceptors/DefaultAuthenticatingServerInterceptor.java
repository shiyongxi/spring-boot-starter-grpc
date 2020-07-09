package com.yx.grpc.server.security.interceptors;

import com.yx.grpc.common.util.InterceptorOrder;
import com.yx.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import com.yx.grpc.server.security.authentication.GrpcAuthenticationReader;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:33
 * @Description: DefaultAuthenticatingServerInterceptor
 */
@Slf4j
@GrpcGlobalServerInterceptor
@Order(InterceptorOrder.ORDER_SECURITY_AUTHENTICATION)
public class DefaultAuthenticatingServerInterceptor implements AuthenticatingServerInterceptor {

    private final AuthenticationManager authenticationManager;
    private final GrpcAuthenticationReader grpcAuthenticationReader;

    /**
     * Creates a new DefaultAuthenticatingServerInterceptor with the given authentication manager and reader.
     *
     * @param authenticationManager The authentication manager used to verify the credentials.
     * @param authenticationReader The authentication reader used to extract the credentials from the call.
     */
    @Autowired
    public DefaultAuthenticatingServerInterceptor(final AuthenticationManager authenticationManager,
                                                  final GrpcAuthenticationReader authenticationReader) {
        this.authenticationManager = requireNonNull(authenticationManager, "authenticationManager");
        this.grpcAuthenticationReader = requireNonNull(authenticationReader, "authenticationReader");
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call,
                                                                 final Metadata headers, final ServerCallHandler<ReqT, RespT> next) {
        Authentication authentication;
        try {
            authentication = this.grpcAuthenticationReader.readAuthentication(call, headers);
        } catch (final AuthenticationException e) {
            log.debug("Failed to read authentication: {}", e.getMessage());
            throw e;
        }
        if (authentication == null) {
            log.debug("No credentials found: Continuing unauthenticated");
            try {
                return next.startCall(call, headers);
            } catch (final AccessDeniedException e) {
                throw new BadCredentialsException("No credentials found in the request", e);
            }
        }
        if (authentication.getDetails() == null && authentication instanceof AbstractAuthenticationToken) {
            // Append call attributes to the authentication request.
            // This gives the AuthenticationManager access to information like remote and local address.
            // It can then decide whether it wants to use its own user details or the attributes.
            ((AbstractAuthenticationToken) authentication).setDetails(call.getAttributes());
        }
        log.debug("Credentials found: Authenticating '{}'", authentication.getName());
        try {
            authentication = this.authenticationManager.authenticate(authentication);
        } catch (final AuthenticationException e) {
            log.debug("Authentication request failed: {}", e.getMessage());
            throw e;
        }

        final Context context = Context.current().withValue(AUTHENTICATION_CONTEXT_KEY, authentication);
        final Context previousContext = context.attach();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Authentication successful: Continuing as {} ({})", authentication.getName(),
                authentication.getAuthorities());
        try {
            return new AuthenticatingServerCallListener<>(next.startCall(call, headers), context, authentication);
        } catch (final AccessDeniedException e) {
            if (authentication instanceof AnonymousAuthenticationToken) {
                throw new BadCredentialsException("No credentials found in the request", e);
            } else {
                throw e;
            }
        } finally {
            SecurityContextHolder.clearContext();
            context.detach(previousContext);
            log.debug("startCall - Authentication cleared");
        }
    }

    /**
     * A call listener that will set the authentication context using {@link SecurityContextHolder} before each
     * invocation and clear it afterwards.
     *
     * @param <ReqT> The type of the request.
     */
    private static class AuthenticatingServerCallListener<ReqT> extends AbstractAuthenticatingServerCallListener<ReqT> {

        private final Authentication authentication;

        /**
         * Creates a new AuthenticatingServerCallListener which will attach the given security context before delegating
         * to the given listener.
         *
         * @param delegate The listener to delegate to.
         * @param context The context to attach.
         * @param authentication The authentication instance to attach.
         */
        public AuthenticatingServerCallListener(final ServerCall.Listener<ReqT> delegate, final Context context,
                                                final Authentication authentication) {
            super(delegate, context);
            this.authentication = authentication;
        }

        @Override
        protected void attachAuthenticationContext() {
            SecurityContextHolder.getContext().setAuthentication(this.authentication);
        }

        @Override
        protected void detachAuthenticationContext() {
            SecurityContextHolder.clearContext();
        }

        @Override
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (final AccessDeniedException e) {
                if (this.authentication instanceof AnonymousAuthenticationToken) {
                    throw new BadCredentialsException("No credentials found in the request", e);
                } else {
                    throw e;
                }
            }
        }

    }

}
