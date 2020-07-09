package com.yx.grpc.common.codec;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 15:54
 * @Description: CodecType
 */
public enum CodecType {
    /**
     * The codec should be used for compression only.
     */
    COMPRESS(true, false),

    /**
     * The codec should be used for decompression only.
     */
    DECOMPRESS(false, true),

    /**
     * The codec should be used for both compression and decompression.
     */
    ALL(true, true);

    private final boolean forCompression;
    private final boolean forDecompression;

    private CodecType(final boolean forCompression, final boolean forDecompression) {
        this.forCompression = forCompression;
        this.forDecompression = forDecompression;
    }

    /**
     * Whether the associated codec should be used for compression.
     *
     * @return True, if the codec can be used for compression. False otherwise.
     */
    public boolean isForCompression() {
        return this.forCompression;
    }

    /**
     * Whether the associated codec should be used for decompression.
     *
     * @return True, if the codec can be used for decompression. False otherwise.
     */
    public boolean isForDecompression() {
        return this.forDecompression;
    }
}
