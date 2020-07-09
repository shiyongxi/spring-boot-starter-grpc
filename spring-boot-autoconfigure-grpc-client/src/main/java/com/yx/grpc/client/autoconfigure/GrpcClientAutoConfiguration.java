package com.yx.grpc.client.autoconfigure;

import com.yx.grpc.client.channelfactory.*;
import com.yx.grpc.client.config.GrpcChannelsProperties;
import com.yx.grpc.client.inject.GrpcClientBeanPostProcessor;
import com.yx.grpc.client.interceptor.AnnotationGlobalClientInterceptorConfigurer;
import com.yx.grpc.client.interceptor.GlobalClientInterceptorRegistry;
import com.yx.grpc.client.nameresolver.ConfigMappedNameResolverFactory;
import com.yx.grpc.client.nameresolver.NameResolverRegistration;
import com.yx.grpc.common.autoconfigure.GrpcCommonCodecAutoConfiguration;
import io.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import java.util.Collections;
import java.util.List;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-16 17:17
 * @Description: GrpcClientAutoConfiguration
 */
@Configuration
@EnableConfigurationProperties
@AutoConfigureAfter(name = "org.springframework.cloud.client.CommonsClientAutoConfiguration",
        value = GrpcCommonCodecAutoConfiguration.class)
public class GrpcClientAutoConfiguration {

    @Bean
    public static GrpcClientBeanPostProcessor grpcClientBeanPostProcessor(final ApplicationContext applicationContext) {
        return new GrpcClientBeanPostProcessor(applicationContext);
    }

    @ConditionalOnMissingBean
    @Bean
    public GrpcChannelsProperties grpcChannelsProperties() {
        return new GrpcChannelsProperties();
    }

    @ConditionalOnMissingBean
    @Bean
    public GlobalClientInterceptorRegistry globalClientInterceptorRegistry() {
        return new GlobalClientInterceptorRegistry();
    }

    @Bean
    public AnnotationGlobalClientInterceptorConfigurer annotationGlobalClientInterceptorConfigurer() {
        return new AnnotationGlobalClientInterceptorConfigurer();
    }

    @ConditionalOnMissingBean
    @Lazy
    @Bean
    public NameResolverRegistration grpcNameResolverRegistration(
            @Autowired(required = false) List<NameResolverProvider> nameResolverProviders) {
        return new NameResolverRegistration(nameResolverProviders);
    }

    @ConditionalOnMissingBean
    @Lazy
    @Bean
    public NameResolverRegistry grpcNameResolverRegistry(NameResolverRegistration registration) {
        NameResolverRegistry registry = NameResolverRegistry.getDefaultRegistry();
        registration.register(registry);
        return registry;
    }

    @ConditionalOnMissingBean(name = "grpcNameResolverFactory")
    @Lazy
    @Bean
    @Primary
    public NameResolver.Factory grpcNameResolverFactory(final GrpcChannelsProperties channelProperties,
                                                        NameResolverRegistry registry) {
        return new ConfigMappedNameResolverFactory(channelProperties, registry);
    }

    @ConditionalOnBean(CompressorRegistry.class)
    @Bean
    public GrpcChannelConfigurer compressionChannelConfigurer(final CompressorRegistry registry) {
        return (builder, name) -> builder.compressorRegistry(registry);
    }

    @ConditionalOnBean(DecompressorRegistry.class)
    @Bean
    public GrpcChannelConfigurer decompressionChannelConfigurer(final DecompressorRegistry registry) {
        return (builder, name) -> builder.decompressorRegistry(registry);
    }

    @ConditionalOnMissingBean(GrpcChannelConfigurer.class)
    @Bean
    public List<GrpcChannelConfigurer> defaultChannelConfigurers() {
        return Collections.emptyList();
    }

    @ConditionalOnMissingBean(GrpcChannelFactory.class)
    @ConditionalOnClass(name = {"io.grpc.netty.shaded.io.netty.channel.Channel", "io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder"})
    @Bean
    public GrpcChannelFactory shadedNettyGrpcChannelFactory(final GrpcChannelsProperties properties,
                                                            final NameResolver.Factory nameResolverFactory,
                                                            final GlobalClientInterceptorRegistry globalClientInterceptorRegistry,
                                                            final List<GrpcChannelConfigurer> channelConfigurers) {
        final ShadedNettyChannelFactory channelFactory = new ShadedNettyChannelFactory(properties, nameResolverFactory, globalClientInterceptorRegistry, channelConfigurers);
        final InProcessChannelFactory inProcessChannelFactory = new InProcessChannelFactory(properties, globalClientInterceptorRegistry, channelConfigurers);
        return new InProcessOrAlternativeChannelFactory(properties, inProcessChannelFactory, channelFactory);
    }

    @ConditionalOnMissingBean(GrpcChannelFactory.class)
    @ConditionalOnClass(name = {"io.netty.channel.Channel", "io.grpc.netty.NettyChannelBuilder"})
    @Bean
    public GrpcChannelFactory nettyGrpcChannelFactory(final GrpcChannelsProperties properties,
                                                      final NameResolver.Factory nameResolverFactory,
                                                      final GlobalClientInterceptorRegistry globalClientInterceptorRegistry,
                                                      final List<GrpcChannelConfigurer> channelConfigurers) {
        final NettyChannelFactory channelFactory = new NettyChannelFactory(properties, nameResolverFactory, globalClientInterceptorRegistry, channelConfigurers);
        final InProcessChannelFactory inProcessChannelFactory = new InProcessChannelFactory(properties, globalClientInterceptorRegistry, channelConfigurers);
        return new InProcessOrAlternativeChannelFactory(properties, inProcessChannelFactory, channelFactory);
    }

    @ConditionalOnMissingBean(GrpcChannelFactory.class)
    @Bean
    public GrpcChannelFactory inProcessGrpcChannelFactory(final GrpcChannelsProperties properties,
                                                          final GlobalClientInterceptorRegistry globalClientInterceptorRegistry,
                                                          final List<GrpcChannelConfigurer> channelConfigurers) {
        return new InProcessChannelFactory(properties, globalClientInterceptorRegistry, channelConfigurers);
    }

}
