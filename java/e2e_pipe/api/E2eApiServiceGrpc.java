package e2e_pipe.api;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * A service that Speech-to-Speech API layer will serve.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.38.0)",
    comments = "Source: e2e_pipe/api/e2e_api_layer_proto.proto")
public final class E2eApiServiceGrpc {

  private E2eApiServiceGrpc() {}

  public static final String SERVICE_NAME = "e2e_pipe.api.E2eApiService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest,
      e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse> getInitE2eMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InitE2e",
      requestType = e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest.class,
      responseType = e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest,
      e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse> getInitE2eMethod() {
    io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest, e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse> getInitE2eMethod;
    if ((getInitE2eMethod = E2eApiServiceGrpc.getInitE2eMethod) == null) {
      synchronized (E2eApiServiceGrpc.class) {
        if ((getInitE2eMethod = E2eApiServiceGrpc.getInitE2eMethod) == null) {
          E2eApiServiceGrpc.getInitE2eMethod = getInitE2eMethod =
              io.grpc.MethodDescriptor.<e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest, e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InitE2e"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse.getDefaultInstance()))
              .setSchemaDescriptor(new E2eApiServiceMethodDescriptorSupplier("InitE2e"))
              .build();
        }
      }
    }
    return getInitE2eMethod;
  }

  private static volatile io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest,
      e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse> getTransE2eMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TransE2e",
      requestType = e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest.class,
      responseType = e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest,
      e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse> getTransE2eMethod() {
    io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest, e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse> getTransE2eMethod;
    if ((getTransE2eMethod = E2eApiServiceGrpc.getTransE2eMethod) == null) {
      synchronized (E2eApiServiceGrpc.class) {
        if ((getTransE2eMethod = E2eApiServiceGrpc.getTransE2eMethod) == null) {
          E2eApiServiceGrpc.getTransE2eMethod = getTransE2eMethod =
              io.grpc.MethodDescriptor.<e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest, e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TransE2e"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse.getDefaultInstance()))
              .setSchemaDescriptor(new E2eApiServiceMethodDescriptorSupplier("TransE2e"))
              .build();
        }
      }
    }
    return getTransE2eMethod;
  }

