package com.ducksteam.needleseye;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Configuration class for the game
 * @author SkySourced
 * @author thechiefpotatopeeler
 */
public class Config {
    // Modifiable
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
    public static String savePath;

    /** Use <code>Config.prefs.getString("Resolution")</code> */
    private static Resolution resolution;
    /** Use <code>Config.prefs.getString("WindowType")</code> */
    private static WindowType windowType;
    /** Use <code>Config.prefs.getString("VSync")</code> */
    private static boolean vSync;
    public static int brightness;
    public static int sensitivity;
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
        sensitivity = prefs.getInteger("MouseSpeed", 100);
        musicVolume = prefs.getInteger("MusicVolume", 50);
        sfxVolume = prefs.getInteger("SFXVolume", 50);

        audioOutputDevice = prefs.getString("AudioDevice", null);

        if (audioOutputDevice != null && audioOutputDevice.isBlank()) audioOutputDevice = null; // null sets to the default audio output, but it cannot be saved to prefs file so "" is used instead
        boolean audioSwitchSuccess = Main.audio.getDevice().switchToDevice(audioOutputDevice);
        if (!audioSwitchSuccess) {
            // if there is a problem setting the saved audio device, reset to default. if the saved audio device is default, there is a problem
            if (audioOutputDevice == null) Gdx.app.error("Config-Audio", "Failed to switch to default audio device");

            Main.audio.getDevice().switchToDevice(null);
            Gdx.app.error("Config-Audio", "Failed to switch to " + audioOutputDevice);
            audioOutputDevice = null;
        }

        savePath = prefs.getString("SavePath","saves/");

        Keybind.clear();

        new Keybind(Keybind.KeybindType.MOVEMENT, "Forward", Input.Keys.W);
        new Keybind(Keybind.KeybindType.MOVEMENT, "Backward", Input.Keys.S);
        new Keybind(Keybind.KeybindType.MOVEMENT, "Left", Input.Keys.A);
        new Keybind(Keybind.KeybindType.MOVEMENT, "Right", Input.Keys.D);
        new Keybind(Keybind.KeybindType.MOVEMENT, "Jump", Input.Keys.SPACE);
        new Keybind(Keybind.KeybindType.MOVEMENT, "Run", Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT);
        new Keybind(Keybind.KeybindType.ABILITY, "Advance Level", Input.Keys.R);
        new Keybind(Keybind.KeybindType.ABILITY, "Switch Ability Mode", Input.Keys.CONTROL_LEFT);
        new Keybind(Keybind.KeybindType.OTHER, "Pause", Input.Keys.ESCAPE);
        new Keybind(Keybind.KeybindType.DEBUG, "Toggle Music");
        new Keybind(Keybind.KeybindType.DEBUG, "Toggle DebugDrawer");
        new Keybind(Keybind.KeybindType.DEBUG, "Toggle Debug Info");
        new Keybind(Keybind.KeybindType.DEBUG, "Toggle Gravity");
        new Keybind(Keybind.KeybindType.DEBUG, "Toggle Room Rendering");
        new Keybind(Keybind.KeybindType.DEBUG, "Move Player Up");
        new Keybind(Keybind.KeybindType.DEBUG, "Heal");
        new Keybind(Keybind.KeybindType.DEBUG, "Damage");
        new Keybind(Keybind.KeybindType.DEBUG, "Step Visualiser Forward");
        new Keybind(Keybind.KeybindType.DEBUG, "Step Visualiser Backward");

        flushPrefs();
    }

    /** apply new preferences and save to disk */
    public static void flushPrefs(){
        prefs.putString("Resolution", resolution.toString());
        prefs.putString("WindowType", windowType.toString());

        switch (windowType) {
            case WINDOWED -> {
                if (!(!Gdx.graphics.isFullscreen() && Gdx.graphics.getWidth() == resolution.width && Gdx.graphics.getHeight() == resolution.height)) {
                    Gdx.graphics.setWindowedMode(resolution.width, resolution.height);
                }
            }
            case FULLSCREEN -> {
                if (!(Gdx.graphics.isFullscreen() && Gdx.graphics.getWidth() == resolution.width && Gdx.graphics.getHeight() == resolution.height)) {
                    Gdx.graphics.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
                }
            }
        }

        prefs.putString("SavePath", savePath);

        prefs.putBoolean("VSync", vSync);
        Gdx.graphics.setVSync(vSync);

        prefs.putInteger("Brightness", brightness);
        prefs.putInteger("MouseSpeed", sensitivity);
        prefs.putInteger("MusicVolume", musicVolume);
        prefs.putInteger("SFXVolume", sfxVolume);
        if (audioOutputDevice != null && !audioOutputDevice.isBlank()) prefs.putString("AudioDevice", audioOutputDevice);
        else prefs.putString("AudioDevice", "");

        Keybind.flushAll();

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

    public static Resolution getFullscreenResolution() {
        return new Resolution(Lwjgl3ApplicationConfiguration.getDisplayMode());
    }

    public enum WindowType {
        WINDOWED,
        FULLSCREEN;

        static final WindowType defaultValue = WINDOWED;

        /** Returns an array of titlecase strings for each value in the enum */
        public static String[] getUserStrings() {
            return Arrays.stream(WindowType.values()).map(WindowType::getUserString).toArray(String[]::new);
        }

        /** Returns a titlecase string of the display mode */
        public String getUserString() {
            String[] chars = this.toString().toLowerCase().split("");
            chars[0] = chars[0].toUpperCase();
            return String.join("", chars);
        }
    }

    public static class Resolution implements Comparable<Resolution> {
        public int width;
        public int height;

        static final char SEPARATOR = 'x';

        public static TreeSet<Resolution> resolutions = new TreeSet<>();

        static {
            resolutions.add(new Resolution(3840, 2160)); // 4k
            resolutions.add(new Resolution(3200, 1800)); // qhd+
            resolutions.add(new Resolution(2560, 1440)); // qhd
            resolutions.add(new Resolution(1920, 1080)); // full hd
            resolutions.add(new Resolution(1600, 900)); // hd+
            resolutions.add(new Resolution(1280, 720)); // hd
        }

        public Resolution(int width, int height) {
            if (width <= 0 || height <= 0) throw new IllegalArgumentException("Resolution width and height must be positive");
            if ((float) width/16.0*9.0 != height) System.err.println("[Options] Resolution may not be correct aspect ratio: " + width + SEPARATOR + height); // this is called in Lwjgl3Launcher so we cannot use Gdx.app as it is not created at that point
            this.width = width;
            this.height = height;
        }

        public Resolution(Graphics.DisplayMode displayMode) {
            this(displayMode.width, displayMode.height);
        }

        public static void addMatchingResolutions(Graphics.Monitor monitor) {
            Arrays.stream(Lwjgl3ApplicationConfiguration.getDisplayModes(monitor)).forEach((Graphics.DisplayMode m) -> resolutions.add(new Resolution(m)));
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
        public int compareTo(Resolution o) {
            int thisPixels = this.width * this.height;
            int otherPixels = o.width * o.height;
            return thisPixels - otherPixels;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Resolution)) return false;
            return ((Resolution) o).width == this.width && ((Resolution) o).height == this.height;
        }
    }
}
