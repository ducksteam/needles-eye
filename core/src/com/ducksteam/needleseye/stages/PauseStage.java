package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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

	@Override
	public void build() {
		background = new Image(new Texture("ui/menu/pausebackground.png"));
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

		rebuild();

		isBuilt = true;
	}

	@Override
	public void rebuild() {
		clear();

		getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		background.setBounds(0, 0, getWidth(), getHeight());
		addActor(background);

		resumeButton.setPosition(getWidth() * 36/640, getHeight() * 228/360);
		resumeButton.setSize(getWidth() * 129/640, getHeight() * 30/360);
		resumeButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				setGameState(Main.GameState.IN_GAME);
				return true;
			}
		});

		exitButton.setPosition(getWidth() * 36/640, getHeight() * 80/360);
		exitButton.setSize(getWidth() * 129/640, getHeight() * 30/360);
		exitButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Main.resetGame();
				setGameState(Main.GameState.MAIN_MENU);
				return true;
			}
		});

		resumeButton.getImage().setFillParent(true);
		exitButton.getImage().setFillParent(true);

		addActor(background);
		addActor(exitButton);
		addActor(resumeButton);
	}
}
