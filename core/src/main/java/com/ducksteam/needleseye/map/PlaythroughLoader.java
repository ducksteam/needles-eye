package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlaythroughLoader {

    static Json json;
    static String localRoot;
    static DateFormat dateFormat;
    /**
     * Prepares the PlaythroughLoader for use.
     */
    public static void initialisePlaythroughLoader() {
        json = new Json();
        localRoot = Gdx.files.getLocalStoragePath();
    }

    /**
     * Loads a playthrough from a file.
     * @param path The path to the file to load the playthrough from.
     * @return The loaded playthrough.
     * */
    public static Playthrough loadPlaythrough(String path) {
        Playthrough playthrough;
        String data = Gdx.files.local(path).readString();
        playthrough = json.fromJson(Playthrough.class, data);
        playthrough.fromSave = true;
        return playthrough;
    }

    /**
     * Saves a playthrough to a file.
     * @param playthrough The playthrough to save.
     * @param path The path to save the playthrough to.
     * @throws RuntimeException If the playthrough cannot be saved.
     * */
    public static void savePlaythrough(Playthrough playthrough, String path) throws RuntimeException {
        String data;
        data = json.prettyPrint(playthrough);
        if (Gdx.files.local(path).exists()) path = path + getDateFormat().format(new Date());
        Gdx.files.local(path).writeString(data, false);
    }

    public static DateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        }
        return dateFormat;
    }
}
