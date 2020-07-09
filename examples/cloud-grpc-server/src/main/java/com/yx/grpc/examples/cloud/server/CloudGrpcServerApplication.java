package com.yx.grpc.examples.cloud.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:23
 * @Description: CloudGrpcServerApplication
 */
@EnableDiscoveryClient
@SpringBootApplication
public class CloudGrpcServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudGrpcServerApplication.class, args);
    }
}
