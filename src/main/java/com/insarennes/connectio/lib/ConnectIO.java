package com.insarennes.connectio.lib;

import io.socket.client.Socket;
import org.json.JSONObject;

public interface ConnectIO {
    public ConnectIO connectTo(String serverIP);
    public ConnectIO withUser(String user);
    public ConnectIO connect(Listener listener);

    public void createGroup();
    public void joinGroup(String groupID);

    public void onJoinGroup(Listener listener);
    public void onUserInGroupChanged(Listener listener);
    public void on(String eventName, Listener listener);
    public void off(String eventName);

    public void send(String eventName, JSONObject data, boolean receiveAlso);
    public void send(String eventName, JSONObject data);
    public void send(String eventName, Object data, boolean receiveAlso);
    public void send(String eventName, Object data);

    public void getLatency(Listener listener);

    public static interface Listener {
        public void call(Object... args);
    }

    //To remove
    public void setTestListener(Listener l);
    public void emitTestEvent();
}
