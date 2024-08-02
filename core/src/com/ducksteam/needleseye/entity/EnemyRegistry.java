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

public class EnemyRegistry {
    public static HashMap<String, Class<? extends EnemyEntity>> registeredEnemies = new HashMap<>();

    public static HashMap<Class<? extends EnemyEntity>, ModelInstance> enemyModelInstances = new HashMap<>();

    public static void registerEnemy(String id, Class<? extends EnemyEntity> enemyClass) {
        registeredEnemies.put(id, enemyClass);
    }

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

    public static EnemyEntity getNewEnemyInstance(String id, Vector3 pos, Quaternion rot, RoomInstance room) {
        if (registeredEnemies.containsKey(id)) {
            return getNewEnemyInstance(registeredEnemies.get(id),pos,rot,room);
        }
        return null;
    }

    public static void initEnemies(){
        registerEnemy(WormEnemy.modelAddress, WormEnemy.class);
    }

    public static void loadEnemyAssets(AssetManager assetManager) {
        for (Map.Entry<String, Class<? extends EnemyEntity>> enemyEntry : registeredEnemies.entrySet()) {
            try {
                Class<? extends EnemyEntity> enemyClass = enemyEntry.getValue();
                String address = enemyEntry.getKey();
                assetManager.setLoader(SceneAsset.class,".gltf",new GLTFAssetLoader());
                assetManager.load(address, SceneAsset.class);
                assetManager.finishLoadingAsset(address);
                enemyModelInstances.put(enemyClass, new ModelInstance(new ModelInstance(new ModelInstance(((SceneAsset)assetManager.get(address)).scene.model))));
            } catch (Exception e){
                Gdx.app.error("EnemyRegistry", "Error loading enemy assets", e);
            }
        }
        Gdx.app.debug("EnemyRegistry", "Loaded enemy assets");
    }
}
