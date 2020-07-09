package com.yx.grpc.client.autoconfigure;

import com.google.common.collect.ImmutableMap;
import com.yx.grpc.client.channelfactory.GrpcChannelFactory;
import io.grpc.ConnectivityState;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 10:06
 * @Description: GrpcClientHealthAutoConfiguration
 */
@Configuration
@AutoConfigureAfter(GrpcClientAutoConfiguration.class)
@ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
public class GrpcClientHealthAutoConfiguration {
    /**
     * Creates a HealthIndicator based on the channels' {@link ConnectivityState}s from the underlying
     * {@link GrpcChannelFactory}.
     *
     * @param factory The factory to derive the connectivity states from.
     * @return A health indicator bean, that uses the following assumption
     *         <code>DOWN == states.contains(TRANSIENT_FAILURE)</code>.
     */
    @Bean
    @Lazy
    public HealthIndicator grpcChannelHealthIndicator(final GrpcChannelFactory factory) {
        return () -> {
            final ImmutableMap<String, ConnectivityState> states = ImmutableMap.copyOf(factory.getConnectivityState());
            final Health.Builder health;
            if (states.containsValue(ConnectivityState.TRANSIENT_FAILURE)) {
                health = Health.down();
            } else {
                health = Health.up();
            }
            return health.withDetails(states)
                    .build();
        };
    }
}
