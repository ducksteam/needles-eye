package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ducksteam.needleseye.Main;

/**
 * The stage for options. The categories are separated by
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

        buildGeneralPane();

        root.add(currentCategory.pane).padLeft(Value.percentWidth(0.1f, background)).prefSize(Value.percentWidth(0.5f, background), Value.percentHeight(790f/1080, background)).maxHeight(Value.percentHeight(790f/1080, background)).left();
    }

    private void buildGeneralPane(){
        Table pane = new Table();

        TextField seedInputField = new TextField("", textFieldStyle);

        Label seedLabel = new Label("Seed", new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()));

        pane.add(seedLabel).pad(Value.percentWidth(0.4f));
        pane.add(seedInputField).padRight(Value.percentWidth(0.1f)).prefWidth(Value.percentWidth(0.75f)).growX().row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();
        pane.add(new Label("Test" , new Label.LabelStyle(Main.uiFont, Main.uiFont.getColor()))).space(200).row();

        OptionCategory.GENERAL.pane = new ScrollPane(pane, scrollStyle);
    }
}
