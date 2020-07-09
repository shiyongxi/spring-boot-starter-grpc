package com.yx.grpc.examples.cloud.client;

import com.yx.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-18 10:50
 * @Description: GlobalClientInterceptorConfiguration
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@Configuration
public class GlobalClientInterceptorConfiguration {

    @Bean
    public GlobalClientInterceptorConfigurer globalInterceptorConfigurerAdapter() {
        return registry -> registry.addClientInterceptors(new LogGrpcInterceptor());
    }

}
