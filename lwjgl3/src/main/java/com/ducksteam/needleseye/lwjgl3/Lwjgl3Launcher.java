package com.ducksteam.needleseye.lwjgl3;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static void createApplication() {
        Main main = new Main();
        Main.maxResolution = new Config.Resolution(Lwjgl3ApplicationConfiguration.getDisplayMode());
        main.setSplashWorker(new LwjglSplashWorker());
        new Lwjgl3Application(main, getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        /*//// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);*/
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        System.out.println("[Monitor info] " + Lwjgl3ApplicationConfiguration.getMonitors().length + " monitors");
        for (Graphics.Monitor monitor : Lwjgl3ApplicationConfiguration.getMonitors())
            for (Graphics.DisplayMode mode : Lwjgl3ApplicationConfiguration.getDisplayModes(monitor))
                System.out.println("["+monitor.name + "] " + mode);

        configuration.setTitle("The Needle's Eye"); // window title

        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        System.out.println("[Selected DisplayMode] "+Lwjgl3ApplicationConfiguration.getDisplayMode());

        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2);

        configuration.setDecorated(true); // enable OS window options

        configuration.useVsync(false); // disable vsync

        configuration.disableAudio(true);

        configuration.setResizable(false); // disable user drag resizing

        configuration.setHdpiMode(HdpiMode.Pixels); // use actual pixels for resolution

        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 8, 4);

        configuration.setWindowIcon("icon_128.png", "icon_64.png", "icon_32.png", "icon_16.png"); // application icon

        return configuration;
    }
}
