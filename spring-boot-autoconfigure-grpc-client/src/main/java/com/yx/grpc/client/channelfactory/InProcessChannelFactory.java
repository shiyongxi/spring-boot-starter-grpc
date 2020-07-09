package com.yx.grpc.client.channelfactory;

import com.yx.grpc.client.config.GrpcChannelsProperties;
import com.yx.grpc.client.interceptor.GlobalClientInterceptorRegistry;
import io.grpc.inprocess.InProcessChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 17:53
 * @Description: InProcessChannelFactory
 */
@Slf4j
public class InProcessChannelFactory extends AbstractChannelFactory<InProcessChannelBuilder> {
    /**
     * Creates a new InProcessChannelFactory with the given properties.
     *
     * @param properties The properties for the channels to create.
     * @param globalClientInterceptorRegistry The interceptor registry to use.
     */
    public InProcessChannelFactory(final GrpcChannelsProperties properties,
                                   final GlobalClientInterceptorRegistry globalClientInterceptorRegistry) {
        this(properties, globalClientInterceptorRegistry, Collections.emptyList());
    }

    /**
     * Creates a new InProcessChannelFactory with the given properties.
     *
     * @param properties The properties for the channels to create.
     * @param globalClientInterceptorRegistry The interceptor registry to use.
     * @param channelConfigurers The channel configurers to use. Can be empty.
     */
    public InProcessChannelFactory(final GrpcChannelsProperties properties,
                                   final GlobalClientInterceptorRegistry globalClientInterceptorRegistry,
                                   final List<GrpcChannelConfigurer> channelConfigurers) {
        super(properties, globalClientInterceptorRegistry, channelConfigurers);
    }

    @Override
    protected InProcessChannelBuilder newChannelBuilder(final String name) {
        log.debug("Creating new channel: {}", name);
        return InProcessChannelBuilder.forName(name);
    }

    @Override
    protected void configureSecurity(final InProcessChannelBuilder builder, final String name) {
        // No need to configure security as we are in process only.
        // There is also no need to throw exceptions if transport security is configured.
    }
}
