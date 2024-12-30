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

    Table generalPane;
    TextField seedInputField;
    Label seedLabel;

    private void buildGeneralPane(){
        generalPane = new Table();

        seedInputField = new TextField("", textFieldStyle);

        seedInputField.setTextFieldListener((textField, c) -> tempSeed = textField.getText());

        seedLabel = new Label("Seed", labelStyle);

        generalPane.add(seedLabel).pad(Value.percentWidth(0.04f, generalPane)).left();
        generalPane.add(seedInputField).padRight(Value.percentWidth(0.04f, generalPane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        OptionCategory.GENERAL.pane = new ScrollPane(generalPane, scrollStyle);
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

    Table videoPane;
    SelectBox<Config.Resolution> resolutionDropdown;
    Label resolutionLabel;
    SelectBox<String> windowTypeDropdown;
    Label windowTypeLabel;
    ImageButton vSyncCheckbox;
    Label vSyncLabel;
    Slider brightnessSlider;
    Label brightnessLabel;
    Label brightnessValueLabel;
    Table brightnessSliderTable;

    private void buildVideoPane(){
        videoPane = new Table();

        resolutionDropdown = new SelectBox<>(selectBoxStyle);
        resolutionDropdown.setItems(Config.Resolution.getMatchingResolutions(Main.maxResolution).toArray(new Config.Resolution[0]));
        resolutionDropdown.setMaxListCount(4);

        if (Config.prefs.getString("WindowType").equalsIgnoreCase("fullscreen")){
            resolutionDropdown.setSelected(Config.getFullscreenResolution());
        } else {
            resolutionDropdown.setSelected(new Config.Resolution(Config.prefs.getString("Resolution", "")));
        }

        resolutionDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.setResolution(resolutionDropdown.getSelected());
            }
        });

        resolutionLabel = new Label("Resolution", labelStyle);

        videoPane.add(resolutionLabel).pad(Value.percentWidth(0.04f, videoPane)).left();
        videoPane.add(resolutionDropdown).padRight(Value.percentWidth(0.04f, videoPane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        windowTypeDropdown = new SelectBox<>(selectBoxStyle);
        windowTypeDropdown.setItems(Config.WindowType.getUserStrings());
        windowTypeDropdown.setMaxListCount(3);

        windowTypeDropdown.setSelected(Config.WindowType.valueOf(Config.prefs.getString("WindowType", "")).getUserString());

        windowTypeDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.setWindowType(Config.WindowType.valueOf(windowTypeDropdown.getSelected().toUpperCase()));
                Config.setResolution(Config.getFullscreenResolution());
            }
        });

        resolutionDropdown.setDisabled(windowTypeDropdown.getSelected().equals(Config.WindowType.FULLSCREEN.getUserString()));

        windowTypeLabel = new Label("Window Type", labelStyle);

        videoPane.add(windowTypeLabel).pad(Value.percentWidth(0.04f, videoPane)).left();
        videoPane.add(windowTypeDropdown).padRight(Value.percentWidth(0.04f, videoPane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        vSyncCheckbox = new ImageButton(checkboxStyle);
        vSyncLabel = new Label("VSync", labelStyle);

        videoPane.add(vSyncLabel).pad(Value.percentWidth(0.04f, videoPane)).left();
        videoPane.add(vSyncCheckbox).padRight(Value.percentWidth(0.04f, videoPane)).left().prefSize(Value.percentHeight(0.8f, windowTypeDropdown)).row();

        vSyncCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.setvSync(vSyncCheckbox.isChecked());
            }
        });

        brightnessSlider = new Slider(0, 100, 1, false, sliderStyle);
        brightnessLabel = new Label("Brightness", labelStyle);
        brightnessValueLabel = new Label((int)brightnessSlider.getValue() + "%", labelStyle);

        brightnessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                brightnessValueLabel.setText((int) brightnessSlider.getValue() + "%");
                Config.brightness = (int) brightnessSlider.getValue();
            }
        });

        brightnessSlider.setValue(Config.prefs.getInteger("Brightness", 50));

        videoPane.add(brightnessLabel).pad(Value.percentWidth(0.04f, videoPane)).left();
        brightnessSliderTable = new Table();
        brightnessSliderTable.add(brightnessSlider).padLeft(sliderStyle.background.getLeftWidth()).padRight(Value.percentWidth(0.04f, videoPane)).minSize(Value.percentWidth(0.5f, videoPane), Value.percentHeight(0.7f, brightnessLabel)).left().growX();
        brightnessSliderTable.add(brightnessValueLabel).padLeft(Value.percentWidth(0.02f, brightnessSliderTable)).row();
        videoPane.add(brightnessSliderTable).padRight(Value.percentWidth(0.04f, videoPane)).maxWidth(Value.percentWidth(0.75f, videoPane)).left().row();

        OptionCategory.VIDEO.pane = new ScrollPane(videoPane, scrollStyle);
        OptionCategory.VIDEO.pane.setFlingTime(0);
        OptionCategory.VIDEO.pane.setFlickScroll(false);
        OptionCategory.VIDEO.pane.setScrollingDisabled(true, false);
    }

    Table audioPane;

    private void buildAudioPane() {
        audioPane = new Table();



        OptionCategory.AUDIO.pane = new ScrollPane(audioPane, scrollStyle);
    }

    Table controlsPane;

    private void buildControlsPane(){
        controlsPane = new Table();



        OptionCategory.CONTROLS.pane = new ScrollPane(controlsPane, scrollStyle);
    }

}
