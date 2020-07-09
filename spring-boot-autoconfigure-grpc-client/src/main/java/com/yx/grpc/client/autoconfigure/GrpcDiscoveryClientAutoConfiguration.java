package com.yx.grpc.client.autoconfigure;

import com.yx.grpc.client.nameresolver.DiscoveryClientResolverFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-16 17:53
 * @Description: GrpcDiscoveryClientAutoConfiguration
 */
@Configuration
@ConditionalOnBean(DiscoveryClient.class)
@AutoConfigureBefore(GrpcClientAutoConfiguration.class)
public class GrpcDiscoveryClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Lazy
    @Bean
    DiscoveryClientResolverFactory grpcDiscoveryClientResolverFactory(final DiscoveryClient client) {
        return new DiscoveryClientResolverFactory(client);
    }
}
