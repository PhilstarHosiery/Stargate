package com.philstar.stargate;

import com.philstar.stargate.proto.ChatSession;
import com.philstar.stargate.proto.Group;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton that holds application-wide state for the current session:
 * the logged-in user, the gRPC client, the session list, and caches
 * for contact names and group names.
 */
public class AppState {

    private static final AppState INSTANCE = new AppState();

    private String userId;
    private String username;
    private GrpcClient grpc;

    private final ObservableList<ChatSession> sessions = FXCollections.observableArrayList();
    private ChatSession selectedSession;

    /** phone → display name (empty until HR renames the contact) */
    private final Map<String, String> contactNames = new HashMap<>();

    /** groupId → group name */
    private final Map<String, String> groupNames = new HashMap<>();

    private AppState() {}

    public static AppState get() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Auth
    // -------------------------------------------------------------------------

    public void setUser(String userId, String username) {
        this.userId   = userId;
        this.username = username;
    }

    public String getUserId()   { return userId; }
    public String getUsername() { return username; }

    // -------------------------------------------------------------------------
    // gRPC client
    // -------------------------------------------------------------------------

    public void setGrpc(GrpcClient grpc) {
        this.grpc = grpc;
    }

    public GrpcClient getGrpc() {
        return grpc;
    }

    public void shutdown() {
        if (grpc != null) {
            grpc.shutdown();
            grpc = null;
        }
    }

    // -------------------------------------------------------------------------
    // Sessions
    // -------------------------------------------------------------------------

    public ObservableList<ChatSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<ChatSession> list) {
        sessions.setAll(list);
    }

    /**
     * Adds a session to the front of the list, or replaces it if already present.
     * Used when a new unknown session arrives via the real-time stream.
     */
    public void replaceOrAddSession(ChatSession newSession) {
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getSessionId().equals(newSession.getSessionId())) {
                sessions.set(i, newSession);
                return;
            }
        }
        sessions.add(0, newSession);
    }

    public ChatSession getSelectedSession()            { return selectedSession; }
    public void setSelectedSession(ChatSession s)      { this.selectedSession = s; }

    // -------------------------------------------------------------------------
    // Contact names
    // -------------------------------------------------------------------------

    public void setContactName(String phone, String name) {
        contactNames.put(phone, name);
    }

    /** Returns the assigned name if known, otherwise the phone number itself. */
    public String getContactDisplayName(String phone) {
        String name = contactNames.get(phone);
        return (name != null && !name.isBlank()) ? name : phone;
    }

    // -------------------------------------------------------------------------
    // Groups
    // -------------------------------------------------------------------------

    public void loadGroups(List<Group> groups) {
        groupNames.clear();
        for (Group g : groups) {
            groupNames.put(g.getId(), g.getName());
        }
    }

    /** Returns the human-readable group name, or "Unassigned" if the id is empty. */
    public String getGroupName(String groupId) {
        if (groupId == null || groupId.isBlank()) return "Unassigned";
        return groupNames.getOrDefault(groupId, groupId);
    }

    /** Returns a snapshot of the groupId → name map. */
    public Map<String, String> getGroupNames() {
        return new HashMap<>(groupNames);
    }

    // -------------------------------------------------------------------------
    // Reset (on logout)
    // -------------------------------------------------------------------------

    public void reset() {
        shutdown();
        userId   = null;
        username = null;
        sessions.clear();
        selectedSession = null;
        contactNames.clear();
        groupNames.clear();
    }
}
