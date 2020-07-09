package com.yx.grpc.examples.cloud.server;

import com.yx.grpc.examples.api.HelloReply;
import com.yx.grpc.examples.api.HelloRequest;
import com.yx.grpc.examples.api.HelloWorldProto;
import com.yx.grpc.server.service.GrpcMethod;
import com.yx.grpc.server.service.GrpcService;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:28
 * @Description: GrpcServerService
 */
@GrpcService(serviceName = "Simple2", protoClass = HelloWorldProto.class)
public class GrpcServerService2 {

    @GrpcMethod(methodName = "SayHello2")
    public HelloReply test(HelloRequest req) {
        return HelloReply.newBuilder().setMessage("Hello ==> " + req.getName()).build();
    }

}
