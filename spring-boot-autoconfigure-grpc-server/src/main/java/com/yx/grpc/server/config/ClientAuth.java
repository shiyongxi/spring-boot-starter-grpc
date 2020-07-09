package com.yx.grpc.server.config;

import javax.net.ssl.SSLEngine;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 11:45
 * @Description: ClientAuth
 */
public enum  ClientAuth {
    /**
     * Indicates that the {@link SSLEngine} will not request client authentication.
     */
    NONE,

    /**
     * Indicates that the {@link SSLEngine} will request client authentication.
     */
    OPTIONAL,

    /**
     * Indicates that the {@link SSLEngine} will <b>require</b> client authentication.
     */
    REQUIRE;
}
