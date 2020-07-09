package com.yx.grpc.server.cloud;

import com.yx.grpc.common.util.GrpcUtils;
import com.yx.grpc.server.config.GrpcServerProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:56
 * @Description: ConsulGrpcRegistrationCustomizer
 */
public class ConsulGrpcRegistrationCustomizer implements ConsulRegistrationCustomizer {

    private final GrpcServerProperties grpcServerProperties;

    /**
     * Creates a new ConsulGrpcRegistrationCustomizer which will read the grpc server port from the given
     * {@link GrpcServerProperties}.
     *
     * @param grpcServerProperties The properties to get the server port from.
     */
    public ConsulGrpcRegistrationCustomizer(final GrpcServerProperties grpcServerProperties) {
        this.grpcServerProperties = grpcServerProperties;
    }

    @Override
    public void customize(final ConsulRegistration registration) {
        List<String> tags = registration.getService().getTags();
        if (tags == null) {
            tags = new ArrayList<>();
        }
        final int port = this.grpcServerProperties.getPort();
        if (port != -1) {
            tags.add(GrpcUtils.CLOUD_DISCOVERY_METADATA_PORT + "=" + port);
            registration.getService().setTags(tags);
        }
    }

}
