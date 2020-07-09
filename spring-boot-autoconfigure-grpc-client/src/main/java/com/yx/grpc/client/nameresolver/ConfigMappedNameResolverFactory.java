package com.yx.grpc.client.nameresolver;

import com.yx.grpc.client.config.GrpcChannelProperties;
import com.yx.grpc.client.config.GrpcChannelsProperties;
import io.grpc.NameResolver;
import io.grpc.NameResolverRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.net.URI;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 16:38
 * @Description: ConfigMappedNameResolverFactory
 */
@Slf4j
public class ConfigMappedNameResolverFactory extends NameResolver.Factory {
    private final GrpcChannelsProperties config;
    private final NameResolver.Factory delegate;

    /**
     * Creates a new ConfigMappedNameResolverFactory with the given config that resolves the remapped target uri using
     * the grpc's registered name resolvers.
     *
     * @param config The config used to remap the target uri.
     */
    public ConfigMappedNameResolverFactory(final GrpcChannelsProperties config) {
        this(config, NameResolverRegistry.getDefaultRegistry());
    }

    /**
     * Creates a new ConfigMappedNameResolverFactory with the given config that resolves the remapped target uri using
     * the grpc's registered name resolvers.
     *
     * @param config The config used to remap the target uri.
     * @param registry The registry to use as {@link NameResolver.Factory NameResolver.Factory} delegate.
     */
    public ConfigMappedNameResolverFactory(final GrpcChannelsProperties config, NameResolverRegistry registry) {
        this(config, registry.asFactory());
    }

    /**
     * Creates a new ConfigMappedNameResolverFactory with the given config that resolves the remapped target uri using
     * the given delegate.
     *
     * @param config The config used to remap the target uri.
     * @param delegate The delegate used to resolve the remapped target uri.
     */
    public ConfigMappedNameResolverFactory(final GrpcChannelsProperties config, final NameResolver.Factory delegate) {
        this.config = requireNonNull(config, "config");
        this.delegate = requireNonNull(delegate, "delegate");
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(final URI targetUri, final NameResolver.Args args) {
        final String clientName = targetUri.toString();
        final GrpcChannelProperties clientConfig = this.config.getChannel(clientName);
        URI remappedUri = clientConfig.getAddress();
        if (remappedUri == null) {
            remappedUri = URI.create(clientName);
        }
        log.debug("Remapping target URI for {} to {} via {}", clientName, remappedUri, this.delegate);
        NameResolver resolver = this.delegate.newNameResolver(remappedUri, args);
        if (resolver != null) {
            return resolver;
        }
        remappedUri = URI.create(getDefaultSchemeInternal() + ":/" + remappedUri.toString());
        log.debug("Remapping target URI (with default scheme) for {} to {} via {}",
                clientName, remappedUri, this.delegate);
        return this.delegate.newNameResolver(remappedUri, args);
    }

    @Override
    public String getDefaultScheme() {
        // The config does not use schemes at all
        return "";
    }

    private String getDefaultSchemeInternal() {
        String configured = this.config.getDefaultScheme();
        if (configured != null) {
            return configured;
        }
        return this.delegate.getDefaultScheme();
    }

    @Override
    public String toString() {
        return "ConfigMappedNameResolverFactory [config=" + this.config + ", delegate=" + this.delegate + "]";
    }
}
