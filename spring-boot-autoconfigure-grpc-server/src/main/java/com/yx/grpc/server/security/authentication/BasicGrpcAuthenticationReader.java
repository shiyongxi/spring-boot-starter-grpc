package com.yx.grpc.server.security.authentication;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Base64;

import static com.yx.grpc.common.security.SecurityConstants.AUTHORIZATION_HEADER;
import static com.yx.grpc.common.security.SecurityConstants.BASIC_AUTH_PREFIX;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Reads {@link UsernamePasswordAuthenticationToken basic auth credentials} from the request.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:24
 * @Description: GrpcAuthenticationReader
 */
@Slf4j
public class BasicGrpcAuthenticationReader implements GrpcAuthenticationReader {

    private static final String PREFIX = BASIC_AUTH_PREFIX.toLowerCase();
    private static final int PREFIX_LENGTH = PREFIX.length();

    @Override
    public Authentication readAuthentication(final ServerCall<?, ?> call, final Metadata headers)
            throws AuthenticationException {
        final String header = headers.get(AUTHORIZATION_HEADER);
        if (header == null || !header.toLowerCase().startsWith(PREFIX)) {
            log.debug("No basic auth header found");
            return null;
        }
        final String[] decoded = extractAndDecodeHeader(header);
        return new UsernamePasswordAuthenticationToken(decoded[0], decoded[1]);
    }

    /**
     * Decodes the header into a username and password.
     *
     * @param header The authorization header.
     * @return The decoded username and password.
     * @throws BadCredentialsException If the Basic header is not valid Base64 or is missing the {@code ':'} separator.
     * @see <a href=
     *      "https://github.com/spring-projects/spring-security/blob/master/web/src/main/java/org/springframework/security/web/authentication/www/BasicAuthenticationFilter.java">BasicAuthenticationFilter</a>
     */
    private String[] extractAndDecodeHeader(final String header) {

        final byte[] base64Token = header.substring(PREFIX_LENGTH).getBytes(UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (final IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token", e);
        }

        final String token = new String(decoded, UTF_8);

        final int delim = token.indexOf(':');

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[] {token.substring(0, delim), token.substring(delim + 1)};
    }

}
