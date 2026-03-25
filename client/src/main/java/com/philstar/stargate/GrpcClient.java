package com.philstar.stargate;

import com.philstar.stargate.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class GrpcClient {

    private static final String HOST = "localhost";
    private static final int PORT = 50051;

    private final ManagedChannel channel;
    private final StarGateCoreGrpc.StarGateCoreBlockingStub blocking;
    private final StarGateCoreGrpc.StarGateCoreStub async;

    public GrpcClient() {
        channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext()
                .build();
        blocking = StarGateCoreGrpc.newBlockingStub(channel);
        async    = StarGateCoreGrpc.newStub(channel);
    }

    public LoginResponse login(String username, String password) {
        return blocking.login(LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build());
    }

    public List<ChatSession> getSessions(String userId) {
        return blocking.getSessions(User.newBuilder().setUserId(userId).build())
                .getSessionsList();
    }

    public ChatSession getSession(String sessionId) {
        return blocking.getSession(SessionRequest.newBuilder()
                .setSessionId(sessionId)
                .build());
    }

    public List<Message> getMessages(String sessionId) {
        return blocking.getMessages(SessionRequest.newBuilder()
                .setSessionId(sessionId)
                .build())
                .getMessagesList();
    }

    public ActionResponse sendReply(String sessionId, String text, String userId) {
        return blocking.sendReply(ReplyRequest.newBuilder()
                .setSessionId(sessionId)
                .setMessageText(text)
                .setUserId(userId)
                .build());
    }

    public ActionResponse renameContact(String phone, String name, String userId) {
        return blocking.renameContact(RenameRequest.newBuilder()
                .setContactPhone(phone)
                .setName(name)
                .setUserId(userId)
                .build());
    }

    public ActionResponse assignContact(String phone, String groupId, String userId) {
        return blocking.assignContact(AssignRequest.newBuilder()
                .setContactPhone(phone)
                .setGroupId(groupId)
                .setUserId(userId)
                .build());
    }

    public ChatSession retireContact(String phone, String userId) {
        return blocking.retireContact(RetireRequest.newBuilder()
                .setContactPhone(phone)
                .setUserId(userId)
                .build());
    }

    public List<Group> listGroups(String userId) {
        return blocking.listGroups(User.newBuilder().setUserId(userId).build())
                .getGroupsList();
    }

    public void subscribeToInbox(String userId, StreamObserver<MessageEvent> observer) {
        async.subscribeToInbox(User.newBuilder()
                .setUserId(userId)
                .build(), observer);
    }

    public void shutdown() {
        channel.shutdownNow();
    }
}
