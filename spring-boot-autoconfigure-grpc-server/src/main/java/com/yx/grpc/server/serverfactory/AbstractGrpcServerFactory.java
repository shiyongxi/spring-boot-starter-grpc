package com.yx.grpc.server.serverfactory;

import com.google.common.collect.Lists;
import com.yx.grpc.server.config.GrpcServerProperties;
import com.yx.grpc.server.service.GrpcServiceDefinition;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.unit.DataSize;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:51
 * @Description: AbstractGrpcServerFactory
 */
@Slf4j
public abstract class AbstractGrpcServerFactory<T extends ServerBuilder<T>> implements GrpcServerFactory {
    private final List<GrpcServiceDefinition> serviceList = Lists.newLinkedList();

    protected final GrpcServerProperties properties;
    protected final List<GrpcServerConfigurer> serverConfigurers;

    @Autowired
    private HealthStatusManager healthStatusManager;

    /**
     * Creates a new server factory with the given properties.
     *
     * @param properties The properties used to configure the server.
     * @param serverConfigurers The server configurers to use. Can be empty.
     */
    public AbstractGrpcServerFactory(final GrpcServerProperties properties,
                                     final List<GrpcServerConfigurer> serverConfigurers) {
        this.properties = requireNonNull(properties, "properties");
        this.serverConfigurers = requireNonNull(serverConfigurers, "serverConfigurers");
    }

    @Override
    public Server createServer() {
        final T builder = newServerBuilder();
        configure(builder);
        return builder.build();
    }

    /**
     * Creates a new server builder.
     *
     * @return The newly created server builder.
     */
    protected abstract T newServerBuilder();

    /**
     * Configures the given server builder. This method can be overwritten to add features that are not yet supported by
     * this library or use a {@link GrpcServerConfigurer} instead.
     *
     * @param builder The server builder to configure.
     */
    protected void configure(final T builder) {
        configureServices(builder);
        configureKeepAlive(builder);
        configureSecurity(builder);
        configureLimits(builder);
        for (final GrpcServerConfigurer serverConfigurer : this.serverConfigurers) {
            serverConfigurer.accept(builder);
        }
    }

    /**
     * Configures the services that should be served by the server.
     *
     * @param builder The server builder to configure.
     */
    protected void configureServices(final T builder) {
        // support health check
        if (this.properties.isHealthServiceEnabled()) {
            builder.addService(this.healthStatusManager.getHealthService());
        }
        if (this.properties.isReflectionServiceEnabled()) {
            builder.addService(ProtoReflectionService.newInstance());
        }

        for (final GrpcServiceDefinition service : this.serviceList) {
            final String serviceName = service.getDefinition().getServiceDescriptor().getName();
            log.info("Registered gRPC service: " + serviceName);
            builder.addService(service.getDefinition());
            this.healthStatusManager.setStatus(serviceName, HealthCheckResponse.ServingStatus.SERVING);
        }
    }

    /**
     * Configures the keep alive options that should be used by the server.
     *
     * @param builder The server builder to configure.
     */
    protected void configureKeepAlive(final T builder) {
        if (this.properties.isEnableKeepAlive()) {
            throw new IllegalStateException("KeepAlive is enabled but this implementation does not support keepAlive!");
        }
    }

    /**
     * Configures the security options that should be used by the server.
     *
     * @param builder The server builder to configure.
     */
    protected void configureSecurity(final T builder) {
        if (this.properties.getSecurity().isEnabled()) {
            throw new IllegalStateException("Security is enabled but this implementation does not support security!");
        }
    }

    /**
     * Configures limits such as max message sizes that should be used by the server.
     *
     * @param builder The server builder to configure.
     */
    protected void configureLimits(final T builder) {
        final DataSize maxInboundMessageSize = this.properties.getMaxInboundMessageSize();
        if (maxInboundMessageSize != null) {
            builder.maxInboundMessageSize((int) maxInboundMessageSize.toBytes());
        }
    }

    @Override
    public String getAddress() {
        return this.properties.getAddress();
    }

    @Override
    public int getPort() {
        return this.properties.getPort();
    }

    @Override
    public void addService(final GrpcServiceDefinition service) {
        this.serviceList.add(service);
    }

    @Override
    public void destroy() {
        for (final GrpcServiceDefinition grpcServiceDefinition : this.serviceList) {
            final String serviceName = grpcServiceDefinition.getDefinition().getServiceDescriptor().getName();
            this.healthStatusManager.clearStatus(serviceName);
        }
    }
}
