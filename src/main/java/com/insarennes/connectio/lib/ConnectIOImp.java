package com.insarennes.connectio.lib;

import com.google.gson.Gson;
import com.insarennes.connectio.lib.exception.NotConnectedException;
import com.insarennes.connectio.lib.model.Event;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectIOImp implements ConnectIO{
    private Socket socket;
    private String serverIP;
    private String user;
    private Listener joinGroupListener;
    private Listener userInGroupChangedListener;
    private Listener testListener;
    private ConcurrentHashMap<String, Listener> eventListeners;
    private Gson gson;

    private ConnectIOImp() {
        eventListeners = new ConcurrentHashMap<String, Listener>();
        gson = new Gson();
    }

    public static ConnectIO create() {
        return new ConnectIOImp();
    }

    public ConnectIO connectTo(String serverIP) {
        this.serverIP = serverIP;
        return this;
    }

    public ConnectIO withUser(String user) {
        this.user = user;
        return this;
    }

    public ConnectIO connect(final Listener listener)  {
        try {
            IO.Options options = new IO.Options();
            options.query = "user=" + user;
            socket = IO.socket("http://" + serverIP, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        socket.on("joinedGroup", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(joinGroupListener != null)
                    joinGroupListener.call((String) args[0]);
            }
        });

        socket.on("users", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(userInGroupChangedListener != null)
                    userInGroupChangedListener.call(args);
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                listener.call();
            }
        });

        socket.on("event", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                try {
                    JSONObject event = (JSONObject) objects[0];
                    String eventName = event.getString("type");
                    if (eventListeners.containsKey(eventName))
                        eventListeners.get(eventName).call(new Event(event));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("test", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(testListener != null)
                    testListener.call(args);
            }
        });

        socket.connect();

        return this;
    }

    @Override
    public void createGroup() {
        try {
            checkConnect();
            socket.emit("createRoom", new Ack() {
                @Override
                public void call(Object... args) {

                }
            });
        } catch(NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinGroup(String groupID) {
        try {
            checkConnect();
            socket.emit("joinRoom", groupID, new Ack() {
                @Override
                public void call(Object... args) {
                    int a = 5;
                    a++;
                }
            });
        } catch(NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJoinGroup(Listener listener) {
        joinGroupListener = listener;
    }

    @Override
    public void onUserInGroupChanged(Listener listener) {
        userInGroupChangedListener = listener;
    }

    @Override
    public void on(String eventName, Listener listener) {
        eventListeners.put(eventName, listener);
    }

    @Override
    public void off(String eventName) {
        eventListeners.remove(eventName);
    }

    @Override
    public void send(String eventName, JSONObject data, boolean receiveAlso) {
        try {
            checkConnect();
            JSONObject o = new JSONObject();
            o.put("me", receiveAlso);
            o.put("type", eventName);
            o.put("data", data);
            socket.emit("event", o);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String eventName, JSONObject data) {
        send(eventName, data, false);
    }

    @Override
    public void send(String eventName, Object data, boolean receiveAlso)  {
        try {
            checkConnect();
            HashMap<String, Object> datas = new HashMap<String, Object>();
            datas.put("type", eventName);
            datas.put("me", receiveAlso);
            datas.put("data", data);
            socket.emit("event", new JSONObject(gson.toJson(datas)));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String eventName, Object data) {
        send(eventName, data, false);
    }

    @Override
    public void getLatency(Listener listener){
        try {
            checkConnect();
            socket.emit("ping");
        } catch(NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void checkConnect() throws NotConnectedException {
        if(socket == null)
            throw new NotConnectedException("ConnectIO: please call connect() before.");
        else if(!socket.connected())
            throw new NotConnectedException("ConnectIO: socket disconnected.");
    }

    @Override
    public void emitTestEvent() {
        socket.emit("test");
    }

    @Override
    public void setTestListener(Listener l) {
        testListener = l;
    }
}
