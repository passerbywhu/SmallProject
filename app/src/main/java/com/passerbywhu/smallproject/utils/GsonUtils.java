package com.passerbywhu.smallproject.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class GsonUtils {
    private static final Gson gson = new Gson();

    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        try {
            return gson.fromJson(json, typeToken.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(Object object, Class<T> classOfT) {
        try {
            return gson.fromJson(object.toString(), classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static <T> T fromJson(JsonElement jsonElement, TypeToken<T> typeToken) {
        try {
            return gson.fromJson(jsonElement, typeToken.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> String toJson(Object object, TypeToken<T> typeToken) {
        return gson.toJson(object, typeToken.getType());
    }
}
