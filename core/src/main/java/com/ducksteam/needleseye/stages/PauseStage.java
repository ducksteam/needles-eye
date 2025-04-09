package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.ducksteam.needleseye.Main;

import static com.ducksteam.needleseye.Main.setGameState;

/**
 * The stage that displays when the player pauses mid-game
 * @author SkySourced
 */

public class PauseStage extends StageTemplate {

	Image background;

	TextButton resumeButton;
    TextButton saveButton;
	TextButton exitButton;

	Table buttons;

	@Override
	public void build() {
        super.build();

		background = new Image(new Texture("ui/pause/pausebackground.png"));

		resumeButton = new TextButton("Play", buttonStyle);
        saveButton = new TextButton("Save", buttonStyle);
		exitButton = new TextButton("Quit", buttonStyle);

		buttons = new Table();

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

		resumeButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				setGameState(Main.GameState.IN_GAME);
				return true;
			}
		});

        saveButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.saveGame();
                saveButton.setChecked(false);
                return true;
            }
        });

		exitButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Main.resetGame();
				setGameState(Main.GameState.MAIN_MENU);
				return true;
			}
		});

        alignButton(resumeButton, saveButton, exitButton);

        buttons.add(resumeButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();
        buttons.add(saveButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().row();
        buttons.add(exitButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceTop(Value.percentHeight(350f/1080, background)).row();
		root.add(buttons).expand().pad(Value.percentWidth(0.1f, background)).left();
	}
}
