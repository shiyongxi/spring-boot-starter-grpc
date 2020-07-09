package com.yx.grpc.client.channelfactory;

import com.google.common.collect.ImmutableMap;
import com.yx.grpc.client.config.GrpcChannelsProperties;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ConnectivityState;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 17:59
 * @Description: InProcessOrAlternativeChannelFactory
 */
public class InProcessOrAlternativeChannelFactory implements GrpcChannelFactory {

    private static final String IN_PROCESS_SCHEME = "in-process";

    private final GrpcChannelsProperties properties;
    private final InProcessChannelFactory inProcessChannelFactory;
    private final GrpcChannelFactory alternativeChannelFactory;

    /**
     * Creates a new InProcessOrAlternativeChannelFactory with the given properties and channel factories.
     *
     * @param properties The properties used to resolved the target scheme
     * @param inProcessChannelFactory The in process channel factory implementation to use.
     * @param alternativeChannelFactory The alternative channel factory implementation to use.
     */
    public InProcessOrAlternativeChannelFactory(final GrpcChannelsProperties properties,
                                                final InProcessChannelFactory inProcessChannelFactory, final GrpcChannelFactory alternativeChannelFactory) {
        this.properties = requireNonNull(properties, "properties");
        this.inProcessChannelFactory = requireNonNull(inProcessChannelFactory, "inProcessChannelFactory");
        this.alternativeChannelFactory = requireNonNull(alternativeChannelFactory, "alternativeChannelFactory");
    }

    @Override
    public Channel createChannel(final String name, final List<ClientInterceptor> interceptors,
                                 boolean sortInterceptors) {
        final URI address = this.properties.getChannel(name).getAddress();
        if (address != null && IN_PROCESS_SCHEME.equals(address.getScheme())) {
            return this.inProcessChannelFactory.createChannel(address.getSchemeSpecificPart(), interceptors,
                    sortInterceptors);
        }
        return this.alternativeChannelFactory.createChannel(name, interceptors, sortInterceptors);
    }

    @Override
    public Map<String, ConnectivityState> getConnectivityState() {
        return ImmutableMap.<String, ConnectivityState>builder()
                .putAll(inProcessChannelFactory.getConnectivityState())
                .putAll(alternativeChannelFactory.getConnectivityState())
                .build();
    }

    @Override
    public void close() {
        try {
            this.inProcessChannelFactory.close();
        } finally {
            this.alternativeChannelFactory.close();
        }
    }
}
