package com.yx.grpc.server.autoconfigure;

import com.yx.grpc.common.autoconfigure.GrpcCommonCodecAutoConfiguration;
import com.yx.grpc.server.config.GrpcServerProperties;
import com.yx.grpc.server.interceptor.AnnotationGlobalServerInterceptorConfigurer;
import com.yx.grpc.server.interceptor.GlobalServerInterceptorRegistry;
import com.yx.grpc.server.nameresolver.SelfNameResolverFactory;
import com.yx.grpc.server.scope.GrpcRequestScope;
import com.yx.grpc.server.serverfactory.GrpcServerConfigurer;
import com.yx.grpc.server.serverfactory.GrpcServerFactory;
import com.yx.grpc.server.serverfactory.GrpcServerLifecycle;
import com.yx.grpc.server.service.DefaultGrpcServiceDiscoverer;
import com.yx.grpc.server.service.GrpcServiceDiscoverer;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.Server;
import io.grpc.services.HealthStatusManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Collections;
import java.util.List;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 11:36
 * @Description: GrpcServerAutoConfiguration
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass(Server.class)
@AutoConfigureAfter(GrpcCommonCodecAutoConfiguration.class)
public class GrpcServerAutoConfiguration {
    @Bean
    public static GrpcRequestScope grpcRequestScope() {
        return new GrpcRequestScope();
    }

    @ConditionalOnMissingBean
    @Bean
    public GrpcServerProperties defaultGrpcServerProperties() {
        return new GrpcServerProperties();
    }

    @ConditionalOnMissingBean
    @Bean
    public GlobalServerInterceptorRegistry globalServerInterceptorRegistry() {
        return new GlobalServerInterceptorRegistry();
    }

    @Bean
    public AnnotationGlobalServerInterceptorConfigurer annotationGlobalServerInterceptorConfigurer() {
        return new AnnotationGlobalServerInterceptorConfigurer();
    }

    @ConditionalOnMissingBean
    @Bean
    public HealthStatusManager healthStatusManager() {
        return new HealthStatusManager();
    }

    @ConditionalOnMissingBean
    @Bean
    @Lazy
    public SelfNameResolverFactory selfNameResolverFactory(GrpcServerProperties properties) {
        return new SelfNameResolverFactory(properties);
    }

    @ConditionalOnMissingBean
    @Bean
    public GrpcServiceDiscoverer defaultGrpcServiceDiscoverer() {
        return new DefaultGrpcServiceDiscoverer();
    }

    @ConditionalOnBean(CompressorRegistry.class)
    @Bean
    public GrpcServerConfigurer compressionServerConfigurer(final CompressorRegistry registry) {
        return builder -> builder.compressorRegistry(registry);
    }

    @ConditionalOnBean(DecompressorRegistry.class)
    @Bean
    public GrpcServerConfigurer decompressionServerConfigurer(final DecompressorRegistry registry) {
        return builder -> builder.decompressorRegistry(registry);
    }

    @ConditionalOnMissingBean(GrpcServerConfigurer.class)
    @Bean
    public List<GrpcServerConfigurer> defaultServerConfigurers() {
        return Collections.emptyList();
    }

    @ConditionalOnMissingBean
    @ConditionalOnBean(GrpcServerFactory.class)
    @Bean
    public GrpcServerLifecycle grpcServerLifecycle(final GrpcServerFactory factory) {
        return new GrpcServerLifecycle(factory);
    }
}
