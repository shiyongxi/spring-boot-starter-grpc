package com.yx.grpc.client.autoconfigure;

import com.yx.grpc.client.inject.StubTransformer;
import com.yx.grpc.client.security.CallCredentialsHelper;
import io.grpc.CallCredentials;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 18:03
 * @Description: GrpcClientSecurityAutoConfiguration
 */
@Configuration
@AutoConfigureBefore(GrpcClientAutoConfiguration.class)
public class GrpcClientSecurityAutoConfiguration {
    @ConditionalOnSingleCandidate(CallCredentials.class)
    @Bean
    StubTransformer stubCallCredentialsTransformer(final CallCredentials credentials) {
        return CallCredentialsHelper.fixedCredentialsStubTransformer(credentials);
    }
}
