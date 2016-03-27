package com.insarennes.connectio.lib.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Event {
    private String type;
    private boolean me;
    private JSONObject data;

    public Event(JSONObject jsonObj) {
        try {
            this.type = jsonObj.getString("type");
            data = jsonObj.getJSONObject("data");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String s, Class<T> type) {
        try {
            return type.cast(data.get(s));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
