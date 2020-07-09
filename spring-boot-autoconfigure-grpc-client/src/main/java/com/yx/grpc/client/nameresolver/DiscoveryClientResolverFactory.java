package com.yx.grpc.client.nameresolver;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.internal.GrpcUtil;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.event.EventListener;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-16 18:04
 * @Description: DiscoveryClientResolverFactory
 */
public class DiscoveryClientResolverFactory extends NameResolverProvider {
    /**
     * The constant containing the scheme that will be used by this factory.
     */
    public static final String DISCOVERY_SCHEME = "discovery";

    private final Set<DiscoveryClientNameResolver> discoveryClientNameResolvers = ConcurrentHashMap.newKeySet();
    private final HeartbeatMonitor monitor = new HeartbeatMonitor();

    private final DiscoveryClient client;

    /**
     * Creates a new discovery client based name resolver factory.
     *
     * @param client The client to use for the address discovery.
     */
    public DiscoveryClientResolverFactory(final DiscoveryClient client) {
        this.client = requireNonNull(client, "client");
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(final URI targetUri, final NameResolver.Args args) {
        if (DISCOVERY_SCHEME.equals(targetUri.getScheme())) {
            final String serviceName = targetUri.getPath();
            if (serviceName == null || serviceName.length() <= 1 || !serviceName.startsWith("/")) {
                throw new IllegalArgumentException("Incorrectly formatted target uri; "
                        + "expected: '" + DISCOVERY_SCHEME + ":[//]/<service-name>'; "
                        + "but was '" + targetUri.toString() + "'");
            }
            final AtomicReference<DiscoveryClientNameResolver> reference = new AtomicReference<>();
            final DiscoveryClientNameResolver discoveryClientNameResolver =
                    new DiscoveryClientNameResolver(serviceName.substring(1), this.client, args,
                            GrpcUtil.SHARED_CHANNEL_EXECUTOR,
                            () -> this.discoveryClientNameResolvers.remove(reference.get()));
            reference.set(discoveryClientNameResolver);
            this.discoveryClientNameResolvers.add(discoveryClientNameResolver);
            return discoveryClientNameResolver;
        }
        return null;
    }

    @Override
    public String getDefaultScheme() {
        return DISCOVERY_SCHEME;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 6; // More important than DNS
    }

    /**
     * Triggers a refresh of the registered name resolvers.
     *
     * @param event The event that triggered the update.
     */
    @EventListener(HeartbeatEvent.class)
    public void heartbeat(final HeartbeatEvent event) {
        if (this.monitor.update(event.getValue())) {
            for (final DiscoveryClientNameResolver discoveryClientNameResolver : this.discoveryClientNameResolvers) {
                discoveryClientNameResolver.refreshFromExternal();
            }
        }
    }

    /**
     * Cleans up the name resolvers.
     */
    @PreDestroy
    public void destroy() {
        this.discoveryClientNameResolvers.clear();
    }

    @Override
    public String toString() {
        return "DiscoveryClientResolverFactory [scheme=" + getDefaultScheme() +
                ", discoveryClient=" + this.client + "]";
    }
}
