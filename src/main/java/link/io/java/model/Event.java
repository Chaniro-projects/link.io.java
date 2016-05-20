package link.io.java.model;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Event {
    private String type;
    private boolean me;
    private JSONObject data;
    private JSONObject root;
    private static Gson gson;

    public Event(JSONObject jsonObj) {
        if(gson == null)
            gson = new Gson();

        try {
            root = jsonObj;
            this.type = jsonObj.getString("type");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getString() {
        try {
            return root.get("data").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Integer getInteger() {
        try {
            return Integer.parseInt(root.get("data").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Double getDouble() {
        try {
            return Double.parseDouble(root.get("data").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Float getFloat() {
        try {
            return Float.parseFloat(root.get("data").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> T getObject(Class<T> type) {
        try {
            return gson.fromJson(root.get("data").toString(), type);
        } catch (JSONException e) {
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
