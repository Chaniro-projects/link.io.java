package link.io.java;

import link.io.java.model.*;

import java.util.ArrayList;

public interface LinkIO {
    void createRoom(JoinRoomListener callback);

    void joinRoom(String roomID, JoinRoomListener callback);

    ArrayList<User> getAllUsersInCurrentRoom();

    void onUserJoinRoom(UserJoinListener userJoinListener);

    void onUserLeftRoom(UserLeftListener userLeftListener);

    void on(String eventName, EventListener listener);

    void off(String eventName);

    void send(String eventName, Object data, Boolean receiveAlso);

    void send(String eventName, Object data);

    void send(String eventName, Object data, ArrayList<User> receivers, Boolean receiveAlso);

    void send(String eventName, Object data, ArrayList<User> receivers);

    void getLatency(LatencyListener listener);

    boolean isConnected();

    void disconnect();

    User getCurrentUser();
}
