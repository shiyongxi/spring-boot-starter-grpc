package com.yx.grpc.examples.cloud.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.yx.grpc.examples.api.HelloReply;
import com.yx.grpc.examples.api.HelloRequest;
import com.yx.grpc.examples.api.Simple2Grpc;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-18 10:57
 * @Description: GrpcClientService
 */
@Service
public class GrpcClientService {

    @Autowired
    private Simple2Grpc.Simple2FutureStub simpleStub;

    public String sendMessage(final String name) {
        try {
            final
            ListenableFuture<HelloReply> future = this.simpleStub.sayHello2(HelloRequest.newBuilder().setName(name).build());
            return future.get().getMessage();
        } catch (final StatusRuntimeException e) {
            return "FAILED with " + e.getStatus().getCode();
        } catch (InterruptedException e) {
            return "FAILED with " + e.getMessage();
        } catch (ExecutionException e) {
            return "FAILED with " + e.getMessage();
        }
    }

}
