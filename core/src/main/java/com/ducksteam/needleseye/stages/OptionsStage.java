package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.map.MapManager;

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

    /** Where the seed in general pane is stored before the apply button is pressed */
    String tempSeed;

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
                Config.flushPrefs();
                applySeed();
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

        seedInputField.setTextFieldListener((textField, c) -> tempSeed = textField.getText());

        Label seedLabel = new Label("Seed", labelStyle);

        pane.add(seedLabel).pad(Value.percentWidth(0.04f, pane)).left();
        pane.add(seedInputField).padRight(Value.percentWidth(0.04f, pane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        OptionCategory.GENERAL.pane = new ScrollPane(pane, scrollStyle);
        OptionCategory.GENERAL.pane.setFlingTime(0);
        OptionCategory.GENERAL.pane.setFlickScroll(false);
        OptionCategory.GENERAL.pane.setScrollingDisabled(true, false);
    }

    /**
     * Determines type of the inputted seed value, and sends it to MapMan
     */
    private void applySeed(){
        try {
            MapManager.setSeed(Long.parseLong(tempSeed));
        } catch (NumberFormatException e) {
            MapManager.setSeed(tempSeed);
        }
    }

    private void buildVideoPane(){
        Table pane = new Table();

        SelectBox<Config.Resolution> resolutionDropdown = new SelectBox<>(selectBoxStyle);
        resolutionDropdown.setItems(Config.Resolution.getMatchingResolutions(Main.maxResolution).toArray(new Config.Resolution[0]));
        resolutionDropdown.setMaxListCount(4);

        resolutionDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.setResolution(resolutionDropdown.getSelected());
            }
        });

        Label resolutionLabel = new Label("Resolution", labelStyle);

        pane.add(resolutionLabel).pad(Value.percentWidth(0.04f, pane)).left();
        pane.add(resolutionDropdown).padRight(Value.percentWidth(0.04f, pane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        SelectBox<String> windowTypeDropdown = new SelectBox<>(selectBoxStyle);
        windowTypeDropdown.setItems(Config.WindowType.getUserStrings());
        windowTypeDropdown.setMaxListCount(3);

        windowTypeDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.setWindowType(Config.WindowType.valueOf(windowTypeDropdown.getSelected().toUpperCase()));
            }
        });

        Label windowTypeLabel = new Label("Window Type", labelStyle);

        pane.add(windowTypeLabel).pad(Value.percentWidth(0.04f, pane)).left();
        pane.add(windowTypeDropdown).padRight(Value.percentWidth(0.04f, pane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        ImageButton vSyncCheckbox = new ImageButton(checkboxStyle);
        Label vSyncLabel = new Label("VSync", labelStyle);

        pane.add(vSyncLabel).pad(Value.percentWidth(0.04f, pane)).left();
        pane.add(vSyncCheckbox).padRight(Value.percentWidth(0.04f, pane)).left().prefSize(Value.percentHeight(0.8f, windowTypeDropdown)).row();

        vSyncCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.setvSync(vSyncCheckbox.isChecked());
            }
        });

        Slider brightnessSlider = new Slider(0, 100, 1, false, sliderStyle);
        Label brightnessLabel = new Label("Brightness", labelStyle);
        Label brightnessValueLabel = new Label((int)brightnessSlider.getValue() + "%", labelStyle);

        brightnessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                brightnessValueLabel.setText((int) brightnessSlider.getValue() + "%");
                Config.brightness =  (int) brightnessSlider.getValue();
            }
        });

        pane.add(brightnessLabel).pad(Value.percentWidth(0.04f, pane)).left();
        Table sliderTable = new Table();
        sliderTable.add(brightnessSlider).padLeft(sliderStyle.background.getLeftWidth()).padRight(Value.percentWidth(0.04f, pane)).minSize(Value.percentWidth(0.5f, pane), Value.percentHeight(0.7f, brightnessLabel)).left().growX();
        sliderTable.add(brightnessValueLabel).padLeft(Value.percentWidth(0.02f, sliderTable)).row();
        pane.add(sliderTable).padRight(Value.percentWidth(0.04f, pane)).maxWidth(Value.percentWidth(0.75f, pane)).left().row();

        OptionCategory.VIDEO.pane = new ScrollPane(pane, scrollStyle);
        OptionCategory.VIDEO.pane.setFlingTime(0);
        OptionCategory.VIDEO.pane.setFlickScroll(false);
        OptionCategory.VIDEO.pane.setScrollingDisabled(true, false);
    }


    private void buildAudioPane() {
        Table pane = new Table();



        OptionCategory.AUDIO.pane = new ScrollPane(pane, scrollStyle);
    }

    private void buildControlsPane(){
        Table pane = new Table();



        OptionCategory.CONTROLS.pane = new ScrollPane(pane, scrollStyle);
    }

}
