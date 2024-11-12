package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ducksteam.needleseye.Main;

import static com.ducksteam.needleseye.Main.setGameState;

/**
 * The pause screen
 * @author SkySourced
 */

public class PauseStage extends StageTemplate {

	Image background;
	Image resumeButtonUnpressed;
	Image resumeButtonPressed;
	Image exitButtonUnpressed;
	Image exitButtonPressed;

	ImageButton.ImageButtonStyle resumeButtonStyle;
	ImageButton.ImageButtonStyle exitButtonStyle;

	ImageButton resumeButton;
	ImageButton exitButton;

	Table buttons;

	@Override
	public void build() {
		background = new Image(new Texture("ui/pause/pausebackground.png"));
		resumeButtonUnpressed = new Image(new Texture("ui/menu/play1.png"));
		resumeButtonPressed = new Image(new Texture("ui/menu/play2.png"));
		exitButtonUnpressed = new Image(new Texture("ui/menu/quit1.png"));
		exitButtonPressed = new Image(new Texture("ui/menu/quit2.png"));

		resumeButtonStyle = new ImageButton.ImageButtonStyle();
		resumeButtonStyle.imageUp = resumeButtonUnpressed.getDrawable();
		resumeButtonStyle.imageDown = resumeButtonPressed.getDrawable();
		resumeButtonStyle.imageOver = resumeButtonPressed.getDrawable();

		exitButtonStyle = new ImageButton.ImageButtonStyle();
		exitButtonStyle.imageUp = exitButtonUnpressed.getDrawable();
		exitButtonStyle.imageDown = exitButtonPressed.getDrawable();
		exitButtonStyle.imageOver = exitButtonPressed.getDrawable();

		resumeButton = new ImageButton(resumeButtonStyle);
		exitButton = new ImageButton(exitButtonStyle);

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

		resumeButton.getCell(resumeButton.getImage()).grow();
		exitButton.getCell(exitButton.getImage()).grow();

		buttons.add(resumeButton).prefSize(400, 90).growX().padBottom(220).row();
		buttons.add(exitButton).prefSize(400, 90).growX().spaceTop(250).row();
		root.add(buttons).pad(100).left();
	}
}
