package com.ducksteam.needleseye;

import com.ducksteam.needleseye.player.Upgrade;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class UpgradeRegistry {

    public static HashMap<String,Class<?extends Upgrade>> registeredUpgrades = new HashMap<String,Class<?extends Upgrade>>();

    public static void registerUpgrade(String id,Class<?extends Upgrade> upgradeClass) {
        registeredUpgrades.put(id,upgradeClass);
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
