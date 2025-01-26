package com.ducksteam.needleseye;

import com.badlogic.gdx.Input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents one action that the user can take.
 * @author SkySourced
 */
public class Keybind {

    public enum KeybindType {
        MOVEMENT("Movement"),
        ABILITY("Ability"),
        OTHER("Other"),
        DEBUG("Debug");

        /** The name that will be displayed to the user */
        public final String readableName;
        /** The prefix at the start of the prefs entry, which is just the readableName with spaces removed */
        public final String prefsPrefix;
        /** The keybinds under this category */
        public final ArrayList<Keybind> keybinds;

        /** If true, debug keybinds will be shown in the controls section of the options menu */
        public static final boolean showDebugKeybinds = true;

        KeybindType(String name){
            readableName = name;
            prefsPrefix = String.join("", readableName.split(" "));
            keybinds = new ArrayList<>();
        }
    }
    /** The name that will be displayed to the user */
    public final String readableName;
    /** The prefix at the start of the prefs entry, which is just the readableName with spaces removed */
    public final String prefsName;
    /** The keys that correspond to this action */
    public ArrayList<Integer> keys;
    /** The category of the keybind */
    public final KeybindType type;

    /** All keybinds registered */
    private static final ArrayList<Keybind> keybinds = new ArrayList<>();

    /**
     * Creates a keybind using varargs for the keycodes
     * @param type The category this input should be displayed under in the options menu
     * @param readableName The name that will be shown in game in the options menu
     * @param keycode The default keycodes if none are found in the user's prefs file
     */
    public Keybind(KeybindType type, String readableName, Integer ...keycode) {
        this(type, readableName, new ArrayList<>(Arrays.asList(keycode)));
    }

    /**
     * Creates a keybind
     * @param type The category this input should be displayed under in the options menu
     * @param readableName The name that will be shown in game in the options menu
     * @param defaultKeys The default keycode if none are found in the user's prefs file
     */
    public Keybind(KeybindType type, String readableName, ArrayList<Integer> defaultKeys) {
        this.type = type;
        this.readableName = readableName;
        this.prefsName = String.join("", readableName.split(" "));

        this.keys = tryLoadFromPrefs();
        if (this.keys == null) this.keys = defaultKeys;

        this.type.keybinds.add(this);
        keybinds.add(this);
    }

    /**
     * Reads the prefs file to import any saved keys for this keybind.
     * The name of the preference key is determined by the <code>KeybindType</code> and the <code>readableName</code>
     * so a keybind with category <code>ABILITY</code> and name <code>Advance Level</code> will have a base prefix of
     * <code>Ability_AdvanceLevel</code>. Because multiple keys can be used for each input, the first keybind has <code>_0</code> appended. This continues for each subsequent bind. There is also a <code>_Size</code> entry to describe how many keybinds have been saved in the past.
     * @return a list of keycodes for the current keybind stored in prefs
     */
    private ArrayList<Integer> tryLoadFromPrefs(){
        int prevSize = Config.prefs.getInteger(getPrefsSizeName());
        if (prevSize == 0) return null;
        ArrayList<Integer> keys = new ArrayList<>();
        for (int i = 0; i < prevSize; i++) {
            keys.add(Config.prefs.getInteger(getPrefsKey(i)));
        }
        return keys;
    }

    /**
     * Clear old keybinds and size prefs entries and update with new ones.
     */
    public void flush(){
        int prevSize = Config.prefs.getInteger(getPrefsSizeName());
        for (int i = 0; i < prevSize; i++) {
            Config.prefs.remove(getPrefsKey(i));
        }
        Config.prefs.putInteger(getPrefsSizeName(), keys.size());
        for (int i = 0; i < keys.size(); i++) {
            Config.prefs.putInteger(getPrefsKey(i), keys.get(i));
        }
    }

    /**
     * Runs {@link Keybind#flush()} on all keybinds.
     */
    public static void flushAll() {
        for (KeybindType type : KeybindType.values()) {
            type.keybinds.forEach(Keybind::flush);
        }
    }

    /**
     * @return The list of associated keybinds in readable form
     * @see Input.Keys#toString(int)
     */
    public ArrayList<String> getReadableKeys() {
        ArrayList<String> sKeys = new ArrayList<>();
        for (Integer iKey : keys) {
            sKeys.add(Input.Keys.toString(iKey));
        }
        return sKeys;
    }

    /**
     * @param num the index of the keybind
     * @return the prefs file key for the indexed keybind
     * @see Keybind#tryLoadFromPrefs()
     */
    public String getPrefsKey(int num) {
        return String.join("_", type.prefsPrefix, prefsName, String.valueOf(num));
    }

    /**
     * @return the prefs file key for the number of keybinds saved with this action
     * @see Keybind#tryLoadFromPrefs()
     */
    public String getPrefsSizeName() {
        return String.join("_", type.prefsPrefix, prefsName, "Size");
    }

    /**
     * @param keysPressed a hashmap of keys with boolean 'pressed' values
     * @return whether any of the keybinds associated with this action have been pressed
     * @see com.ducksteam.needleseye.player.PlayerInput#KEYS
     */
    public boolean pressed(HashMap<Integer, Boolean> keysPressed) {
        ArrayList<Integer> activeKeys = keysPressed.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
        for (Integer key : activeKeys) {
            if (this.keys.contains(key)) return true;
        }
        return false;
    }

    /**
     * @param key the name of the key to check, this can be {@link Keybind#prefsName} or {@link Keybind#readableName}
     * @param keysPressed a hashmap of keys with boolean 'pressed' values
     * @return whether any of the keybinds associated with the named keybind have been pressed
     * @see com.ducksteam.needleseye.player.PlayerInput#KEYS
     */
    public static boolean pressed(String key, HashMap<Integer, Boolean> keysPressed) {
        Keybind bind = getKeybind(key);
        if (bind == null) return false;
        return bind.pressed(keysPressed);
    }

    /**
     * @param key the name of the key to check, this can be {@link Keybind#prefsName} or {@link Keybind#readableName}
     * @return the first keybind with the name
     */
    public static Keybind getKeybind(String key) {
        String formattedKey = String.join("", key.split(" "));
        return keybinds.stream().filter(kb -> kb.prefsName.equals(formattedKey)).findFirst().orElse(null);
    }

    /**
     * @param key the name of the key to check, this can be {@link Keybind#prefsName} or {@link Keybind#readableName}
     * @return the name of the first key associated with the action
     */
    public static String getKeybindKeyString(String key) {
        return Input.Keys.toString(getKeybind(key).keys.getFirst());
    }

    /**
     * Clear {@link Keybind#keybinds} & all {@link KeybindType#keybinds}
     */
    public static void clear() {
        keybinds.clear();
        Arrays.stream(KeybindType.values()).forEach(t -> t.keybinds.clear());
    }

    @Override
    public String toString() {
        return readableName + " " + type + " " + keys + " " + getReadableKeys();
    }
}

