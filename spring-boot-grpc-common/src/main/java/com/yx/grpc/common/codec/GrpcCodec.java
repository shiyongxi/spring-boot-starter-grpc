package com.yx.grpc.common.codec;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface GrpcCodec {

    /**
     * Advertised codecs will be listed in the {@code Accept-Encoding} header. Defaults to false.
     *
     * @return True, of the codec should be advertised. False otherwise.
     */
    boolean advertised() default false;

    /**
     * Gets the type of codec the annotated bean should be used for.
     *
     * @return The type of codec.
     */
    CodecType codecType() default CodecType.ALL;

}
