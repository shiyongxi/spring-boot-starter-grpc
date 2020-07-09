# gRPC Spring Boot Starter

### gRPC 服务端

使用一下命令添加 Maven 依赖项：

````xml
<dependency>
  <groupId>com.yx</groupId>
  <artifactId>spring-boot-autoconfigure-grpc-server</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
````

在服务端接口实现类上添加 `@Service` 注解。

````java
@Service
public class GrpcServerService extends SimpleGrpc.SimpleImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello ==> " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
````

### gRPC 客户端

使用一下命令添加 Maven 依赖项：

````xml
<dependency>
  <groupId>com.yx</groupId>
  <artifactId>spring-boot-autoconfigure-grpc-client</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
````

在 grpc 客户端的的 stub 字段上添加 `@Autowireed` 注解。

阻塞模式

  ````java
    @Autowired
    private SimpleGrpc.SimpleBlockingStub simpleStub;
      
    public String sendMessage(final String name) {
        try {
            final HelloReply response = this.simpleStub.sayHello(HelloRequest.newBuilder().setName(name).build());
            return response.getMessage();
        } catch (final StatusRuntimeException e) {
            return "FAILED with " + e.getStatus().getCode();
        }
    }
  ````

异步模式

  ````java
    @Autowired
    private SimpleGrpc.SimpleFutureStub simpleStub;
    
    public String sendMessage(final String name) {
        try {
            final
            ListenableFuture<HelloReply> future = this.simpleStub.sayHello(HelloRequest.newBuilder().setName(name).build());
            return future.get().getMessage();
        } catch (final StatusRuntimeException e) {
            return "FAILED with " + e.getStatus().getCode();
        } catch (InterruptedException e) {
            return "FAILED with " + e.getMessage();
        } catch (ExecutionException e) {
            return "FAILED with " + e.getMessage();
        }
    }
  ````

## 使用 (non-shaded) grpc-netty 运行

这个库支持`grpc-netty`和`grpc-nety-shaded`。 后一种可能会防止与不兼容的 gRPC 版本冲突或不同 netty 版本之间的冲突。

**注意:** 如果在classpath 中存在 shaded netty， 则 shaded netty 将使用有线与 non-shaded grpc-netty。

您可以在 Maven 中这样使用。

````xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty</artifactId>
    <version>${grpcVersion}</version>
</dependency>

<!-- For the server (only) -->
<dependency>
    <groupId>com.yx</groupId>
    <artifactId>spring-boot-autoconfigure-grpc-server</artifactId>
    <version>...</version>
    <exclusions>
        <exclusion>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-netty-shaded</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!-- For the client (only) -->
<dependency>
    <groupId>com.yx</groupId>
    <artifactId>spring-boot-autoconfigure-grpc-client</artifactId>
    <version>...</version>
    <exclusions>
        <exclusion>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-netty-shaded</artifactId>
        </exclusion>
    </exclusions>
</dependency>
````
