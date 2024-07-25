package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.entity.enemies.WormEnemy;

import java.util.HashMap;

public class EnemyRegistry {
    public static HashMap<String, Class<? extends EnemyEntity>> registeredEnemies = new HashMap<>();

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
}
