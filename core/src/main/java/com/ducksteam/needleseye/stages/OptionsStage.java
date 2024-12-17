package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ducksteam.needleseye.Main;

import java.util.ArrayList;

/**
 * The stage for options.
 */
public class OptionsStage extends StageTemplate {

    private enum OptionCategory {
        GENERAL,
        VIDEO,
        AUDIO,
        CONTROLS;

        ScrollPane pane;
        TextButton button;
    }

    Image background;

    /*
     * General:
     *  - Seed (text field)
     * Video:
     *  - Resolution (dropdown)
     *  - Fullscreen toggle (dropdown)
     *  - Vsync (checkbox)
     *  - Brightness? (slider)
     * Audio:
     *  - Music volume (slider)
     *  - Sfx volume (slider)
     *  - Output device (dropdown)
     * Controls:
     *  - change controls
     */

    // Switching panel resources

    Table categories;
    OptionCategory currentCategory = OptionCategory.GENERAL;

    TextButton applyButton;
    TextButton exitButton;

    public OptionsStage() {
        super();
    }

    @Override
    public void build() {
        super.build();

        if (currentCategory == null) {
            currentCategory = OptionCategory.GENERAL;
        }

        background = new Image(new Texture("ui/instructions/background.png"));

        categories = new Table();

        OptionCategory.GENERAL.button = new TextButton("General", buttonStyle);
        OptionCategory.VIDEO.button = new TextButton("Video", buttonStyle);
        OptionCategory.AUDIO.button = new TextButton("Audio", buttonStyle);
        OptionCategory.CONTROLS.button = new TextButton("Controls", buttonStyle);
        applyButton = new TextButton("Apply", buttonStyle);
        exitButton = new TextButton("Exit", buttonStyle);

        for (OptionCategory c : OptionCategory.values()) {
            c.button.setChecked(currentCategory == c);
        }

        rebuild();
        isBuilt = true;
    }

