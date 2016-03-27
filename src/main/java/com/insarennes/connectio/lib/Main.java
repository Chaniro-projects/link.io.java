package com.insarennes.connectio.lib;

import com.insarennes.connectio.lib.model.Event;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        final ConnectIO connectIO = ConnectIOImp.create()
                .connectTo("bastienbaret.com:8080")
                .withUser("Xperia Léo");

        try {
            connectIO.setTestListener(new ConnectIO.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        System.out.println(((JSONObject)args[0]).get("buffer"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            connectIO.on("image", new ConnectIO.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println(args);
                }
            });

            connectIO.connect(new ConnectIO.Listener() {
                @Override
                public void call(Object... args) {
                    connectIO.joinGroup("abcd");
                }
            });

            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
