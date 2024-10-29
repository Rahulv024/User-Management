package com.example.ds;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class server {

    private Server grpcServer;

    // In-memory database to store user data
    private Map<String, User> users = new HashMap<>();//In-memory database to store the data

    public static void main(String[] args) throws IOException, InterruptedException {
        server myServer = new server();
        myServer.start();
        myServer.blockUntilShutdown();
    }

    private void start() throws IOException {
        grpcServer = ServerBuilder.forPort(50052)
                .addService(new UserManagementServiceImpl())//It adds the service implementation (UserManagementServiceImpl) to handle incoming requests.
                .build()
                .start();
        System.out.println("Server started, listening on 50052");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            server.this.stop();
            System.out.println("Server shut down");
        }));
    }

    private void stop() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    class UserManagementServiceImpl extends UserManagementGrpc.UserManagementImplBase {
        @Override
        public void addUser(AddUserRequest request, StreamObserver<AddUserResponse> responseObserver) {
            String userId = request.getUser().getUserId();
            if (users.containsKey(userId)) {
                responseObserver.onNext(AddUserResponse.newBuilder().setMessage("User already exists.").build());
            } else {
                users.put(userId, request.getUser());
                responseObserver.onNext(AddUserResponse.newBuilder().setMessage("User added successfully.").build());
            }
            responseObserver.onCompleted();
        }

        @Override
        public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
            String userId = request.getUserId();
            if (users.containsKey(userId)) {
                responseObserver.onNext(GetUserResponse.newBuilder()
                        .setUser(users.get(userId))
                        .setMessage("User found")
                        .build());
            } else {
                responseObserver.onNext(GetUserResponse.newBuilder().setMessage("User not found").build());
            }
            responseObserver.onCompleted();
        }
    }
}
