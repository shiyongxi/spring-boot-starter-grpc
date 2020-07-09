package com.yx.grpc.client.config;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 10:20
 * @Description: NegotiationType
 */
public enum NegotiationType {
    /**
     * Uses TLS ALPN/NPN negotiation, assumes an SSL connection.
     */
    TLS,

    /**
     * Use the HTTP UPGRADE protocol for a plaintext (non-SSL) upgrade from HTTP/1.1 to HTTP/2.
     */
    PLAINTEXT_UPGRADE,

    /**
     * Just assume the connection is plaintext (non-SSL) and the remote endpoint supports HTTP/2 directly without an
     * upgrade.
     */
    PLAINTEXT;
}
