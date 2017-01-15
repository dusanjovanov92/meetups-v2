package com.dusanjovanov.meetups3.rest;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by duca on 15/1/2017.
 */

public class CustomDeserializer<T> implements JsonDeserializer<T> {

    private String nodeName;
    private boolean root;

    public CustomDeserializer() {
        root = true;
    }

    public CustomDeserializer(String nodeName) {
        root = false;
        this.nodeName = nodeName;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (root) {
            return new Gson().fromJson(json, typeOfT);
        }
        else {
            JsonElement node = json.getAsJsonObject().get(nodeName);
            return new Gson().fromJson(node, typeOfT);
        }
    }
}
