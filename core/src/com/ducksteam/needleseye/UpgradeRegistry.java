package com.ducksteam.needleseye;

import com.badlogic.gdx.Gdx;
import com.ducksteam.needleseye.player.Upgrade;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Registry class for upgrades to track instantiation and prevent double ups
 * @author thechiefpotatopeeler
 * */
public class UpgradeRegistry {
    //Utilities for tracking upgrades
    public static boolean iconsLoaded = false;
    public static HashMap<String,Class<?extends Upgrade>> registeredUpgrades = new HashMap<>();

    /**
     * Registers an upgrade
     * @param id the String id of the upgrade
     * @param upgradeClass the class of the upgrade
     * */
    public static void registerUpgrade(String id,Class<?extends Upgrade> upgradeClass) {
        registeredUpgrades.put(id,upgradeClass);
    }
    /**
     * Gets an instance of an upgrade with the given class
     * @return an instance of the upgrade with the given class
     * @param upgradeClass the class of the upgrade to instantiate
     * */
    public static Upgrade getUpgradeInstance(Class<?extends Upgrade> upgradeClass){
        if(registeredUpgrades.containsValue(upgradeClass)){
            try {
                return upgradeClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * Gets an instance of an upgrade with the given class
     * @return an instance of the upgrade with the given class
     * @param id the String id of the upgrade to instantiate
     * */
    public static Upgrade getUpgradeInstance(String id) {
        try {
            return registeredUpgrades.get(id).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Gives a random upgrade
     * @return a random upgrade
     * */
    public static Upgrade getRandomUpgrade() {
        int random = (int) (Math.random() * registeredUpgrades.size());
        Upgrade upgrade = getUpgradeInstance((Class<? extends Upgrade>) registeredUpgrades.values().toArray()[random]);
        if(upgrade == null || upgrade.isBaseUpgrade) return getRandomUpgrade();
        return getUpgradeInstance(upgrade.getClass());
    }

    /**
     * Use to validate that an upgrade is registered by id
     * @param id the id of the upgrade
     * */
    public boolean isUpgradeRegistered(String id) {
        return registeredUpgrades.containsKey(id);
    }

}
