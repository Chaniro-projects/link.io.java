package link.io.java;

import link.io.java.model.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start");
        LinkIOSetup.getInstance()
                .connectTo("localhost:8080")
                .withMail("bastien.baret@insa-rennes.fr")
                .withPassword("123")
                .withAPIKey("JPTsyyDfN7GDnfk6zNpI")
                .connect(new ConnectionListener() {
                    @Override
                    public void connected(final LinkIO linkIO) {
                        System.out.println("Connected as: " + linkIO.getCurrentUser().getMail());

                        linkIO.joinRoom("abcd", new JoinRoomListener() {
                            @Override
                            public void roomJoined(String roomID, ArrayList<User> usersInRoom) {
                                linkIO.onUserJoinRoom(new UserJoinListener() {
                                    @Override
                                    public void userJoin(User user) {
                                        System.out.println("in: " + user.getMail());
                                    }
                                });

                                linkIO.onUserLeftRoom(new UserLeftListener() {
                                    @Override
                                    public void userLeft(User user) {
                                        System.out.println("out: " + user.getMail());
                                    }
                                });
                            }
                        });
                    }
                });

        Scanner sc = new Scanner(System.in);
        sc.nextLine();
    }
}
