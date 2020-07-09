package com.yx.grpc.examples.cloud.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-18 10:47
 * @Description: CloudGrpcClientApplication
 */
@EnableDiscoveryClient
@SpringBootApplication
public class CloudGrpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudGrpcClientApplication.class, args);
    }

}
