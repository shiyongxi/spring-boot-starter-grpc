package com.yx.grpc.common.security;

import io.grpc.Metadata;

import java.nio.charset.StandardCharsets;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 18:07
 * @Description: SecurityConstants
 */
public class SecurityConstants {
    /**
     * A convenience constant that contains the key for the HTTP Authorization Header.
     */
    public static final Metadata.Key<String> AUTHORIZATION_HEADER = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    /**
     * The prefix for basic auth as used in the {@link #AUTHORIZATION_HEADER}. This library assumes that the both the
     * username and password are {@link StandardCharsets#UTF_8 UTF_8} encoded before being turned into a base64 string.
     */
    public static final String BASIC_AUTH_PREFIX = "Basic ";

    /**
     * The prefix for bearer auth as used in the {@link #AUTHORIZATION_HEADER}.
     */
    public static final String BEARER_AUTH_PREFIX = "Bearer ";

    private SecurityConstants() {}
}
