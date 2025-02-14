package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.map.PlaythroughLoader;

import java.util.Date;

import static com.ducksteam.needleseye.Main.setGameState;

public class LoadStage extends StageTemplate {

    Image background;

    TextButton playButton;
    TextButton loadButton;
    TextButton instructionsButton;
    TextButton optionsButton;
    TextButton exitButton;

    Table buttons;
    Table saves;

    ScrollPane scrollPane;

    public LoadStage() {
        super();
    }

    @Override
    public void build() {
        super.build();

        // Load the images
        // This is in build so it doesn't kill performance
        background = new Image(new Texture("ui/main/background.png"));

        // Create the buttons
        playButton = new TextButton("Play", buttonStyle);
        loadButton = new TextButton("Load", buttonStyle);
        instructionsButton = new TextButton("Instructions", buttonStyle);
        optionsButton = new TextButton("Options", buttonStyle);
        exitButton = new TextButton("Exit", buttonStyle);

        // Create the tables
        buttons = new Table();
        saves = new Table();

        scrollPane = new ScrollPane(saves, scrollStyle);

        // Rebuild the stage, positioning the actors
        rebuild();

        isBuilt = true;
    }

    @Override
    public void rebuild() {
        super.rebuild(); // atm just clears root table
        clear(); // clears the stage

        // Update the viewport
        this.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // clear sub-table
        buttons.clear();

        // background is not in the table because it should be behind everything
        background.setBounds(0, 0, getWidth(), getHeight());
        addActor(background);

        root.setFillParent(true);
        addActor(root);

        // event listeners
        playButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }
        });

        instructionsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                setGameState(Main.GameState.INSTRUCTIONS);
                return true;
            }
        });

        optionsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                setGameState(Main.GameState.OPTIONS);
                return true;
            }
        });

        exitButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.exit(0);
                return true;
            }
        });

        // scene2d magic
        alignButton(playButton, loadButton, instructionsButton, optionsButton, exitButton);

        buttons.add(playButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();
        buttons.add(loadButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();
        buttons.add(instructionsButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();
        buttons.add(optionsButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().row();
        buttons.add(exitButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceTop(Value.percentHeight(130f/1080, background)).row();

        root.left();
        root.add(buttons).padLeft(Value.percentWidth(0.1f, background)).padTop(Value.percentHeight(0.07f, background)).padBottom(Value.percentHeight(0.07f, background)).left();

        buttons.getCell(exitButton).bottom();

        for (FileHandle file : Gdx.files.local(Config.savePath).list()) {
            Button playthroughButton = new Button(compactButtonStyle);

            Label nameLabel = new Label(file.name(), uiFontLabelStyle);
            Label dateLabel = new Label(new Date(file.lastModified()).toString(), smallFontLabelStyle);

            playthroughButton.add(nameLabel).growX().left().pad(Value.percentWidth(0.02f, scrollPane), Value.percentWidth(0.02f, scrollPane), Value.percentWidth(0.01f, scrollPane), Value.zero).row();
            playthroughButton.add(dateLabel).left().pad(Value.zero, Value.percentWidth(0.02f, scrollPane), Value.percentWidth(0.02f, scrollPane), Value.zero).row();

            saves.add(playthroughButton).pad(Value.percentWidth(0.005f, background)).growX().maxHeight(Value.percentHeight(0.33f, scrollPane)).row();

            playthroughButton.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Main.setCurrentSave(PlaythroughLoader.loadPlaythrough(file.path()));
                    //todo: move player into level
                    return true;
                }
            });
        }

        root.add(scrollPane).padLeft(Value.percentWidth(0.1f, background)).prefSize(Value.percentWidth(0.5f, background), Value.percentHeight(790f/1080, background)).maxHeight(Value.percentHeight(790f/1080, background)).left();
        scrollPane.setFlingTime(0);
        scrollPane.setFlickScroll(false);
        scrollPane.setScrollingDisabled(true, false);
    }
}
