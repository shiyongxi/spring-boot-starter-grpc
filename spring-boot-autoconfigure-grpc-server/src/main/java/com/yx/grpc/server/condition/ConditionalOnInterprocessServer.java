package com.yx.grpc.server.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:09
 * @Description: ConditionalOnInterprocessServer
 */
public class ConditionalOnInterprocessServer extends NoneNestedConditions {
    ConditionalOnInterprocessServer() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(name = "spring.grpc.server.port", havingValue = "-1")
    static class NoServerPortCondition {
    }
}
