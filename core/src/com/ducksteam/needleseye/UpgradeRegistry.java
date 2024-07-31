package com.ducksteam.needleseye;

import com.ducksteam.needleseye.player.Upgrade;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class UpgradeRegistry {

    public static boolean iconsLoaded = false;

    public static HashMap<String,Class<?extends Upgrade>> registeredUpgrades = new HashMap<>();

    public static void registerUpgrade(String id,Class<?extends Upgrade> upgradeClass) {
        registeredUpgrades.put(id,upgradeClass);
    }

    public static Upgrade getUpgradeInstance(Class<?extends Upgrade> upgradeClass){
        if(registeredUpgrades.containsValue(upgradeClass)){
            try {
                return upgradeClass.getDeclaredConstructor(Upgrade.class).newInstance(new Upgrade());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

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

    public boolean isUpgradeRegistered(String id) {
        return registeredUpgrades.containsKey(id);
    }

}
