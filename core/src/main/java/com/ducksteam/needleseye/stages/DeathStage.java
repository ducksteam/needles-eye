package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ducksteam.needleseye.Main;

import static com.ducksteam.needleseye.Main.*;

/**
 * The stage that displays after the player dies.
 * @author SkySourced
 * @author themoonboyx
 */
public class DeathStage extends StageTemplate {

	Image background;

	TextButton exitButton;
	Label levelText;

	Table upgradeIcons;

	@Override
	public void build() {
        super.build();

		background = new Image(new Texture(Gdx.files.internal("ui/death/background.png")));

		exitButton = new TextButton("Return To Menu",  dualButtonStyle);

		levelText = new Label("", new Label.LabelStyle(titleFont, null));

		upgradeIcons = new Table();

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

		upgradeIcons.clear();
		for(int i=0; i < player.upgrades.size(); i++) { // draws upgrade icons
			if((i*0.05f)%0.4f == 0 && i!=0) upgradeIcons.row();
			upgradeIcons.add(new Image(player.upgrades.get(i).getIcon())).size(Value.percentWidth(0.03f, background)).pad(Value.percentWidth(0.01f, background)).top();
		}
		upgradeIcons.add(new Actor()).expand(); // pushes everything to the left

        exitButton.getLabel().setAlignment(Align.center);

		root.add(levelText).prefHeight(Value.percentHeight(0.08f, background)).row();
		root.add(upgradeIcons).prefWidth(Value.percentWidth(0.41875f, background)).prefHeight(Value.percentHeight(0.325f, background)).spaceTop(Value.percentHeight(0.225f, background)).row();
		root.add(exitButton).prefSize(Value.percentWidth(167f/640, background), Value.percentHeight(32f/360, background)).spaceTop(Value.percentHeight(0.08f, background));
	}
}
