package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

	Table upgradeIcons;

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

		levelText = new Label("", new Label.LabelStyle(titleFont, null));

		rebuild();

		isBuilt = true;
	}

	@Override
	public void rebuild() {
		super.rebuild(); // atm just clears root table
		clear(); // clears the stage

		// Update the viewport
		this.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		// background is not in the table because it should be behind everything
		background.setBounds(0, 0, getWidth(), getHeight());
		addActor(background);

		root.setFillParent(true);
		addActor(root);

		exitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setGameState(Main.GameState.MAIN_MENU);
				return true;
			}
		});

		if (mapMan != null){
			levelText.setText("You reached level " + (mapMan.levelIndex - 1));
			layout.setText(titleFont, levelText.getText());
		}

		exitButton.getCell(exitButton.getImage()).grow();

		root.add(levelText).prefHeight(Value.percentHeight(0.08f, background)).row();
		root.add(upgradeIcons).maxWidth(Value.percentWidth(0.41875f, background)).prefHeight(Value.percentHeight(0.325f, background)).spaceTop(Value.percentHeight(0.225f, background)).row();
		root.add(exitButton).prefSize(Value.percentWidth(0.2f, background), Value.percentHeight(0.083f, background)).spaceTop(Value.percentHeight(0.05f, background));
	}
}
