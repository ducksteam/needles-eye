package com.ducksteam.needleseye.lwjgl3;

import com.ducksteam.needleseye.SplashWorker;

import java.awt.*;

public class LwjglSplashWorker implements SplashWorker {
    @Override
    public void closeSplashScreen() {
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            splash.close();
        }
    }
}
