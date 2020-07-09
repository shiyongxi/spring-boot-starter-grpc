package com.yx.grpc.server.security.authentication;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.annotation.Nullable;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:24
 * @Description: GrpcAuthenticationReader
 */
@FunctionalInterface
public interface GrpcAuthenticationReader {

    /**
     * Tries to read the {@link Authentication} information from the given call and metadata.
     *
     * <p>
     * <b>Note:</b> Implementations are free to throw an {@link AuthenticationException} if no credentials could be
     * found in the call. If an exception is thrown by an implementation then the authentication attempt should be
     * considered as failed and no subsequent {@link GrpcAuthenticationReader}s should be called.
     * </p>
     *
     * @param call The call to get that send the request.
     * @param headers The metadata/headers as sent by the client.
     * @return The authentication object or null if no authentication is present.
     * @throws AuthenticationException If the authentication details are malformed or incomplete and thus the
     *         authentication attempt should be aborted.
     */
    @Nullable
    Authentication readAuthentication(ServerCall<?, ?> call, Metadata headers) throws AuthenticationException;

}
