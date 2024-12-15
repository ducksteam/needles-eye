package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Scaling;
import com.ducksteam.needleseye.Main;

import static com.ducksteam.needleseye.Main.setGameState;

/**
 * The stage that displays the main menu.
 * @author SkySourced
 */

public class MainStage extends StageTemplate {

	Image background;
	Image logo;

	TextButton playButton;
	TextButton instructionsButton;
	TextButton optionsButton;
	TextButton exitButton;

	Table buttons;

    /**
     * Create a new main menu stage
     */
	public MainStage() {
		super();
	}

	@Override
	public void build() {
        super.build();

		// Load the images
		// This is in build so it doesn't kill performance
		background = new Image(new Texture("ui/main/background.png"));
		logo = new Image(new Texture("ui/main/logo.png"));

		// Set the scaling behaviour
		logo.setScaling(Scaling.fit);

		// Create the buttons
		playButton = new TextButton("Play", buttonStyle);
		instructionsButton = new TextButton("Instructions", buttonStyle);
        optionsButton = new TextButton("Options", buttonStyle);
		exitButton = new TextButton("Exit", buttonStyle);

		// Create the table for the buttons
		buttons = new Table();

		// Rebuild the stage, positioning the actors
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

		// event listeners
		playButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (Main.getActiveUIAnim() == null){
					Main.setActiveUIAnim(Main.transitionAnimation, MainStage.this::update, () -> setGameState(Main.GameState.THREAD_SELECT));
				}
				return true;
			}
		});

		instructionsButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				setGameState(Main.GameState.INSTRUCTIONS);
				return true;
			}
		});

		optionsButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				setGameState(Main.GameState.OPTIONS);
				return true;
			}
		});

		exitButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.exit(0);
				return true;
			}
		});

		// scene2d magic
        alignButton(playButton, instructionsButton, optionsButton, exitButton);

		buttons.add(playButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();
		buttons.add(instructionsButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceBottom(Value.percentHeight(20f/1080, background)).row();
		buttons.add(optionsButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().row();
		buttons.add(exitButton).prefSize(Value.percentWidth(400f/1920, background), Value.percentHeight(90f/1080, background)).growX().spaceTop(Value.percentHeight(250f/1080, background)).row();
		root.add(buttons).left().pad(Value.percentWidth(0.1f, background));
		root.add(logo).expandX().pad(Value.percentWidth(0.05f, background)).fillY().fillX().padRight(100);

        buttons.getCell(exitButton).bottom();
	}
}
