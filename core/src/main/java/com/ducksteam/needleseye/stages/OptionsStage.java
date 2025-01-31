package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Keybind;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.map.MapManager;
import de.pottgames.tuningfork.AudioDevice;

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

    /** Used when adding new keybinds */
    Keybind activeKeybind;

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
    Dialog dialog;

    boolean changes = false;

    ChangeListener popupDetector;

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

        dialog = new Dialog("", windowStyle) {
            @Override
            protected void result(Object object) {
                if ((boolean) object) { // apply
                    applyButton.setChecked(false);
                    if (currentCategory != OptionCategory.CONTROLS) activeKeybind = null; // deactivate keyboard listening
                    Config.flushPrefs();
                    applySeed();
                    Main.gameState = Main.GameState.MAIN_MENU;
                    Config.init();
                } else { // exit
                    exitButton.setChecked(false);
                    Main.gameState = Main.GameState.MAIN_MENU;
                    if (currentCategory != OptionCategory.CONTROLS) activeKeybind = null; // deactivate keyboard listening
                    Config.init();
                }
                changes = false;
            }
        };
        dialog.text("You have unsaved changes that will be overridden.", labelStyle);
        dialog.button("Apply", true, compactButtonStyle);
        dialog.button("Exit", false, compactButtonStyle);

        dialog.getContentTable().pad(Value.percentWidth(0.005f, background));
        dialog.getButtonTable().getCells().forEach(cell -> cell.space(Value.percentWidth(0.01f, background)));
        dialog.getButtonTable().padBottom(Value.percentWidth(0.005f, background));

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
                    if (currentCategory != OptionCategory.CONTROLS) activeKeybind = null; // deactivate keyboard listening
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
                if (currentCategory != OptionCategory.CONTROLS) activeKeybind = null; // deactivate keyboard listening
                Config.flushPrefs();
                changes = false;
                applySeed();
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitButton.setChecked(false);
                if (changes) {
                    dialog.show(actor.getStage());
                } else {
                    Main.gameState = Main.GameState.MAIN_MENU;
                    if (currentCategory != OptionCategory.CONTROLS) activeKeybind = null; // deactivate keyboard listening
                    Config.init();
                }
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
    Slider sensitivitySlider;
    Label sensitivityLabel;
    Label sensitivityValueLabel;
    Table sensitivitySliderTable;

    private void buildGeneralPane(){
        generalPane = new Table();

        seedInputField = new TextField("", textFieldStyle);

        seedInputField.setTextFieldListener((textField, c) -> tempSeed = textField.getText());

        seedLabel = new Label("Seed", labelStyle);

        addPopupDetector(seedInputField);

        generalPane.add(seedLabel).pad(Value.percentWidth(0.04f, generalPane)).left();
        generalPane.add(seedInputField).padRight(Value.percentWidth(0.04f, generalPane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        sensitivitySlider = new Slider(1, 200, 1, false, sliderStyle);
        sensitivityLabel = new Label("Mouse Sensitivity", labelStyle);
        sensitivityValueLabel = new Label((int) sensitivitySlider.getValue() + "%", labelStyle);

        sensitivitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityValueLabel.setText((int) sensitivitySlider.getValue() + "%");
                Config.sensitivity = (int) sensitivitySlider.getValue();
            }
        });

        sensitivitySlider.setValue(Config.prefs.getInteger("MouseSpeed", 100));

        generalPane.add(sensitivityLabel).pad(Value.percentWidth(0.04f, generalPane)).left();
        sensitivitySliderTable = new Table();
        sensitivitySliderTable.add(sensitivitySlider).padLeft(sliderStyle.background.getLeftWidth()).maxSize(Value.percentWidth(0.5f, generalPane), Value.percentHeight(0.7f, sensitivityLabel)).left().growX();
        sensitivitySliderTable.add(sensitivityValueLabel).row();
        generalPane.add(sensitivitySliderTable).padRight(Value.percentWidth(0.04f, generalPane)).prefWidth(Value.percentWidth(0.75f, generalPane)).left().row();

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

        vSyncCheckbox.setChecked(Config.prefs.getBoolean("VSync", true));

        vSyncCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.setvSync(vSyncCheckbox.isChecked());
            }
        });

        brightnessSlider = new Slider(0, 100, 1, false, sliderStyle);
        brightnessLabel = new Label("Brightness", labelStyle);
        brightnessValueLabel = new Label((int) brightnessSlider.getValue() + "%", labelStyle);

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
        brightnessSliderTable.add(brightnessSlider).padLeft(sliderStyle.background.getLeftWidth()).maxSize(Value.percentWidth(0.5f, videoPane), Value.percentHeight(0.7f, brightnessLabel)).left().growX();
        brightnessSliderTable.add(brightnessValueLabel).row();
        videoPane.add(brightnessSliderTable).padRight(Value.percentWidth(0.04f, videoPane)).prefWidth(Value.percentWidth(0.75f, videoPane)).left().row();

        addPopupDetector(resolutionDropdown, windowTypeDropdown, vSyncCheckbox, brightnessSlider);

        OptionCategory.VIDEO.pane = new ScrollPane(videoPane, scrollStyle);
        OptionCategory.VIDEO.pane.setFlingTime(0);
        OptionCategory.VIDEO.pane.setFlickScroll(false);
        OptionCategory.VIDEO.pane.setScrollingDisabled(true, false);
    }

    Table audioPane;

    private void buildAudioPane() {
        audioPane = new Table();

        Slider musicVolumeSlider = new Slider(0, 100, 1, false, sliderStyle);
        musicVolumeSlider.setValue(Config.musicVolume);

        Label musicVolumeLabel = new Label("Music Volume", labelStyle);
        Label musicVolumeValueLabel = new Label((int)musicVolumeSlider.getValue() + "%", labelStyle);

        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicVolumeValueLabel.setText((int) musicVolumeSlider.getValue() + "%");
                Config.musicVolume = (int) musicVolumeSlider.getValue();
                Main.menuMusic.setVolume((float) Config.musicVolume /100);
            }
        });

        audioPane.add(musicVolumeLabel).pad(Value.percentWidth(0.04f, audioPane)).left();
        Table musicVolumeSliderTable = new Table();
        musicVolumeSliderTable.add(musicVolumeSlider).padLeft(sliderStyle.background.getLeftWidth()).maxSize(Value.percentWidth(0.5f, audioPane), Value.percentHeight(0.7f, musicVolumeLabel)).left().growX();
        musicVolumeSliderTable.add(musicVolumeValueLabel).row();
        audioPane.add(musicVolumeSliderTable).padRight(Value.percentWidth(0.04f, audioPane)).prefWidth(Value.percentWidth(0.75f, audioPane)).left().row();

        Slider sfxVolumeSlider = new Slider(0, 100, 1, false, sliderStyle);
        sfxVolumeSlider.setValue(Config.sfxVolume);

        Label sfxVolumeLabel = new Label("SFX Volume", labelStyle);
        Label sfxVolumeValueLabel = new Label((int)sfxVolumeSlider.getValue() + "%", labelStyle);

        sfxVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfxVolumeValueLabel.setText((int) sfxVolumeSlider.getValue() + "%");
                Config.sfxVolume = (int) sfxVolumeSlider.getValue();
            }
        });

        audioPane.add(sfxVolumeLabel).pad(Value.percentWidth(0.04f, audioPane)).left();
        Table sfxVolumeSliderTable = new Table();
        sfxVolumeSliderTable.add(sfxVolumeSlider).padLeft(sliderStyle.background.getLeftWidth()).maxSize(Value.percentWidth(0.5f, audioPane), Value.percentHeight(0.7f, sfxVolumeLabel)).left().growX();
        sfxVolumeSliderTable.add(sfxVolumeValueLabel).row();
        audioPane.add(sfxVolumeSliderTable).padRight(Value.percentWidth(0.04f, audioPane)).prefWidth(Value.percentWidth(0.75f, audioPane)).left().row();

        SelectBox<String> audioDeviceDropdown = new SelectBox<>(selectBoxStyle);
        audioDeviceDropdown.setItems(AudioDevice.availableDevices().stream().map(s -> s.substring(15)).toArray(String[]::new));
        audioDeviceDropdown.setMaxListCount(3);
        audioDeviceDropdown.setSelected(Config.audioOutputDevice.substring(15));

        audioDeviceDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.audioOutputDevice = AudioDevice.availableDevices().stream().filter(s -> s.substring(15).equals(audioDeviceDropdown.getSelected())).findFirst().orElse(null);
            }
        });

        Label resolutionLabel = new Label("Output Device", labelStyle);

        audioPane.add(resolutionLabel).pad(Value.percentWidth(0.04f, audioPane)).left();
        audioPane.add(audioDeviceDropdown).padRight(Value.percentWidth(0.04f, audioPane)).prefWidth(Value.percentWidth(0.75f)).growX().row();

        addPopupDetector(audioDeviceDropdown);

        OptionCategory.AUDIO.pane = new ScrollPane(audioPane, scrollStyle);
        OptionCategory.AUDIO.pane.setFlingTime(0);
        OptionCategory.AUDIO.pane.setFlickScroll(false);
        OptionCategory.AUDIO.pane.setScrollingDisabled(true, false);
    }

    Table controlsPane;

    private void buildControlsPane(){
        controlsPane = new Table();



        for (Keybind.KeybindType keybindType : Keybind.KeybindType.values()) {
            if (keybindType == Keybind.KeybindType.DEBUG && !Keybind.KeybindType.showDebugKeybinds) continue;
            Label sectionTitle = new Label(keybindType.name(), labelStyle);
            sectionTitle.setFontScale(2);
            controlsPane.add(sectionTitle).pad(Value.percentHeight(0.02f, background)).colspan(3).row();
            for (Keybind keybind : keybindType.keybinds) {
                Label keybindName = new Label(keybind.readableName, labelStyle);
                Table keybindContainer = new Table();

                for (Integer key : keybind.keys) {
                    TextButton button = new TextButton(Input.Keys.toString(key), compactButtonStyle);

                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            changes = true;
                            keybind.keys.remove(key);
                            rebuild();
                        }
                    });

                    keybindContainer.add(button).space(Value.percentWidth(0.02f, background));
                }

                Container<TextButton> plusButton = new Container<>(new TextButton("+", compactButtonStyle));
                plusButton.getActor().addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        activeKeybind = keybind;
                    }
                });

                controlsPane.add(keybindName).prefWidth(Value.percentWidth(0.35f, controlsPane)).expandX().padLeft(Value.percentWidth(0.03f, controlsPane)).left();
                controlsPane.add(keybindContainer).prefSize(Value.percentHeight(0.85f, keybindName)).expandX();
                controlsPane.add(plusButton).maxSize(Value.percentHeight(0.7f, keybindContainer)).pad(Value.percentHeight(0.1f, keybindName)).padRight(Value.percentWidth(0.01f, background)).row();
            }
        }

        OptionCategory.CONTROLS.pane = new ScrollPane(controlsPane, scrollStyle);
        OptionCategory.CONTROLS.pane.setFlingTime(0);
        OptionCategory.CONTROLS.pane.setFlickScroll(false);
        OptionCategory.CONTROLS.pane.setScrollingDisabled(true, false);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (activeKeybind != null && !activeKeybind.keys.contains(keyCode)) {
            activeKeybind.keys.add(keyCode);
            activeKeybind = null;
            changes = true;
            rebuild();
            return true;
        }
        return false;
    }

    private void addPopupDetector(Actor ...actors) {
        if (popupDetector == null) {
            popupDetector = new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    changes = true;
                }
            };
        }
        for (Actor actor : actors) {
            actor.addListener(popupDetector);
        }
    }
}
