package com.yx.grpc.common.codec;

import com.google.common.collect.ImmutableList;
import io.grpc.Codec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 15:59
 * @Description: AnnotationGrpcCodecDiscoverer
 */
@Slf4j
public class AnnotationGrpcCodecDiscoverer implements ApplicationContextAware, GrpcCodecDiscoverer {
    private ApplicationContext applicationContext;
    private Collection<GrpcCodecDefinition> definitions;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Collection<GrpcCodecDefinition> findGrpcCodecs() {
        if (this.definitions == null) {
            log.debug("Searching for codecs...");
            final String[] beanNames = this.applicationContext.getBeanNamesForAnnotation(GrpcCodec.class);
            final ImmutableList.Builder<GrpcCodecDefinition> builder = ImmutableList.builder();
            for (final String beanName : beanNames) {
                final Codec codec = this.applicationContext.getBean(beanName, Codec.class);
                final GrpcCodec annotation = this.applicationContext.findAnnotationOnBean(beanName, GrpcCodec.class);
                builder.add(new GrpcCodecDefinition(codec, annotation.advertised(), annotation.codecType()));
                log.debug("Found gRPC codec: {}, bean: {}, class: {}",
                        codec.getMessageEncoding(), beanName, codec.getClass().getName());
            }
            this.definitions = builder.build();
            log.debug("Done");
        }
        return this.definitions;
    }
}
