package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.ducksteam.needleseye.Main;

import static com.ducksteam.needleseye.Main.setGameState;

/**
 * The stage that displays when the player pauses mid-game
 * @author SkySourced
 */

public class PauseStage extends StageTemplate {

	Image background;

	TextButton resumeButton;
	TextButton exitButton;

	Table buttons;

	@Override
	public void build() {
        super.build();

		background = new Image(new Texture("ui/pause/pausebackground.png"));

		resumeButton = new TextButton("Play", buttonStyle);
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

		exitButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Main.resetGame();
				setGameState(Main.GameState.MAIN_MENU);
				return true;
			}
		});

        resumeButton.getCell(resumeButton.getLabel()).padLeft(Value.percentWidth(0.05f, resumeButton));
        resumeButton.getLabel().setAlignment(Align.left);

        exitButton.getCell(exitButton.getLabel()).padLeft(Value.percentWidth(0.05f, exitButton));
        exitButton.getLabel().setAlignment(Align.left);

		buttons.add(resumeButton).prefSize(400, 90).growX().padBottom(220).row();
		buttons.add(exitButton).prefSize(400, 90).growX().spaceTop(250).row();
		root.add(buttons).pad(100).left();
	}
}
