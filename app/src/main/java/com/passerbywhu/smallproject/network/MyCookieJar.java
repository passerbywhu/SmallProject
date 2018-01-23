package com.passerbywhu.smallproject.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.passerbywhu.smallproject.MyApplication;
import com.passerbywhu.smallproject.utils.GsonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MyCookieJar implements CookieJar {
    private final static String OKHTTP3_PREFERENCE_NAME = "cookie_jar";
    private static volatile MyCookieJar instance;
    private volatile Map<String, List<Cookie>> cookiesMap;
    private static final String PREFERENCE_KEY = "cookiesMap";

    public static MyCookieJar getInstance() {
        if (instance == null) {
            synchronized (MyCookieJar.class) {
                if (instance == null) {
                    instance = new MyCookieJar();
                    instance.load();
                }
            }
        }
        return instance;
    }

    private MyCookieJar() {
    }

    private void load() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(OKHTTP3_PREFERENCE_NAME, Context.MODE_PRIVATE);
        cookiesMap = GsonUtils.fromJson(sp.getString(PREFERENCE_KEY, ""), new TypeToken<Map<String, List<Cookie>>>(){});
        if (cookiesMap == null) {
            cookiesMap = new HashMap<>();
        }
    }

    public synchronized void removeAll() {
        if (cookiesMap == null) {
            return;
        }
        cookiesMap.clear();
        flush();
    }

    public synchronized void add(Cookie cookie, boolean needFlush) {
        if (cookie == null) {
            throw new NullPointerException("cookie == null");
        }
        addCookie(cookie);
        if (needFlush) {
            flush();
        }
    }

    private void addCookie(Cookie cookie) {
        if (cookie == null) {
            return;
        }
        String host = cookie.domain();
        List<Cookie> cookieList = cookiesMap.get(host);
        if (cookieList == null) {
            cookieList = new ArrayList<>();
            cookiesMap.put(host, cookieList);
        }
        for (int i = 0; i < cookieList.size(); i ++) {
            if (cookieList.get(i).name().equals(cookie.name())) {
                cookieList.remove(i);
                break;
            }
        }
        cookieList.add(cookie);
    }

    private Cookie cloneCookie(Cookie cookie, String domain) {
        Cookie.Builder builder = new Cookie.Builder();
        builder.name(cookie.name());
        builder.value(cookie.value());
        builder.domain(domain);
        builder.expiresAt(cookie.expiresAt());
        builder.path(cookie.path());
        if (cookie.secure()) {
            builder.secure();
        }
        return builder.build();
    }

    private void flush() {
        if (cookiesMap == null) {
            return;
        }
        String jsonStr = GsonUtils.toJson(cookiesMap, new TypeToken<Map<String, List<Cookie>>>(){});
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(OKHTTP3_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putString(PREFERENCE_KEY, jsonStr);
        editor.commit();
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies == null || cookies.size() == 0) {
            return;
        }
        for (Cookie cookie : cookies) {
            add(cookie, true);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookieList = new ArrayList<>();
        String domain = url.host();
        synchronized (this) {
            if (cookiesMap.containsKey(domain)) {
                cookieList.addAll(cookiesMap.get(domain));
            }
        }
        return cookieList;
    }

    private Cookie buildCookie(String name, String value, String domain) {
        try {
            value = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Cookie cookie = new Cookie.Builder().name(name).value(value).domain(domain).build();
        return cookie;
    }
}
