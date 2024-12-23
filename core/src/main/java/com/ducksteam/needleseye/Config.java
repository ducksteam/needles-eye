package com.ducksteam.needleseye;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import de.pottgames.tuningfork.AudioDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Configuration class for the game
 * @author SkySourced
 * @author thechiefpotatopeeler
 */
public class Config {
    // Modifiable
    /**
     * The keys used for player input, see static block for defaults
     * String is the action, Integer is the key code
     */
    public static HashMap<String, Integer> keys = new HashMap<>();

    /**
     * Whether the debug menu is enabled, toggled by default with F9
     */
    public static boolean debugMenu = false;

    /**
     * Whether the bullet debug renderer is enabled, toggled by default with F8
     */
    public static boolean doRenderColliders = false;

    /**
     * The current player move speed, changed in {@link com.ducksteam.needleseye.player.PlayerInput}
     */
    public static float moveSpeed;

    // Global constants
    /**
     * The player's starting position in the central hallway
     */
    public static final Vector3 PLAYER_START_POSITION = new Vector3(-5, 0.52f, -5);
    /**
     * The speed at which the mouse moves the camera
     */
    public static final float ROTATION_SPEED = 0.007F;
    /**
     * The speed of the loading animation (time per frame in seconds)
     */
    public static final float LOADING_ANIM_SPEED = 0.05f;
    /**
     * The speed at which the attack animation plays (time per frame in seconds)
     */
    public static final float ATTACK_ANIM_SPEED = 0.03f;
    /**
     * The ideal aspect ratio of the game
     */
    public static final float ASPECT_RATIO = (float) 16 / 9;
    /**
     * The target width of the game at startup
     */
    public static final int TARGET_WIDTH = 1920;
    /**
     * The target height of the game at startup
     */
    public static final int TARGET_HEIGHT = 1080;
    /**
     * The scale factor from converting from room space to world space
     */
    public static final int ROOM_SCALE = 10;
    /**
     * The mass of the player in kg
     */
    public static final float PLAYER_MASS = 10;
    /**
     * The time in seconds before an entity can be damaged again
     */
    public static final float DAMAGE_TIMEOUT = 1;
    /**
     * The time in seconds the screen flashes red when the player is damaged
     */
    public static final float DAMAGE_SCREEN_FLASH = 0.5f;
    /**
     * The mean height where upgrade entities spawn see {@link com.ducksteam.needleseye.entity.pickups.UpgradeEntity}
     */
    public static final float UPGRADE_HEIGHT = 1.6f;
    /**
     * The height where soul fire particles are spawned see {@link com.ducksteam.needleseye.entity.effect.SoulFireEffectManager}
     */
    public static final float SOUL_FIRE_HEIGHT = 0f;
    /**
     * The AoE range of the soul fire particle effect
     */
    public static final float SOUL_FIRE_RANGE = 2f;
    /**
     * The distance from the player the soul fire particles spawn
     */
    public static final float SOUL_FIRE_THROW_DISTANCE = 2.5f;
    /**
     * The time in seconds enemies are paralysed by jolt thread LMB
     */
    public static final float JOLT_PARALYSE_TIME = 1.5f;
    /**
     * The strength of light effects used in the game
     * This really should be refactored
     */
    public static final float LIGHT_INTENSITY = 0.25f;
    /**
     * The colour that used to be for the players lantern I think?
     * As you can see it is not used
     */
    public static final Color LIGHT_COLOUR = new Color(0.4f* LIGHT_INTENSITY, 0.4f* LIGHT_INTENSITY, 0.4f* LIGHT_INTENSITY, 1f);
    /**
     * The strength of the force applied to entities when they are knocked back
     */
    public static final float KNOCKBACK_FORCE = 200f;
    /**
     * The strength of the force associated with walking at normal speed
     */
    public static final float WALK_SPEED = 220f;
    /**
     * The strength of the force associated with running
     */
    public static final float RUN_SPEED = 300f;
    /**
     * The time in seconds that the upgrade text is displayed
     */
    public static final float UPGRADE_TEXT_DISPLAY_TIMEOUT = 3f;
    /** File name for preferences */
    public static final String PREFS_NAME = "needleseye";

    public static Lwjgl3Graphics desktopGraphics;

    public static Preferences prefs;

    /** Use <code>Config.prefs.getString("Resolution")</code> */
    private static Resolution resolution;
    /** Use <code>Config.prefs.getString("WindowType")</code> */
    private static WindowType windowType;
    /** Use <code>Config.prefs.getString("VSync")</code> */
    private static boolean vSync;
    public static int brightness;
    public static int musicVolume;
    public static int sfxVolume;
    public static String audioOutputDevice;

