package com.example.ds;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class client {

    public static void main(String[] args) {
        // Create a channel to connect to the server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        // Create a stub to interact with the server
        UserManagementGrpc.UserManagementBlockingStub stub = UserManagementGrpc.newBlockingStub(channel);

        // Add a user
        User user = User.newBuilder()
                .setUserId("1")
                .setName("Alice")
                .build();
        AddUserResponse addUserResponse = stub.addUser(AddUserRequest.newBuilder().setUser(user).build());
        System.out.println("Add User Response: " + addUserResponse.getMessage());

        // Get user info
        GetUserResponse getUserResponse = stub.getUser(GetUserRequest.newBuilder().setUserId("1").build());
        System.out.println("Get User Response: Name - " + getUserResponse.getUser().getName() + ", Message - " + getUserResponse.getMessage());

        // Shutdown the channel
        channel.shutdown();
    }
}
