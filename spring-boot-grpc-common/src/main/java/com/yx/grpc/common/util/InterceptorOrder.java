package com.yx.grpc.common.util;

import org.springframework.core.Ordered;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 11:53
 * @Description: InterceptorOrder
 */
public class InterceptorOrder {
    /**
     * The order value for interceptors that should be executed first. This is equivalent to
     * {@link Ordered#HIGHEST_PRECEDENCE}.
     */
    public static final int ORDER_FIRST = Ordered.HIGHEST_PRECEDENCE;
    /**
     * The order value for global exception handling interceptors.
     */
    public static final int ORDER_GLOBAL_EXCEPTION_HANDLING = 0;
    /**
     * The order value for tracing and metrics collecting interceptors.
     */
    public static final int ORDER_TRACING_METRICS = 2500;
    /**
     * The order value for interceptors related security exception handling.
     */
    public static final int ORDER_SECURITY_EXCEPTION_HANDLING = 5000;
    /**
     * The order value for security interceptors related to authentication.
     */
    public static final int ORDER_SECURITY_AUTHENTICATION = 5100;
    /**
     * The order value for security interceptors related to authorization checks.
     */
    public static final int ORDER_SECURITY_AUTHORISATION = 5200;
    /**
     * The order value for interceptors that should be executed last. This is equivalent to
     * {@link Ordered#LOWEST_PRECEDENCE}. This is the default for interceptors without specified priority.
     */
    public static final int ORDER_LAST = Ordered.LOWEST_PRECEDENCE;

    private InterceptorOrder() {}
}
