package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.ducksteam.needleseye.Main;

import static com.ducksteam.needleseye.Main.*;

/**
 * The screen after the player dies.
 * @author SkySourced
 */
public class DeathStage extends StageTemplate {

	Image background;
	Image title;
	Image exitButtonUnpressed;
	Image exitButtonPressed;

	ImageButton.ImageButtonStyle exitButtonStyle;

	ImageButton exitButton;
	Label levelText;

	@Override
	public void build() {
		background = new Image(new Texture(Gdx.files.internal("ui/death/background.png")));
		title = new Image(new Texture(Gdx.files.internal("ui/death/title.png")));
		exitButtonUnpressed = new Image(new Texture(Gdx.files.internal("ui/death/exit1.png")));
		exitButtonPressed = new Image(new Texture(Gdx.files.internal("ui/death/exit2.png")));

		exitButtonStyle = new ImageButton.ImageButtonStyle();
		exitButtonStyle.imageUp = exitButtonUnpressed.getDrawable();
		exitButtonStyle.imageDown = exitButtonPressed.getDrawable();
		exitButtonStyle.imageOver = exitButtonPressed.getDrawable();

		exitButton = new ImageButton(exitButtonStyle);

		levelText = new Label("", new Label.LabelStyle(uiFont, null));

		rebuild();

		isBuilt = true;
	}

	@Override
	public void rebuild() {
		clear();

		getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		background.setBounds(0, 0, getWidth(), getHeight());
		addActor(background);

		title.setBounds(0, 0, getWidth(), getHeight());
		addActor(title);

		exitButton.setPosition(getWidth() * 237 / 640, getHeight() * 34 / 360);
		exitButton.setSize(getWidth() * 167 / 640, getHeight() * 32 / 360);
		exitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setGameState(Main.GameState.MAIN_MENU);
				return true;
			}
		});
		exitButton.getImage().setFillParent(true);
		addActor(exitButton);

		if (mapMan != null){
			levelText.setText("You reached level " + (mapMan.levelIndex - 1));
			layout.setText(uiFont, levelText.getText());
			levelText.setPosition((float) getWidth() * 320 / 640 - layout.width / 2, (float) getHeight() * 220 / 360);
			addActor(levelText);
		}
	}
}
