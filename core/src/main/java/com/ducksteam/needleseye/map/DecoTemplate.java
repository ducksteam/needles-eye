package com.ducksteam.needleseye.map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.ducksteam.needleseye.Main;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;
import java.util.HashSet;

public class DecoTemplate {
    private String modelPath;
    private String name;
    private boolean destructible;
    public HashSet<DecoTag> tags;

    /**
     * Creates an empty deco template to be filled with setters
     */
    public DecoTemplate() {}

    /**
     * Loads deco templates from JSON
     * @param map the JSON array to read from
     * @return the parsed templates
     */
    public static ArrayList<DecoTemplate> loadDecoTemplates(Array<JsonValue> map){
        ArrayList<DecoTemplate> dtArray = new ArrayList<>();

        for (JsonValue v : map){
            DecoTemplate dt = new DecoTemplate();
            dt.modelPath = v.get("modelPath").asString();
            dt.name = v.get("name").asString();
            dt.destructible = v.get("destructible").asBoolean();
            HashSet<DecoTag> tags = new HashSet<>();
            for (JsonValue t : v.get("tags")){
                tags.add(DecoTag.valueOf(t.asString()));
            }
            dt.tags = tags;

            dtArray.add(dt);
        }

        return dtArray;
    }

    public String getModelPath() {
        return modelPath;
    }

    public String getName() {
        return name;
    }

    public boolean isDestructible() {
        return destructible;
    }

    /**
     * Get the scene of the deco
     * @return a new copy of the scene
     */
    public Scene getScene() {
        if (getModelPath() != null) return new Scene(((SceneAsset) Main.assMan.get(getModelPath())).scene);
        else return null;
    }
}
