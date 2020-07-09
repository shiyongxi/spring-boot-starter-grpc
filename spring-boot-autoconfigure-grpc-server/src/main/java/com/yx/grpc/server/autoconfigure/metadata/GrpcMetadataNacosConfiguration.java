package com.yx.grpc.server.autoconfigure.metadata;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.yx.grpc.common.util.GrpcUtils;
import com.yx.grpc.server.config.GrpcServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Configuration class that configures the required beans for grpc discovery via Nacos.
 *
 * @author Michael (yidongnan@gmail.com)
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass({NacosDiscoveryProperties.class, NacosNamingService.class})
public class GrpcMetadataNacosConfiguration {

    @Autowired(required = false)
    private NacosRegistration nacosRegistration;

    @Autowired
    private GrpcServerProperties grpcProperties;

    @PostConstruct
    public void init() {
        if (this.nacosRegistration == null) {
            return;
        }
        final int port = this.grpcProperties.getPort();
        if (port != -1) {
            this.nacosRegistration.getMetadata().put(GrpcUtils.CLOUD_DISCOVERY_METADATA_PORT, Integer.toString(port));
        }
    }

}
