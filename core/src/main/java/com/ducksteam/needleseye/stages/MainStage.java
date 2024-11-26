package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
	Image playButtonUnpressed;
	Image playButtonPressed;
	Image instructionsButtonUnpressed;
	Image instructionsButtonPressed;
	Image optionsButtonUnpressed;
	Image optionsButtonPressed;
	Image exitButtonUnpressed;
	Image exitButtonPressed;

	ImageButton.ImageButtonStyle playButtonStyle;
	ImageButton.ImageButtonStyle instructionsButtonStyle;
	ImageButton.ImageButtonStyle optionsButtonStyle;
	ImageButton.ImageButtonStyle exitButtonStyle;

	ImageButton playButton;
	ImageButton instructionsButton;
	ImageButton optionsButton;
	ImageButton exitButton;

	Table buttons;

    /**
     * Create a new main menu stage
     */
	public MainStage() {
		super();
	}

	@Override
	public void build() {
		// Load the images
		// This is in build so it doesn't kill performance
		background = new Image(new Texture("ui/main/background.png"));
		logo = new Image(new Texture("ui/main/logo.png"));
		playButtonUnpressed = new Image(new Texture("ui/main/play1.png"));
		playButtonPressed = new Image(new Texture("ui/main/play2.png"));
		instructionsButtonUnpressed = new Image(new Texture("ui/main/instructions1.png"));
		instructionsButtonPressed = new Image(new Texture("ui/main/instructions2.png"));
		optionsButtonUnpressed = new Image(new Texture("ui/main/options1.png"));
		optionsButtonPressed = new Image(new Texture("ui/main/options2.png"));
		exitButtonUnpressed = new Image(new Texture("ui/main/quit1.png"));
		exitButtonPressed = new Image(new Texture("ui/main/quit2.png"));

		// Set the scaling behaviour
		logo.setScaling(Scaling.fit);
		playButtonUnpressed.setScaling(Scaling.fit);
		playButtonPressed.setScaling(Scaling.fit);
		instructionsButtonUnpressed.setScaling(Scaling.fit);
		instructionsButtonPressed.setScaling(Scaling.fit);
		optionsButtonUnpressed.setScaling(Scaling.fit);
		optionsButtonPressed.setScaling(Scaling.fit);
		exitButtonUnpressed.setScaling(Scaling.fit);
		exitButtonPressed.setScaling(Scaling.fit);

		// Create the button styles & buttons
		playButtonStyle = new ImageButton.ImageButtonStyle();
		playButtonStyle.imageUp = playButtonUnpressed.getDrawable();
		playButtonStyle.imageDown = playButtonPressed.getDrawable();
		playButtonStyle.imageOver = playButtonPressed.getDrawable();

		playButton = new ImageButton(playButtonStyle);

		instructionsButtonStyle = new ImageButton.ImageButtonStyle();
		instructionsButtonStyle.imageUp = instructionsButtonUnpressed.getDrawable();
		instructionsButtonStyle.imageDown = instructionsButtonPressed.getDrawable();
		instructionsButtonStyle.imageOver = instructionsButtonPressed.getDrawable();

		instructionsButton = new ImageButton(instructionsButtonStyle);

		optionsButtonStyle = new ImageButton.ImageButtonStyle();
		optionsButtonStyle.imageUp = optionsButtonUnpressed.getDrawable();
		optionsButtonStyle.imageDown = optionsButtonPressed.getDrawable();
		optionsButtonStyle.imageOver = optionsButtonPressed.getDrawable();

		optionsButton = new ImageButton(optionsButtonStyle);

		exitButtonStyle = new ImageButton.ImageButtonStyle();
		exitButtonStyle.imageUp = exitButtonUnpressed.getDrawable();
		exitButtonStyle.imageDown = exitButtonPressed.getDrawable();
		exitButtonStyle.imageOver = exitButtonPressed.getDrawable();

		exitButton = new ImageButton(exitButtonStyle);

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
		playButton.getCell(playButton.getImage()).grow();
		instructionsButton.getCell(instructionsButton.getImage()).grow();
		optionsButton.getCell(optionsButton.getImage()).grow();
		exitButton.getCell(exitButton.getImage()).grow();

		buttons.add(playButton).prefSize(400, 90).growX().spaceBottom(20).row();
		buttons.add(instructionsButton).prefSize(400, 90).growX().spaceBottom(20).row();
		buttons.add(optionsButton).prefSize(400, 90).growX().row();
		buttons.add(exitButton).prefSize(400, 90).growX().spaceTop(250).row();
		root.add(buttons).left().pad(100).padRight(200);
		root.add(logo).expandX().fillY().fillX().padRight(100);
	}
}
