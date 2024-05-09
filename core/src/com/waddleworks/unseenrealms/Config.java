package com.waddleworks.unseenrealms;

import com.badlogic.gdx.Input;

import java.util.HashMap;

public class Config {
    public static HashMap<String, Integer> keys = new HashMap<>();

    public Config() {
        keys.put("forward", Input.Keys.W);
        keys.put("back", Input.Keys.S);
        keys.put("left", Input.Keys.A);
        keys.put("right", Input.Keys.D);
    }
}
