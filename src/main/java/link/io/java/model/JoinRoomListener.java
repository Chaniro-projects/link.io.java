package link.io.java.model;

import java.util.ArrayList;

public interface JoinRoomListener {
    void roomJoined(String roomID, ArrayList<User> usersInRoom);
}
