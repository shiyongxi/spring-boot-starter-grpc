package com.yx.grpc.server.serverfactory;

import com.google.common.net.InetAddresses;
import com.yx.grpc.server.config.ClientAuth;
import com.yx.grpc.server.config.GrpcServerProperties;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:00
 * @Description: NettyGrpcServerFactory
 */
public class NettyGrpcServerFactory extends AbstractGrpcServerFactory<NettyServerBuilder> {
    public NettyGrpcServerFactory(final GrpcServerProperties properties,
                                  final List<GrpcServerConfigurer> serverConfigurers) {
        super(properties, serverConfigurers);
    }

    @Override
    protected NettyServerBuilder newServerBuilder() {
        final String address = getAddress();
        final int port = getPort();
        if (GrpcServerProperties.ANY_IP_ADDRESS.equals(address)) {
            return NettyServerBuilder.forPort(port);
        } else {
            return NettyServerBuilder.forAddress(new InetSocketAddress(InetAddresses.forString(address), port));
        }
    }

    @Override
    protected void configureKeepAlive(final NettyServerBuilder builder) {
        if (this.properties.isEnableKeepAlive()) {
            builder.keepAliveTime(this.properties.getKeepAliveTime().toNanos(), TimeUnit.NANOSECONDS)
                    .keepAliveTimeout(this.properties.getKeepAliveTimeout().toNanos(), TimeUnit.NANOSECONDS);
        }
        builder.permitKeepAliveTime(this.properties.getPermitKeepAliveTime().toNanos(), TimeUnit.NANOSECONDS)
                .permitKeepAliveWithoutCalls(this.properties.isPermitKeepAliveWithoutCalls());
    }

    @Override
    // Keep this in sync with ShadedNettyGrpcServerFactory#configureSecurity
    protected void configureSecurity(final NettyServerBuilder builder) {
        final GrpcServerProperties.Security security = this.properties.getSecurity();
        if (security.isEnabled()) {
            final Resource certificateChain =
                    requireNonNull(security.getCertificateChain(), "certificateChain not configured");
            final Resource privateKey = requireNonNull(security.getPrivateKey(), "privateKey not configured");
            SslContextBuilder sslContextBuilder;
            try (InputStream certificateChainStream = certificateChain.getInputStream();
                 InputStream privateKeyStream = privateKey.getInputStream()) {
                sslContextBuilder = GrpcSslContexts.forServer(certificateChainStream, privateKeyStream,
                        security.getPrivateKeyPassword());
            } catch (IOException | RuntimeException e) {
                throw new IllegalArgumentException("Failed to create SSLContext (PK/Cert)", e);
            }

            if (security.getClientAuth() != ClientAuth.NONE) {
                sslContextBuilder.clientAuth(of(security.getClientAuth()));

                final Resource trustCertCollection = security.getTrustCertCollection();
                if (trustCertCollection != null) {
                    try (InputStream trustCertCollectionStream = trustCertCollection.getInputStream()) {
                        sslContextBuilder.trustManager(trustCertCollectionStream);
                    } catch (IOException | RuntimeException e) {
                        throw new IllegalArgumentException("Failed to create SSLContext (TrustStore)", e);
                    }
                }
            }

            if (security.getCiphers() != null && !security.getCiphers().isEmpty()) {
                sslContextBuilder.ciphers(security.getCiphers());
            }

            if (security.getProtocols() != null && security.getProtocols().length > 0) {
                sslContextBuilder.protocols(security.getProtocols());
            }

            try {
                builder.sslContext(sslContextBuilder.build());
            } catch (final SSLException e) {
                throw new IllegalStateException("Failed to create ssl context for grpc server", e);
            }
        }
    }

    /**
     * Converts the given client auth option to netty's client auth.
     *
     * @param clientAuth The client auth option to convert.
     * @return The converted client auth option.
     */
    protected static io.netty.handler.ssl.ClientAuth of(final ClientAuth clientAuth) {
        switch (clientAuth) {
            case NONE:
                return io.netty.handler.ssl.ClientAuth.NONE;
            case OPTIONAL:
                return io.netty.handler.ssl.ClientAuth.OPTIONAL;
            case REQUIRE:
                return io.netty.handler.ssl.ClientAuth.REQUIRE;
            default:
                throw new IllegalArgumentException("Unsupported ClientAuth: " + clientAuth);
        }
    }
}
