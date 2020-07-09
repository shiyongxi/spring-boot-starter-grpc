package com.yx.grpc.server.autoconfigure;

import com.yx.grpc.server.security.authentication.GrpcAuthenticationReader;
import com.yx.grpc.server.security.check.GrpcSecurityMetadataSource;
import com.yx.grpc.server.security.interceptors.AuthenticatingServerInterceptor;
import com.yx.grpc.server.security.interceptors.AuthorizationCheckingServerInterceptor;
import com.yx.grpc.server.security.interceptors.DefaultAuthenticatingServerInterceptor;
import com.yx.grpc.server.security.interceptors.ExceptionTranslatingServerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.WebSecurityEnablerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:11
 * @Description: GrpcServerSecurityAutoConfiguration
 */
@Configuration
@ConditionalOnBean(AuthenticationManager.class)
@AutoConfigureAfter(WebSecurityEnablerConfiguration.class)
public class GrpcServerSecurityAutoConfiguration {

    /**
     * The interceptor for handling security related exceptions such as {@link AuthenticationException} and
     * {@link AccessDeniedException}.
     *
     * @return The exceptionTranslatingServerInterceptor bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public ExceptionTranslatingServerInterceptor exceptionTranslatingServerInterceptor() {
        return new ExceptionTranslatingServerInterceptor();
    }

    /**
     * The security interceptor that handles the authentication of requests.
     *
     * @param authenticationManager The authentication manager used to verify the credentials.
     * @param authenticationReader  The authentication reader used to extract the credentials from the call.
     * @return The authenticatingServerInterceptor bean.
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticatingServerInterceptor.class)
    public DefaultAuthenticatingServerInterceptor authenticatingServerInterceptor(
            final AuthenticationManager authenticationManager,
            final GrpcAuthenticationReader authenticationReader) {
        return new DefaultAuthenticatingServerInterceptor(authenticationManager, authenticationReader);
    }

    /**
     * The security interceptor that handles the authorization of requests.
     *
     * @param accessDecisionManager  The access decision manager used to check the requesting user.
     * @param securityMetadataSource The source for the security metadata (access constraints).
     * @return The authorizationCheckingServerInterceptor bean.
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({AccessDecisionManager.class, GrpcSecurityMetadataSource.class})
    public AuthorizationCheckingServerInterceptor authorizationCheckingServerInterceptor(
            final AccessDecisionManager accessDecisionManager,
            final GrpcSecurityMetadataSource securityMetadataSource) {
        return new AuthorizationCheckingServerInterceptor(accessDecisionManager, securityMetadataSource);
    }
}