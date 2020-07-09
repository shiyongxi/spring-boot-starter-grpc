package com.yx.grpc.server.nameresolver;

import com.yx.grpc.server.config.GrpcServerProperties;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 14:56
 * @Description: SelfNameResolverFactory
 */
public class SelfNameResolverFactory extends NameResolverProvider {
    /**
     * The constant containing the scheme that will be used by this factory.
     */
    public static final String SELF_SCHEME = "self";

    private final GrpcServerProperties properties;

    /**
     * Creates a new SelfNameResolverFactory that uses the given properties.
     *
     * @param properties The properties used to resolve this server's address.
     */
    public SelfNameResolverFactory(GrpcServerProperties properties) {
        this.properties = properties;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (SELF_SCHEME.equals(targetUri.getScheme()) || targetUri.toString().equals(SELF_SCHEME)) {
            return new SelfNameResolver(this.properties, args);
        }
        return null;
    }

    @Override
    public String getDefaultScheme() {
        return SELF_SCHEME;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 0;
    }
}
