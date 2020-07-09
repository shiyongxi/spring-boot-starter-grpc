package com.yx.grpc.client.channelfactory;

import com.yx.grpc.client.config.GrpcChannelProperties;
import com.yx.grpc.client.config.GrpcChannelsProperties;
import com.yx.grpc.client.config.NegotiationType;
import com.yx.grpc.client.interceptor.GlobalClientInterceptorRegistry;
import io.grpc.NameResolver;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * This channel factory creates and manages shaded netty based {@link GrpcChannelFactory}s.
 *
 * <p>
 * This class utilizes connection pooling and thus needs to be {@link #close() closed} after usage.
 * </p>
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-17 15:40
 * @Description: ShadedNettyChannelFactory
 */
public class ShadedNettyChannelFactory extends AbstractChannelFactory<NettyChannelBuilder> {
    private final NameResolver.Factory nameResolverFactory;

    /**
     * Creates a new GrpcChannelFactory for shaded netty with the given options.
     *
     * @param properties The properties for the channels to create.
     * @param nameResolverFactory The name resolver factory to use.
     * @param globalClientInterceptorRegistry The interceptor registry to use.
     * @param channelConfigurers The channel configurers to use. Can be empty.
     */
    public ShadedNettyChannelFactory(final GrpcChannelsProperties properties,
                                     final NameResolver.Factory nameResolverFactory,
                                     final GlobalClientInterceptorRegistry globalClientInterceptorRegistry,
                                     final List<GrpcChannelConfigurer> channelConfigurers) {
        super(properties, globalClientInterceptorRegistry, channelConfigurers);
        this.nameResolverFactory = requireNonNull(nameResolverFactory, "nameResolverFactory");
    }

    @Override
    protected NettyChannelBuilder newChannelBuilder(final String name) {
        return NettyChannelBuilder.forTarget(name)
                .defaultLoadBalancingPolicy(getPropertiesFor(name).getDefaultLoadBalancingPolicy())
                .nameResolverFactory(this.nameResolverFactory);
    }

    @Override
    protected void configureSecurity(final NettyChannelBuilder builder, final String name) {
        final GrpcChannelProperties properties = getPropertiesFor(name);

        final NegotiationType negotiationType = properties.getNegotiationType();
        builder.negotiationType(of(negotiationType));

        if (negotiationType == NegotiationType.TLS) {
            final GrpcChannelProperties.Security security = properties.getSecurity();

            final String authorityOverwrite = security.getAuthorityOverride();
            if (authorityOverwrite != null && !authorityOverwrite.isEmpty()) {
                builder.overrideAuthority(authorityOverwrite);
            }

            final SslContextBuilder sslContextBuilder = GrpcSslContexts.forClient();

            if (security.isClientAuthEnabled()) {
                final Resource certificateChain =
                        requireNonNull(security.getCertificateChain(), "certificateChain not configured");
                final Resource privateKey = requireNonNull(security.getPrivateKey(), "privateKey not configured");
                try (InputStream certificateChainStream = certificateChain.getInputStream();
                     InputStream privateKeyStream = privateKey.getInputStream()) {
                    sslContextBuilder.keyManager(certificateChainStream, privateKeyStream,
                            security.getPrivateKeyPassword());
                } catch (IOException | RuntimeException e) {
                    throw new IllegalArgumentException("Failed to create SSLContext (PK/Cert)", e);
                }
            }

            final Resource trustCertCollection = security.getTrustCertCollection();
            if (trustCertCollection != null) {
                try (InputStream trustCertCollectionStream = trustCertCollection.getInputStream()) {
                    sslContextBuilder.trustManager(trustCertCollectionStream);
                } catch (IOException | RuntimeException e) {
                    throw new IllegalArgumentException("Failed to create SSLContext (TrustStore)", e);
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
                throw new IllegalStateException("Failed to create ssl context for grpc client", e);
            }
        }
    }

    /**
     * Converts the given negotiation type to netty's negotiation type.
     *
     * @param negotiationType The negotiation type to convert.
     * @return The converted negotiation type.
     */
    protected static io.grpc.netty.shaded.io.grpc.netty.NegotiationType of(final NegotiationType negotiationType) {
        switch (negotiationType) {
            case PLAINTEXT:
                return io.grpc.netty.shaded.io.grpc.netty.NegotiationType.PLAINTEXT;
            case PLAINTEXT_UPGRADE:
                return io.grpc.netty.shaded.io.grpc.netty.NegotiationType.PLAINTEXT_UPGRADE;
            case TLS:
                return io.grpc.netty.shaded.io.grpc.netty.NegotiationType.TLS;
            default:
                throw new IllegalArgumentException("Unsupported NegotiationType: " + negotiationType);
        }
    }
}
