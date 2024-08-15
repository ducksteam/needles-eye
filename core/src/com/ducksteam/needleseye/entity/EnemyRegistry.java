package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.entity.enemies.WormEnemy;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all enemy types in the game
 * */
public class EnemyRegistry {
    //Utility data for enemy registration
    public static HashMap<String, Class<? extends EnemyEntity>> registeredEnemies = new HashMap<>();
    public static boolean loaded = false;
    public static HashMap<Class<? extends EnemyEntity>, ModelInstance> enemyModelInstances = new HashMap<>();

    /**
     * Register an enemy type
     * @param id The id of the enemy type
     * @param enemyClass The class of the enemy type
     * */
    public static void registerEnemy(String id, Class<? extends EnemyEntity> enemyClass) {
        registeredEnemies.put(id, enemyClass);
    }

    /**
     * Returns an instance of an enemy type
     * @param enemyClass The class of the enemy type
     * @param pos The position of the enemy
     * @param rot The rotation of the enemy
     * @param room The room the enemy is in
     * @return An instance of the enemy type
     * */
    public static EnemyEntity getNewEnemyInstance(Class<? extends EnemyEntity> enemyClass, Vector3 pos, Quaternion rot, RoomInstance room) {
        if (registeredEnemies.containsValue(enemyClass)) {
            try {
                return enemyClass.getDeclaredConstructor(Vector3.class,Quaternion.class,RoomInstance.class).newInstance(pos,rot,room);
            } catch (Exception e) {
                Gdx.app.error("EnemyRegistry", "Error creating enemy instance", e);
            }
        }
        return null;
    }

    /**
     * Returns an instance of an enemy type
     * @param id The String id of the enemy type
     * @param pos The position of the enemy
     * @param rot The rotation of the enemy
     * @param room The room the enemy is in
     * @return An instance of the enemy type
     * */
    public static EnemyEntity getNewEnemyInstance(String id, Vector3 pos, Quaternion rot, RoomInstance room) {
        if (registeredEnemies.containsKey(id)) {
            return getNewEnemyInstance(registeredEnemies.get(id),pos,rot,room);
        }
        return null;
    }

    /**
     * Enacts all actions to initialise enemies
     * */
    public static void initEnemies(){
        registerEnemy(WormEnemy.modelAddress, WormEnemy.class);
    }

    /**
     * Loads all enemy assets
     * @param assetManager The asset manager to load the assets with
     * */
    public static void loadEnemyAssets(AssetManager assetManager) {
        for (Map.Entry<String, Class<? extends EnemyEntity>> enemyEntry : registeredEnemies.entrySet()) {
            try {
                Class<? extends EnemyEntity> enemyClass = enemyEntry.getValue();
                String address = enemyEntry.getKey();
                assetManager.setLoader(SceneAsset.class,".gltf",new GLTFAssetLoader());
                assetManager.load(address, SceneAsset.class);
            } catch (Exception e){
                Gdx.app.error("EnemyRegistry", "Error loading enemy assets", e);
            }
        }
        loaded = true;
        Gdx.app.debug("EnemyRegistry", "Loaded enemy assets");
    }

    /**
     * Does all post loading actions for enemy assets
     * @param assetManager The asset manager to load the assets with
     * */
    public static void postLoadEnemyAssets(AssetManager assetManager) {
        for (Map.Entry<String, Class<? extends EnemyEntity>> enemyEntry : registeredEnemies.entrySet()) {
            try {
                Class<? extends EnemyEntity> enemyClass = enemyEntry.getValue();
                String address = enemyEntry.getKey();
                enemyModelInstances.put(enemyClass, new ModelInstance(new ModelInstance(new ModelInstance(((SceneAsset)assetManager.get(address)).scene.model))));
            } catch (Exception e){
                Gdx.app.error("EnemyRegistry", "Error loading enemy assets", e);
            }
        }
        loaded = true;
        Gdx.app.debug("EnemyRegistry", "Loaded enemy assets");
    }
}
