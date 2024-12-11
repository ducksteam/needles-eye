package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ducksteam.needleseye.Main;

/**
 * A template for any 2d stage visible
 * @author SkySourced
 */

public abstract class StageTemplate extends Stage {

    Image buttonPressed;
    Image buttonUnpressed;

    Image dualButtonPressed;
    Image dualButtonUnpressed;

    NinePatchDrawable dualButtonPressedNinePatch;
    NinePatchDrawable dualButtonUnpressedNinePatch;
    NinePatchDrawable background9Patch;

    TextButton.TextButtonStyle buttonStyle;
    TextButton.TextButtonStyle dualButtonStyle;
    TextButton.TextButtonStyle roundedButtonStyle;

    /**
     * Whether the stage has been built initially, and has the textures loaded
     */
	public boolean isBuilt = false;
    /**
     * The root table of the stage
     */
    protected Table root = new Table();

    /**
     * Create a new stage template
     */
	public StageTemplate() {
		super(new ScreenViewport(), Main.batch2d);
		build();
        if (!System.getProperty("os.name").contains("Mac OS")) this.setDebugAll(false); // not available on OSX
	}

	/**
	 * Called when the stage is created
	 * Loads textures, fonts, etc. to be saved
	 */

    //TODO: this is called once for each stage, could static-ify
	public void build() {
        buttonUnpressed = new Image(new Texture("ui/main/button1.png"));
        buttonPressed = new Image(new Texture("ui/main/button2.png"));

        dualButtonUnpressed = new Image(new Texture("ui/main/dual_button1.png"));
        dualButtonPressed = new Image(new Texture("ui/main/dual_button2.png"));

        background9Patch = new NinePatchDrawable(new NinePatch(new Texture("ui/instructions/textbackground.9.png"), 4,4,4,4));
        dualButtonUnpressedNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/main/dual_button1.png"), 16,16,15,15));
        dualButtonPressedNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/main/dual_button2.png"), 16, 16, 15, 15));

        buttonUnpressed.setScaling(Scaling.fit);
        buttonPressed.setScaling(Scaling.fit);

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = buttonUnpressed.getDrawable();
        buttonStyle.down = buttonStyle.over = buttonPressed.getDrawable();
        buttonStyle.font = Main.buttonFont;
        buttonStyle.fontColor = new Color(0.5803922f, 0.5803922f, 0.5803922f, 1);

        dualButtonStyle = new TextButton.TextButtonStyle();
        dualButtonStyle.up = dualButtonUnpressed.getDrawable();
        dualButtonStyle.down = dualButtonStyle.over = dualButtonPressed.getDrawable();
        dualButtonStyle.font = Main.buttonFont;
        dualButtonStyle.fontColor = new Color(0.5803922f, 0.5803922f, 0.5803922f, 1);

        roundedButtonStyle = new TextButton.TextButtonStyle();
        roundedButtonStyle.up = dualButtonUnpressedNinePatch;
        roundedButtonStyle.down = roundedButtonStyle.over = dualButtonPressedNinePatch;
        roundedButtonStyle.font = Main.buttonFont;
        roundedButtonStyle.fontColor = new Color(0.5803922f, 0.5803922f, 0.5803922f, 1);
    }

	/**
	 * Called whenever content needs to updated
	 * Typically does not reload textures, used primarily for updating text or resizing windows
	 */

	public void rebuild() {
		root.clear();

	}

	/**
	 * Called to update the stage
	 * @param delta the time since the last frame
	 */

	public void update(float delta) {
		act(delta);
		draw();
	}

    /**
     * Update the stage using libGDX's delta time
     */
	public void update() {
		update(Gdx.graphics.getDeltaTime());
	}
}
