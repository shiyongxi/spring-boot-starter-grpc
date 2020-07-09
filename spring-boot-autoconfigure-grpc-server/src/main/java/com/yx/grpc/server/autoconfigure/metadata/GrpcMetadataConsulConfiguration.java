package com.yx.grpc.server.autoconfigure.metadata;

import com.ecwid.consul.v1.ConsulClient;
import com.yx.grpc.server.cloud.ConsulGrpcRegistrationCustomizer;
import com.yx.grpc.server.config.GrpcServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that configures the required beans for gRPC discovery via Consul.
 *
 * @author Michael (yidongnan@gmail.com)
 * @since 5/17/16
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass({ConsulDiscoveryProperties.class, ConsulClient.class, GrpcServerProperties.class})
public class GrpcMetadataConsulConfiguration {

    /**
     * Creates a bean that registers the gRPC endpoint via consul.
     *
     * @param grpcServerProperties The server properties used to fill in the blanks.
     * @return The newly created consulGrpcRegistrationCustomizer bean.
     */
    @ConditionalOnMissingBean
    @Bean
    public ConsulRegistrationCustomizer consulGrpcRegistrationCustomizer(
            final GrpcServerProperties grpcServerProperties) {
        return new ConsulGrpcRegistrationCustomizer(grpcServerProperties);
    }

}
