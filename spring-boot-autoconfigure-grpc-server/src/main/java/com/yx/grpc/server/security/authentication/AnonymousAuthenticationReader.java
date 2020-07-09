package com.yx.grpc.server.security.authentication;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * The AnonymousAuthenticationReader allows users without credentials to get an anonymous identity.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:24
 * @Description: GrpcAuthenticationReader
 */
@Slf4j
public class AnonymousAuthenticationReader implements GrpcAuthenticationReader {

    private final String key;
    private final Object principal;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Creates a new AnonymousAuthenticationReader with the given key and {@code "anonymousUser"} as principal with the
     * {@code ROLE_ANONYMOUS}.
     *
     * @param key The key to used to identify tokens that were created by this instance.
     */
    public AnonymousAuthenticationReader(final String key) {
        this(key, "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    }

    /**
     * Creates a new AnonymousAuthenticationReader with the given key,principal and authorities.
     *
     * @param key The key to used to identify tokens that were created by this instance.
     * @param principal The principal which will be used to represent anonymous users.
     * @param authorities The authority list for anonymous users.
     */
    public AnonymousAuthenticationReader(final String key, final Object principal,
            final Collection<? extends GrantedAuthority> authorities) {
        this.key = requireNonNull(key, "key");
        this.principal = requireNonNull(principal, "principal");
        this.authorities = requireNonNull(authorities, "authorities");
    }

    @Override
    public Authentication readAuthentication(final ServerCall<?, ?> call, final Metadata headers) {
        log.debug("Continue with anonymous auth");
        return new AnonymousAuthenticationToken(this.key, this.principal, this.authorities);
    }

}
