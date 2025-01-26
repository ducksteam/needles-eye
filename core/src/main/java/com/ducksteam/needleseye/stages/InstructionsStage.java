package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ducksteam.needleseye.Keybind;
import com.ducksteam.needleseye.Main;

import static com.ducksteam.needleseye.Main.*;

/**
 * The stage that shows the player the game's instructions.
 * @author SkySourced
 */

public class InstructionsStage extends StageTemplate {

	Image background;

	TextButton backButton;

	StringBuilder keysText;
	Label instructions;
    Table instructionsTable;
    Label title;


	@Override
	public void build() {
        super.build();

		background = new Image(new Texture("ui/instructions/background.png"));

		backButton = new TextButton("Return To Menu", dualButtonStyle);

		keysText = new StringBuilder();

        instructionsTable = new Table();
        instructionsTable.background(backgroundNinePatch);

        instructions = new Label("", new Label.LabelStyle(uiFont, null));
        title = new Label("INSTRUCTIONS", new Label.LabelStyle(titleFont, null));

		rebuild();

		isBuilt = true;
	}

	@Override
	public void rebuild() {
        super.rebuild();
		clear();

		this.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		background.setBounds(0, 0, getWidth(), getHeight());
		addActor(background);

        root.setFillParent(true);
        addActor(root);

		backButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setGameState(Main.GameState.MAIN_MENU);
				return true;
			}
		});
		addActor(backButton);

		keysText.setLength(0); // clear the string builder
		keysText.append(Keybind.getKeybindKeyString("Forward")).append(", ");
		keysText.append(Keybind.getKeybindKeyString("Backward")).append(", ");
		keysText.append(Keybind.getKeybindKeyString("Left")).append(", and ");
		keysText.append(Keybind.getKeybindKeyString("Right"));

		instructions.setText("Fight and navigate your way around the dungeon. Use "+keysText+" to move around. Press "+Keybind.getKeybindKeyString("Jump")+" to jump and hold "+Keybind.getKeybindKeyString("Run") + " to run. Gain upgrades in specific dungeon rooms, and use them to fight off enemies. Use left click to use your melee attack, and use right click or left click while holding "+Keybind.getKeybindKeyString("Switch Ability Mode") + " to use your core thread's secondary ability. In order to progress to the next floor, defeat all the enemies in each room.");

		instructions.setWrap(true);

        instructionsTable.clear();

        instructionsTable.add(instructions).prefWidth(Value.percentWidth(338/640f, background)).pad(Value.percentWidth(0.02f, background)).row();

        backButton.getLabel().setAlignment(Align.center);

        root.add(title).row();

//        root.add(instructions).prefSize(Value.percentWidth(338f/640, background), Value.percentHeight(148f/360, background)).maxSize(Value.percentWidth(338f/640, background), Value.percentHeight(148f/360, background)).growX().padTop(Value.percentHeight(0.1f, background)).row();
        root.add(instructionsTable).padTop(Value.percentHeight(0.1f, background)).row();

        root.add(backButton).prefSize(Value.percentWidth(167f/640, background), Value.percentHeight(32f/360, background)).spaceTop(Value.percentHeight(0.08f, background));
	}
}
