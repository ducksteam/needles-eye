package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;

import static com.ducksteam.needleseye.Main.setGameState;
import static com.ducksteam.needleseye.Main.uiFont;

/**
 * The screen where the player can view the instructions for the game.
 * @author SkySourced
 */

public class InstructionsStage extends StageTemplate {

	Image background;
	Image backButtonUnpressed;
	Image backButtonPressed;

	ImageButton.ImageButtonStyle backButtonStyle;

	ImageButton backButton;

	StringBuilder keysText;
	Label instructions;

	@Override
	public void build() {
		background = new Image(new Texture("ui/menu/instructions/instructions.png"));
		backButtonUnpressed = new Image(new Texture("ui/death/exit1.png"));
		backButtonPressed = new Image(new Texture("ui/death/exit2.png"));

		backButtonStyle = new ImageButton.ImageButtonStyle();
		backButtonStyle.imageUp = backButtonUnpressed.getDrawable();
		backButtonStyle.imageDown = backButtonPressed.getDrawable();
		backButtonStyle.imageOver = backButtonPressed.getDrawable();

		backButton = new ImageButton(backButtonStyle);

		keysText = new StringBuilder();
		instructions = new Label("", new Label.LabelStyle(uiFont, null));

		rebuild();

		isBuilt = true;
	}

	@Override
	public void rebuild() {
		clear();

		getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		background.setBounds(0, 0, getWidth(), getHeight());
		addActor(background);

		backButton.setPosition(getWidth() * 237 / 640, getHeight() * 34 / 360);
		backButton.setSize(getWidth() * 167 / 640, getHeight() * 32 / 360);
		backButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setGameState(Main.GameState.MAIN_MENU);
				return true;
			}
		});
		backButton.getImage().setFillParent(true);
		addActor(backButton);

		keysText.setLength(0); // clear the string builder
		keysText.append(Input.Keys.toString(Config.keys.get("forward"))).append(", ");
		keysText.append(Input.Keys.toString(Config.keys.get("left"))).append(", ");
		keysText.append(Input.Keys.toString(Config.keys.get("back"))).append(", and ");
		keysText.append(Input.Keys.toString(Config.keys.get("right")));

		instructions.setText("Fight and navigate your way around the dungeon. Use "+keysText+" to move around. Press "+Input.Keys.toString(Config.keys.get("jump"))+" and hold "+Input.Keys.toString(Config.keys.get("run")) + " to run. Gain upgrades in specific dungeon rooms, and use them to fight off enemies. Use left click to use your melee attack, and use right click to use your core thread's secondary ability. In order to progress to the next floor, defeat all the enemies in each room.");
		instructions.setBounds((getWidth() * 155) /640, (getHeight() * 113) /360, (getWidth() * 338) /640, (getHeight() * 148) /360);
		instructions.setWrap(true);

		addActor(instructions);
	}
}
