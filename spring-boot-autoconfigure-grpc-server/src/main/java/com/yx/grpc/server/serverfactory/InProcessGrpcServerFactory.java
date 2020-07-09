package com.yx.grpc.server.serverfactory;

import com.yx.grpc.server.config.GrpcServerProperties;
import io.grpc.inprocess.InProcessServerBuilder;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:06
 * @Description: InProcessGrpcServerFactory
 */
public class InProcessGrpcServerFactory extends AbstractGrpcServerFactory<InProcessServerBuilder> {
    private final String name;

    /**
     * Creates a new in process server factory with the given properties.
     *
     * @param properties The properties used to configure the server.
     */
    public InProcessGrpcServerFactory(final GrpcServerProperties properties) {
        this(properties.getInProcessName(), properties);
    }

    /**
     * Creates a new in process server factory with the given properties.
     *
     * @param properties The properties used to configure the server.
     * @param serverConfigurers The server configurers to use. Can be empty.
     */
    public InProcessGrpcServerFactory(final GrpcServerProperties properties,
                                      final List<GrpcServerConfigurer> serverConfigurers) {
        this(properties.getInProcessName(), properties, serverConfigurers);
    }

    /**
     * Creates a new in process server factory with the given properties.
     *
     * @param name The name of the in process server.
     * @param properties The properties used to configure the server.
     */
    public InProcessGrpcServerFactory(final String name, final GrpcServerProperties properties) {
        this(name, properties, Collections.emptyList());
    }

    /**
     * Creates a new in process server factory with the given properties.
     *
     * @param name The name of the in process server.
     * @param properties The properties used to configure the server.
     * @param serverConfigurers The server configurers to use. Can be empty.
     */
    public InProcessGrpcServerFactory(final String name, final GrpcServerProperties properties,
                                      final List<GrpcServerConfigurer> serverConfigurers) {
        super(properties, serverConfigurers);
        this.name = requireNonNull(name, "name");
    }

    @Override
    protected InProcessServerBuilder newServerBuilder() {
        return InProcessServerBuilder.forName(this.name);
    }

    @Override
    public String getAddress() {
        return "in-process:" + this.name;
    }

    @Override
    public int getPort() {
        return -1;
    }
}
