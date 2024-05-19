package com.ducksteam.unseenrealms;

import com.badlogic.gdx.Input;

import java.util.HashMap;

/**
 * Configuration class for the game
 * @author SkySourced
 */
public class Config {
    public static HashMap<String, Integer> keys = new HashMap<>();
    public static float rotationSpeed = 0.6F;
    public static int moveSpeed = 1;

    static {
        keys.put("forward", Input.Keys.W);
        keys.put("back", Input.Keys.S);
        keys.put("left", Input.Keys.A);
        keys.put("right", Input.Keys.D);
    }
}
