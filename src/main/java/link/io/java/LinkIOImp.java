package link.io.java;

import com.google.gson.Gson;
import link.io.java.exception.NotConnectedException;
import link.io.java.model.*;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class LinkIOImp implements LinkIO {
    private Socket socket;
    private String serverIP;
    private String mail;
    private String password;
    private String api_key;

    private ConcurrentHashMap<String, EventListener> eventListeners;
    private Gson gson;
    private User currentUser;
    private ArrayList<User> usersInRoom;
    private UserJoinListener userJoinListener;
    private UserLeftListener userLeftListener;

    LinkIOImp() {
        usersInRoom = new ArrayList<>();
        eventListeners = new ConcurrentHashMap<String, EventListener>();
        gson = new Gson();
    }

    public static LinkIO create() {
        return new LinkIOImp();
    }


    public void connect(final ConnectionListener connectionListener)  {
        try {
            IO.Options options = new IO.Options();
            options.query = "mail=" + mail + "&password=" + password + "&api_key=" + api_key;
            socket = IO.socket("http://" + serverIP, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        socket.on("users", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray users = (JSONArray) args[0];
                ArrayList<User> userList = new ArrayList<User>();
                for(int i = 0; i<users.length(); i++) {
                    try {
                        userList.add(gson.fromJson(users.getJSONObject(i).toString(), User.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                if(userList.size() > usersInRoom.size()) {
                    for(User user1 : userList) {
                        boolean found = false;
                        for(User user2 : usersInRoom) {
                            if(user1.getId().equals(user2.getId()))
                                found = true;
                        }

                        if(!found && userJoinListener != null)
                            userJoinListener.userJoin(user1);
                    }
                }
                else {
                    for(User user1 : usersInRoom) {
                        boolean found = false;
                        for(User user2 : userList) {
                            if(user1.getId().equals(user2.getId()))
                                found = true;
                        }

                        if(!found && userLeftListener != null)
                            userLeftListener.userLeft(user1);
                    }
                }
                usersInRoom = userList;
            }
        });

        socket.on("info", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject event = (JSONObject) args[0];
                try {
                    currentUser = new User(
                            event.get("name").toString(),
                            event.get("firstname").toString(),
                            event.get("role").toString(),
                            event.get("mail").toString(),
                            event.get("id").toString()
                    );

                    connectionListener.connected(LinkIOImp.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("event", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                try {
                    JSONObject event = (JSONObject) objects[0];
                    String eventName = event.getString("type");
                    if (eventListeners.containsKey(eventName))
                        eventListeners.get(eventName).eventReceived(new Event(event));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.connect();
    }

    @Override
    public void createRoom(JoinRoomListener callback) {
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
    public void joinRoom(final String roomID, final JoinRoomListener callback) {
        try {
            checkConnect();
            socket.emit("joinRoom", roomID, new Ack() {
                @Override
                public void call(Object... args) {
                    JSONArray users = (JSONArray) args[1];
                    ArrayList<User> userList = new ArrayList<User>();
                    for(int i = 0; i<users.length(); i++) {
                        try {
                            userList.add(gson.fromJson(users.getJSONObject(i).toString(), User.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    usersInRoom = userList;
                    callback.roomJoined(roomID, userList);
                }
            });
        } catch(NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<User> getAllUsersInCurrentRoom() {
        return usersInRoom;
    }

    @Override
    public void onUserJoinRoom(UserJoinListener userJoinListener) {
        this.userJoinListener = userJoinListener;
    }

    @Override
    public void onUserLeftRoom(UserLeftListener userLeftListener) {
        this.userLeftListener = userLeftListener;
    }

    @Override
    public void on(String eventName, EventListener listener) {
        if(eventListeners.contains(eventName))
            eventListeners.remove(eventName);
        eventListeners.put(eventName, listener);
    }

    @Override
    public void off(String eventName) {
        if(eventListeners.contains(eventName))
            eventListeners.remove(eventName);
    }

    @Override
    public void send(String eventName, Object data, Boolean receiveAlso) {
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
    public void send(String eventName, Object data, ArrayList<User> receivers, Boolean receiveAlso) {

    }

    @Override
    public void send(String eventName, Object data, ArrayList<User> receivers) {

    }

    @Override
    public void getLatency(LatencyListener listener) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    private void checkConnect() throws NotConnectedException {
        if(socket == null)
            throw new NotConnectedException("LinkIO: please call connect() before.");
        else if(!socket.connected())
            throw new NotConnectedException("LinkIO: socket disconnected.");
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAPIKey() {
        return api_key;
    }

    public void setAPIKey(String api_key) {
        this.api_key = api_key;
    }
}
