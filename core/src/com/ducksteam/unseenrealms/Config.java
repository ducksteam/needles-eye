package com.ducksteam.unseenrealms;

import com.badlogic.gdx.Input;

import java.util.HashMap;

/**
 * Configuration class for the game
 * @author SkySourced
 */
public class Config {
    public static HashMap<String, Integer> keys = new HashMap<>();
    public static float rotationSpeed = 0.7F;
    public static int moveSpeed = 5;
    public static boolean doRenderColliders = true;

    static {
        keys.put("forward", Input.Keys.W);
        keys.put("back", Input.Keys.S);
        keys.put("left", Input.Keys.A);
        keys.put("right", Input.Keys.D);
    }
}
