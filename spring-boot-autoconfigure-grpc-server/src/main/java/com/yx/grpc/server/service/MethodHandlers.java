package com.yx.grpc.server.service;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-24 15:51
 * @Description: MethodHandlers
 */
public interface MethodHandlers<Req, Resp> extends
        io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
        io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
        io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
        io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

}