    @Override
    public void rebuild() {
        super.rebuild(); // clear root table
        clear(); // clear stage

        // Update the viewport
        this.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // clear sub-tables
        categories.clear();

        // background is not in the table because it should be behind everything
        background.setBounds(0, 0, getWidth(), getHeight());
        addActor(background);

        root.setFillParent(true);
        addActor(root);

        for (OptionCategory category : OptionCategory.values()) {
            category.button.setProgrammaticChangeEvents(false);
            category.button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    currentCategory = category;
                    rebuild();
                    for (OptionCategory c : OptionCategory.values()) {
                        c.button.setChecked(currentCategory == c);
                    }
                }
            });
        }
        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyButton.setChecked(false);
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.gameState = Main.GameState.MAIN_MENU;
            }
        });


        alignButton(OptionCategory.GENERAL.button, OptionCategory.VIDEO.button, OptionCategory.AUDIO.button, OptionCategory.CONTROLS.button, applyButton, exitButton);

        for (OptionCategory category : OptionCategory.values()) {
            categories.add(category.button).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();
        }
        categories.add(applyButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();
        categories.add(exitButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();

        root.left();
        root.add(categories).padLeft(Value.percentWidth(0.1f, background)).padTop(Value.percentHeight(0.07f, background)).padBottom(Value.percentHeight(0.07f, background)).left();

        switch (currentCategory) {
            case GENERAL -> buildGeneralPane();
            case VIDEO -> buildVideoPane();
            case AUDIO -> buildAudioPane();
            case CONTROLS -> buildControlsPane();
        }

        root.add(currentCategory.pane).padLeft(Value.percentWidth(0.1f, background)).prefSize(Value.percentWidth(0.5f, background), Value.percentHeight(790f/1080, background)).maxHeight(Value.percentHeight(790f/1080, background)).left();

        setScrollFocus(currentCategory.pane);
    }

    private void buildGeneralPane(){
        Table pane = new Table();

        TextField seedInputField = new TextField("", textFieldStyle);

        Label seedLabel = new Label("Seed", labelStyle);

        pane.add(seedLabel).pad(Value.percentWidth(0.04f, pane)).left();
        pane.add(seedInputField).padRight(Value.percentWidth(0.04f, pane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        OptionCategory.GENERAL.pane = new ScrollPane(pane, scrollStyle);
        OptionCategory.GENERAL.pane.setFlingTime(0);
        OptionCategory.GENERAL.pane.setFlickScroll(false);
        OptionCategory.GENERAL.pane.setScrollingDisabled(true, false);
    }

    private void buildVideoPane(){
        Table pane = new Table();

        SelectBox<Resolution> resolutionDropdown = new SelectBox<>(selectBoxStyle);
        resolutionDropdown.setItems(Resolution.getMatchingResolutions(Main.maxResolution));
        resolutionDropdown.setMaxListCount(4);

        Label resolutionLabel = new Label("Resolution", labelStyle);

        pane.add(resolutionLabel).pad(Value.percentWidth(0.04f, pane)).left();
        pane.add(resolutionDropdown).padRight(Value.percentWidth(0.04f, pane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        SelectBox<WindowType> windowTypeDropdown = new SelectBox<>(selectBoxStyle);
        windowTypeDropdown.setItems(WindowType.values());
        windowTypeDropdown.setMaxListCount(3);

        Label windowTypeLabel = new Label("Window Type", labelStyle);

        pane.add(windowTypeLabel).pad(Value.percentWidth(0.04f, pane)).left();
        pane.add(windowTypeDropdown).padRight(Value.percentWidth(0.04f, pane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        ImageButton vSyncCheckbox = new ImageButton(checkboxStyle);
        Label vSyncLabel = new Label("VSync", labelStyle);

        pane.add(vSyncLabel).pad(Value.percentWidth(0.04f, pane)).left();
        pane.add(vSyncCheckbox).padRight(Value.percentWidth(0.04f, pane)).left().maxSize(Value.percentHeight(1, windowTypeDropdown)).row();

        OptionCategory.VIDEO.pane = new ScrollPane(pane, scrollStyle);
    }


    private void buildAudioPane() {
        Table pane = new Table();



        OptionCategory.AUDIO.pane = new ScrollPane(pane, scrollStyle);
    }

    private void buildControlsPane(){
        Table pane = new Table();



        OptionCategory.CONTROLS.pane = new ScrollPane(pane, scrollStyle);
    }

    public static class Resolution {
        public int width;
        public int height;

        static ArrayList<Resolution> resolutions = new ArrayList<>();

        static {
            resolutions.add(new Resolution(3840, 2160)); // 4k
            resolutions.add(new Resolution(3200, 1800)); // qhd+
            resolutions.add(new Resolution(2560, 1440)); // qhd
            resolutions.add(new Resolution(1920, 1080)); // full hd
            resolutions.add(new Resolution(1600, 900)); // hd+
            resolutions.add(new Resolution(1280, 720)); // hd
            resolutions.add(new Resolution(1024, 576)); // wsvga
            resolutions.add(new Resolution(960, 540)); // qHD
            resolutions.add(new Resolution(848, 480)); // FWVGA
            resolutions.add(new Resolution(640, 360)); // nHD
        }

        public Resolution(int width, int height) {
            if (width <= 0 || height <= 0) throw new IllegalArgumentException("Resolution width and height must be positive");
            if (width/16*9 != height) System.err.println("Resolution may not be correct aspect ratio: " + width + "x" + height);
            this.width = width;
            this.height = height;
            resolutions.add(this);
        }

        public Resolution(Graphics.DisplayMode displayMode) {
            this(displayMode.width, displayMode.height);
        }

        public static Array<Resolution> getMatchingResolutions(Resolution maxRes) {
            Array<Resolution> matchingResolutions = new Array<>();
            for (Resolution resolution : resolutions) {
                if (!matchingResolutions.contains(resolution, false) && maxRes.width >= resolution.width && maxRes.height >= resolution.height) {
                    matchingResolutions.add(resolution);
                }
            }
            return matchingResolutions;
        }

        @Override
        public String toString() {
            return width + "x" + height;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Resolution)) return false;
            return ((Resolution) o).width == this.width && ((Resolution) o).height == this.height;
        }
    }

    public enum WindowType {
        WINDOWED,
        FULLSCREEN,
        BORDERLESS;

        @Override
        public String toString() {
            return switch (this) {
                case WINDOWED -> "Windowed";
                case FULLSCREEN -> "Fullscreen";
                case BORDERLESS -> "Borderless Fullscreen";
            };
        }
    }
}