  private static volatile io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest,
      e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse> getCloseE2eMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CloseE2e",
      requestType = e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest.class,
      responseType = e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest,
      e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse> getCloseE2eMethod() {
    io.grpc.MethodDescriptor<e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest, e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse> getCloseE2eMethod;
    if ((getCloseE2eMethod = E2eApiServiceGrpc.getCloseE2eMethod) == null) {
      synchronized (E2eApiServiceGrpc.class) {
        if ((getCloseE2eMethod = E2eApiServiceGrpc.getCloseE2eMethod) == null) {
          E2eApiServiceGrpc.getCloseE2eMethod = getCloseE2eMethod =
              io.grpc.MethodDescriptor.<e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest, e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CloseE2e"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new E2eApiServiceMethodDescriptorSupplier("CloseE2e"))
              .build();
        }
      }
    }
    return getCloseE2eMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static E2eApiServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<E2eApiServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<E2eApiServiceStub>() {
        @java.lang.Override
        public E2eApiServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new E2eApiServiceStub(channel, callOptions);
        }
      };
    return E2eApiServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static E2eApiServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<E2eApiServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<E2eApiServiceBlockingStub>() {
        @java.lang.Override
        public E2eApiServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new E2eApiServiceBlockingStub(channel, callOptions);
        }
      };
    return E2eApiServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static E2eApiServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<E2eApiServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<E2eApiServiceFutureStub>() {
        @java.lang.Override
        public E2eApiServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new E2eApiServiceFutureStub(channel, callOptions);
        }
      };
    return E2eApiServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * A service that Speech-to-Speech API layer will serve.
   * </pre>
   */
  public static abstract class E2eApiServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Initialize the API service.
     * </pre>
     */
    public void initE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest request,
        io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInitE2eMethod(), responseObserver);
    }

    /**
     * <pre>
     * Process a translation request.
     * </pre>
     */
    public void transE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest request,
        io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTransE2eMethod(), responseObserver);
    }

    /**
     * <pre>
     * Close the API service.
     * </pre>
     */
    public void closeE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest request,
        io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCloseE2eMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getInitE2eMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest,
                e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse>(
                  this, METHODID_INIT_E2E)))
          .addMethod(
            getTransE2eMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest,
                e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse>(
                  this, METHODID_TRANS_E2E)))
          .addMethod(
            getCloseE2eMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest,
                e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse>(
                  this, METHODID_CLOSE_E2E)))
          .build();
    }
  }

  /**
   * <pre>
   * A service that Speech-to-Speech API layer will serve.
   * </pre>
   */
  public static final class E2eApiServiceStub extends io.grpc.stub.AbstractAsyncStub<E2eApiServiceStub> {
    private E2eApiServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected E2eApiServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new E2eApiServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Initialize the API service.
     * </pre>
     */
    public void initE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest request,
        io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInitE2eMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Process a translation request.
     * </pre>
     */
    public void transE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest request,
        io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTransE2eMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Close the API service.
     * </pre>
     */
    public void closeE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest request,
        io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCloseE2eMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * A service that Speech-to-Speech API layer will serve.
   * </pre>
   */
  public static final class E2eApiServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<E2eApiServiceBlockingStub> {
    private E2eApiServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected E2eApiServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new E2eApiServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Initialize the API service.
     * </pre>
     */
    public e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse initE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getInitE2eMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Process a translation request.
     * </pre>
     */
    public e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse transE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTransE2eMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Close the API service.
     * </pre>
     */
    public e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse closeE2e(e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCloseE2eMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * A service that Speech-to-Speech API layer will serve.
   * </pre>
   */
  public static final class E2eApiServiceFutureStub extends io.grpc.stub.AbstractFutureStub<E2eApiServiceFutureStub> {
    private E2eApiServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected E2eApiServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new E2eApiServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Initialize the API service.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse> initE2e(
        e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInitE2eMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Process a translation request.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse> transE2e(
        e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTransE2eMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Close the API service.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse> closeE2e(
        e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCloseE2eMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INIT_E2E = 0;
  private static final int METHODID_TRANS_E2E = 1;
  private static final int METHODID_CLOSE_E2E = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final E2eApiServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(E2eApiServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INIT_E2E:
          serviceImpl.initE2e((e2e_pipe.api.E2EApiLayerProto.E2eApiInitRequest) request,
              (io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiInitResponse>) responseObserver);
          break;
        case METHODID_TRANS_E2E:
          serviceImpl.transE2e((e2e_pipe.api.E2EApiLayerProto.E2eApiTransRequest) request,
              (io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiTransResponse>) responseObserver);
          break;
        case METHODID_CLOSE_E2E:
          serviceImpl.closeE2e((e2e_pipe.api.E2EApiLayerProto.E2eApiCloseRequest) request,
              (io.grpc.stub.StreamObserver<e2e_pipe.api.E2EApiLayerProto.E2eApiCloseResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class E2eApiServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    E2eApiServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return e2e_pipe.api.E2EApiLayerProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("E2eApiService");
    }
  }

  private static final class E2eApiServiceFileDescriptorSupplier
      extends E2eApiServiceBaseDescriptorSupplier {
    E2eApiServiceFileDescriptorSupplier() {}
  }

  private static final class E2eApiServiceMethodDescriptorSupplier
      extends E2eApiServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    E2eApiServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (E2eApiServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new E2eApiServiceFileDescriptorSupplier())
              .addMethod(getInitE2eMethod())
              .addMethod(getTransE2eMethod())
              .addMethod(getCloseE2eMethod())
              .build();
        }
      }
    }
    return result;
  }
}
