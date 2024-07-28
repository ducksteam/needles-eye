package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.entity.enemies.WormEnemy;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.HashMap;

public class EnemyRegistry {
    public static HashMap<String, Class<? extends EnemyEntity>> registeredEnemies = new HashMap<>();

    public static HashMap<Class<? extends EnemyEntity>, ModelInstance> enemyModelInstances = new HashMap<>();

    public static void registerEnemy(String id, Class<? extends EnemyEntity> enemyClass) {
        registeredEnemies.put(id, enemyClass);
    }

    public static EnemyEntity getEnemyInstance(Class<? extends EnemyEntity> enemyClass) {
        if (registeredEnemies.containsValue(enemyClass)) {
            try {
                return enemyClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                Gdx.app.error("EnemyRegistry", "Error creating enemy instance", e);
            }
        }
        return null;
    }

    public static EnemyEntity getEnemyInstance(String id) {
        if (registeredEnemies.containsKey(id)) {
            return getEnemyInstance(registeredEnemies.get(id));
        }
        return null;
    }

    public static void initEnemies(){
        registerEnemy("test", WormEnemy.class);
    }

    public static void loadEnemyAssets(AssetManager assetManager) {
        for (Class<? extends EnemyEntity> enemyClass : registeredEnemies.values()) {
            try {
                String address = enemyClass.getDeclaredConstructor().newInstance().getModelAddress();
                assetManager.setLoader(SceneAsset.class,".gltf",new GLTFAssetLoader());
                assetManager.load(address, SceneAsset.class);
                assetManager.finishLoadingAsset(address);
                enemyModelInstances.put(enemyClass, new ModelInstance(new ModelInstance(new ModelInstance(((SceneAsset)assetManager.get(address)).scene.model))));
            } catch (Exception e){
                Gdx.app.error("EnemyRegistry", "Error loading enemy assets", e);
            }
        }
    }
}