    /** Create prefs instance, load stored values and add defaults if needed */
    public static void init(){
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        desktopGraphics = (Lwjgl3Graphics) Gdx.graphics;

        resolution = new Resolution(prefs.getString("Resolution", String.valueOf(Main.maxResolution.width)+Resolution.SEPARATOR+Main.maxResolution.height));
        windowType = WindowType.valueOf(prefs.getString("WindowType", WindowType.defaultValue.toString()));
        vSync = prefs.getBoolean("VSync", true);
        brightness = prefs.getInteger("Brightness", 50);
        musicVolume = prefs.getInteger("MusicVolume", 50);
        sfxVolume = prefs.getInteger("SFXVolume", 50);
        audioOutputDevice = prefs.getString("AudioDevice", AudioDevice.availableDevices().getFirst());

        flushPrefs();
    }

    /** apply new preferences and save to disk */
    public static void flushPrefs(){
        switch (windowType) {
            case WINDOWED -> {
                if (!(!Gdx.graphics.isFullscreen() && Gdx.graphics.getWidth() == resolution.width && Gdx.graphics.getHeight() == resolution.height)) {
                    Gdx.graphics.setWindowedMode(resolution.width, resolution.height);
                }
            }
            case FULLSCREEN -> {
                if (!(Gdx.graphics.isFullscreen() && Gdx.graphics.getWidth() == resolution.width && Gdx.graphics.getHeight() == resolution.height)) {
                    Gdx.graphics.setWindowedMode(resolution.width, resolution.height);
                }
            }
        }
        prefs.putString("Resolution", resolution.toString());
        prefs.putString("WindowType", windowType.toString());

        Gdx.graphics.setVSync(vSync);
        prefs.putBoolean("VSync", vSync);

        prefs.putInteger("Brightness", brightness);
        prefs.putInteger("MusicVolume", musicVolume);
        prefs.putInteger("SFXVolume", sfxVolume);
        prefs.putString("AudioDevice", audioOutputDevice);
        prefs.flush();
    }

    // these are here to stop access to the raw and unflushed values of window settings
    // Config.prefs.getString("Resolution") should be used instead
    public static void setResolution(Resolution resolution) {
        Config.resolution = resolution;
    }

    public static void setvSync(boolean vSync) {
        Config.vSync = vSync;
    }

    public static void setWindowType(WindowType windowType) {
        Config.windowType = windowType;
    }

    public enum WindowType {
        WINDOWED,
        FULLSCREEN;

        static final WindowType defaultValue = WINDOWED;

        public static String[] getUserStrings() {
            String[] arr = new String[WindowType.values().length];
            WindowType[] values = WindowType.values();
            for (int i = 0; i < values.length; i++) {
                WindowType type = values[i];
                arr[i] = type.toString();
                String[] chars = arr[i].toLowerCase().split("");
                chars[0] = chars[0].toUpperCase();
                arr[i] = String.join("", chars);
            }
            return arr;
        }
    }

    public static class Resolution {
        public int width;
        public int height;

        static final char SEPARATOR = 'x';

        static ArrayList<Resolution> resolutions = new ArrayList<>();

        static {
            resolutions.add(new Resolution(3840, 2160)); // 4k
            resolutions.add(new Resolution(3200, 1800)); // qhd+
            resolutions.add(new Resolution(2560, 1440)); // qhd
            resolutions.add(new Resolution(1920, 1080)); // full hd
            resolutions.add(new Resolution(1600, 900)); // hd+
            resolutions.add(new Resolution(1280, 720)); // hd
            resolutions.add(new Resolution(1024, 576)); // wsvga
            resolutions.add(new Resolution(960, 540)); // qHD
            resolutions.add(new Resolution(848, 480)); // FWVGA
            resolutions.add(new Resolution(640, 360)); // nHD
        }

        public Resolution(int width, int height) {
            if (width <= 0 || height <= 0) throw new IllegalArgumentException("Resolution width and height must be positive");
            if (width/16*9 != height) System.err.println("[Options] Resolution may not be correct aspect ratio: " + width + SEPARATOR + height); // this is called in Lwjgl3Launcher so we cannot use Gdx.app as it is not created at that point
            this.width = width;
            this.height = height;
        }

        public Resolution(Graphics.DisplayMode displayMode) {
            this(displayMode.width, displayMode.height);
        }

        public Resolution(String s) {
            this(Integer.parseInt((s.split(String.valueOf(SEPARATOR))[0])), Integer.parseInt(s.split(String.valueOf(SEPARATOR))[1]));
        }

        public static ArrayList<Resolution> getMatchingResolutions(Resolution maxRes) {
            return resolutions.stream().filter(resolution -> maxRes.width >= resolution.width && maxRes.height >= resolution.height).collect(Collectors.toCollection(ArrayList::new));
        }

        @Override
        public String toString() {
            return String.valueOf(width) + SEPARATOR + height;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Resolution)) return false;
            return ((Resolution) o).width == this.width && ((Resolution) o).height == this.height;
        }

        static {
            keys.put("forward", Input.Keys.W);
            keys.put("back", Input.Keys.S);
            keys.put("left", Input.Keys.A);
            keys.put("right", Input.Keys.D);
            keys.put("jump", Input.Keys.SPACE);
            keys.put("run", Input.Keys.SHIFT_LEFT);
            keys.put("advance", Input.Keys.R);
            keys.put("ability", Input.Keys.CONTROL_LEFT);
        }
    }
}
