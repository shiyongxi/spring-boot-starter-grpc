package com.yx.grpc.server.security.authentication;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Combines multiple {@link GrpcAuthenticationReader} into a single one. The readers will be executed in the same order
 * the are passed to the constructor. The authentication is aborted if a grpc authentication reader throws an exception.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:24
 * @Description: GrpcAuthenticationReader
 */
public class CompositeGrpcAuthenticationReader implements GrpcAuthenticationReader {

    private final List<GrpcAuthenticationReader> authenticationReaders;

    /**
     * Creates a new CompositeGrpcAuthenticationReader with the given authentication readers.
     *
     * @param authenticationReaders The authentication readers to use.
     */
    public CompositeGrpcAuthenticationReader(final List<GrpcAuthenticationReader> authenticationReaders) {
        this.authenticationReaders = new ArrayList<>(requireNonNull(authenticationReaders, "authenticationReaders"));
    }

    @Override
    public Authentication readAuthentication(final ServerCall<?, ?> call, final Metadata headers)
            throws AuthenticationException {
        for (final GrpcAuthenticationReader authenticationReader : this.authenticationReaders) {
            final Authentication authentication = authenticationReader.readAuthentication(call, headers);
            if (authentication != null) {
                return authentication;
            }
        }
        return null;
    }

}
