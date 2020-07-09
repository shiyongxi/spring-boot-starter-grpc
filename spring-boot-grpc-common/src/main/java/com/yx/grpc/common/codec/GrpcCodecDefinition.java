package com.yx.grpc.common.codec;

import com.google.common.collect.ImmutableList;
import io.grpc.Codec;
import lombok.Getter;

import java.util.Collection;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 15:54
 * @Description: GrpcCodecDefinition
 */
@Getter
public class GrpcCodecDefinition {
    /**
     * The codec definition for gzip.
     */
    public static final GrpcCodecDefinition GZIP_DEFINITION =
            new GrpcCodecDefinition(new Codec.Gzip(), true, CodecType.ALL);
    /**
     * The codec definition for identity (no-op).
     */
    public static final GrpcCodecDefinition IDENTITY_DEFINITION =
            new GrpcCodecDefinition(Codec.Identity.NONE, false, CodecType.ALL);
    /**
     * The default encodings used by gRPC.
     */
    public static final Collection<GrpcCodecDefinition> DEFAULT_DEFINITIONS =
            ImmutableList.<GrpcCodecDefinition>builder()
                    .add(GZIP_DEFINITION)
                    .add(IDENTITY_DEFINITION)
                    .build();

    private final Codec codec;
    private final boolean advertised;
    private final CodecType codecType;

    /**
     * Creates a new GrpcCodecDefinition.
     *
     * @param codec The codec bean.
     * @param advertised Whether the codec should be advertised in the headers.
     * @param codecType The type of the codec.
     */
    public GrpcCodecDefinition(final Codec codec, final boolean advertised, final CodecType codecType) {
        this.codec = codec;
        this.advertised = advertised;
        this.codecType = codecType;
    }
}
