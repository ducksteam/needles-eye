package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Scaling;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.player.Upgrade;

import static com.ducksteam.needleseye.Main.*;

/**
 * The screen where the player can choose their base upgrade.
 * @author SkySourced
 */
public class ThreadStage extends StageTemplate {

	Image background;
	Image soul;
	Image coal;
	Image jolt;
	Image tRod;

	ImageButton.ImageButtonStyle soulStyle;
	ImageButton.ImageButtonStyle coalStyle;
	ImageButton.ImageButtonStyle joltStyle;
	ImageButton.ImageButtonStyle tRodStyle;

	ImageButton soulButton;
	ImageButton coalButton;
	ImageButton joltButton;
	ImageButton tRodButton;

	Label header;

	@Override
	public void build() {
		background = new Image(new Texture("ui/thread/background.png"));
		soul = new Image(new Texture("ui/thread/soul8.png"));
		coal = new Image(new Texture("ui/thread/coal8.png"));
		jolt = new Image(new Texture("ui/thread/jolt8.png"));
		tRod = new Image(new Texture("ui/thread/threadedrod.png"));

		// Set the scaling behaviour
		soul.setScaling(Scaling.fit);
		coal.setScaling(Scaling.fit);
		jolt.setScaling(Scaling.fit);
		tRod.setScaling(Scaling.fit);

		soulStyle = new ImageButton.ImageButtonStyle();
		soulStyle.imageUp = soul.getDrawable();
		soulButton = new ImageButton(soulStyle);

		coalStyle = new ImageButton.ImageButtonStyle();
		coalStyle.imageUp = coal.getDrawable();
		coalButton = new ImageButton(coalStyle);

		joltStyle = new ImageButton.ImageButtonStyle();
		joltStyle.imageUp = jolt.getDrawable();
		joltButton = new ImageButton(joltStyle);

		tRodStyle = new ImageButton.ImageButtonStyle();
		tRodStyle.imageUp = tRod.getDrawable();
		tRodButton = new ImageButton(tRodStyle);

		header = new Label("~ CHOOSE YOUR THREAD ~", new Label.LabelStyle(titleFont, null));

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

//		tRodButton.setPosition(getWidth() * 220/640, getHeight() * 57/360);
//		tRodButton.setSize(getWidth() * (tRod.getWidth() / 640), getHeight() * tRod.getHeight() /360);

		tRodButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				player.setBaseUpgrade(Upgrade.BaseUpgrade.THREADED_ROD);
				Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
				Main.beginLoading();
				return true;
			}
		});

//		soulButton.setSize(getWidth() * (soul.getWidth() / 640), getHeight() * soul.getHeight() /360);
//		soulButton.setPosition(getWidth() * (160 - soul.getWidth() /2)/640, getHeight() * 100/360);

		soulButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				player.setBaseUpgrade(Upgrade.BaseUpgrade.SOUL_THREAD);
				Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
				Main.beginLoading();
				return true;
			}
		});

//		coalButton.setSize(getWidth() * (coal.getWidth() / 640), getHeight() * coal.getHeight() /360);
//		coalButton.setPosition(getWidth() * (320 - coal.getWidth() /2)/640, getHeight() * 100/360);

		coalButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				player.setBaseUpgrade(Upgrade.BaseUpgrade.COAL_THREAD);
				Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
				Main.beginLoading();
				return true;
			}
		});

//		joltButton.setSize(getWidth() * (jolt.getWidth() / 640), getHeight() * jolt.getHeight() /360);
//		joltButton.setPosition(getWidth() * (480 - jolt.getWidth() /2)/640, getHeight() * 100/360);

		joltButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				player.setBaseUpgrade(Upgrade.BaseUpgrade.JOLT_THREAD);
				Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
				Main.beginLoading();
				return true;
			}
		});

		tRodButton.getCell(tRodButton.getImage()).grow();
		soulButton.getCell(soulButton.getImage()).grow();
		coalButton.getCell(coalButton.getImage()).grow();
		joltButton.getCell(joltButton.getImage()).grow();

		root.add(header).colspan(3).spaceBottom(Value.percentHeight(0.1f, background)).row();
		root.add(soulButton).prefSize(Value.percentHeight(0.312f, background), Value.percentHeight(0.4f, background)).padRight(Value.percentWidth(0.1f, background));
		root.add(coalButton).prefSize(Value.percentHeight(0.312f, background), Value.percentHeight(0.4f, background)).padRight(Value.percentWidth(0.1f, background));
		root.add(joltButton).prefSize(Value.percentHeight(0.312f, background), Value.percentHeight(0.4f, background)).row();
		root.add(tRodButton).prefSize(Value.percentHeight(0.66f, background), Value.percentHeight(0.1f, background)).colspan(3).spaceTop(Value.percentHeight(0.1f, background));
	}
}
