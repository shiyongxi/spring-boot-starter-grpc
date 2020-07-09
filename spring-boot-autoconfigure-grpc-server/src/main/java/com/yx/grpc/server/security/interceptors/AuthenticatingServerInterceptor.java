package com.yx.grpc.server.security.interceptors;

import io.grpc.Context;
import io.grpc.ServerInterceptor;
import org.springframework.security.core.Authentication;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:30
 * @Description: AuthenticatingServerInterceptor
 */
public interface AuthenticatingServerInterceptor extends ServerInterceptor {

    /**
     * The context key that can be used to retrieve the associated {@link Authentication}.
     */
    Context.Key<Authentication> AUTHENTICATION_CONTEXT_KEY = Context.key("authentication");

}
