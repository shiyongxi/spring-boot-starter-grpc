package com.yx.grpc.common.autoconfigure;

import com.yx.grpc.common.codec.AnnotationGrpcCodecDiscoverer;
import com.yx.grpc.common.codec.GrpcCodecDefinition;
import com.yx.grpc.common.codec.GrpcCodecDiscoverer;
import io.grpc.Codec;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 15:50
 * @Description: GrpcCommonCodecAutoConfiguration
 */
@Slf4j
@Configuration
@ConditionalOnClass(Codec.class)
public class GrpcCommonCodecAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public GrpcCodecDiscoverer defaultGrpcCodecDiscoverer() {
        return new AnnotationGrpcCodecDiscoverer();
    }

    @ConditionalOnBean(GrpcCodecDiscoverer.class)
    @ConditionalOnMissingBean
    @Bean
    public CompressorRegistry defaultCompressorRegistry(final GrpcCodecDiscoverer codecDiscoverer) {
        log.debug("Found GrpcCodecDiscoverer -> Creating custom CompressorRegistry");
        final CompressorRegistry registry = CompressorRegistry.getDefaultInstance();
        for (final GrpcCodecDefinition definition : codecDiscoverer.findGrpcCodecs()) {
            if (definition.getCodecType().isForCompression()) {
                final Codec codec = definition.getCodec();
                log.debug("Registering compressor: '{}' ({})", codec.getMessageEncoding(), codec.getClass().getName());
                registry.register(codec);
            }
        }
        return registry;
    }

    @ConditionalOnBean(GrpcCodecDiscoverer.class)
    @ConditionalOnMissingBean
    @Bean
    public DecompressorRegistry defaultDecompressorRegistry(final GrpcCodecDiscoverer codecDiscoverer) {
        log.debug("Found GrpcCodecDiscoverer -> Creating custom DecompressorRegistry");
        DecompressorRegistry registry = DecompressorRegistry.getDefaultInstance();
        for (final GrpcCodecDefinition definition : codecDiscoverer.findGrpcCodecs()) {
            if (definition.getCodecType().isForCompression()) {
                final Codec codec = definition.getCodec();
                final boolean isAdvertised = definition.isAdvertised();
                log.debug("Registering {} decompressor: '{}' ({})",
                        isAdvertised ? "advertised" : "", codec.getMessageEncoding(), codec.getClass().getName());
                registry = registry.with(codec, isAdvertised);
            }
        }
        return registry;
    }
}
