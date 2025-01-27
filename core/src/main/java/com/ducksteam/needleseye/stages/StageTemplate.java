package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
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

    NinePatchDrawable backgroundNinePatch;
    NinePatchDrawable disabledBackgroundNinePatch;
    NinePatchDrawable highlightBackgroundNinePatch;
    NinePatchDrawable backgroundDitheredNinePatch;
    NinePatchDrawable keybindBackgroundNinePatch;
    NinePatchDrawable keybindOverNinePatch;

    NinePatchDrawable checkboxOnNinePatch;
    NinePatchDrawable checkboxOffNinePatch;
    NinePatchDrawable checkboxOnOverNinePatch;
    NinePatchDrawable checkboxOffOverNinePatch;

    TextButton.TextButtonStyle buttonStyle;
    TextButton.TextButtonStyle dualButtonStyle;
    TextButton.TextButtonStyle roundedButtonStyle;
    TextButton.TextButtonStyle keybindButtonStyle;

    ScrollPane.ScrollPaneStyle scrollStyle;
    Window.WindowStyle windowStyle;

    TextField.TextFieldStyle textFieldStyle;
    SelectBox.SelectBoxStyle selectBoxStyle;
    ImageButton.ImageButtonStyle checkboxStyle;
    Slider.SliderStyle sliderStyle;

    Label.LabelStyle labelStyle;

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
//        if (!System.getProperty("os.name").contains("Mac OS")) this.setDebugAll(true); // not available on OSX
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

        backgroundNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/instructions/textbackground.9.png"), 4,4,4,4));
        disabledBackgroundNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/instructions/disabledbackground.9.png"), 4,4,4,4));
        highlightBackgroundNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/instructions/activebackground.9.png"), 4,4,4,4));
        backgroundDitheredNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/instructions/dropdownbackground.9.png"), 4, 4, 4, 4));
        keybindBackgroundNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/options/keybindbackground_off.9.png"), 8,8,8,8));
        keybindOverNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/options/keybindbackground_over.9.png"), 8,8,8,8));

        dualButtonUnpressedNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/main/dual_button1.png"), 16,16,15,15));
        dualButtonPressedNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/main/dual_button2.png"), 16, 16, 15, 15));

        checkboxOnNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/options/checkbox_outon.png"), 4,4,4,4));
        checkboxOffNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/options/checkbox_outoff.png"), 4,4,4,4));
        checkboxOnOverNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/options/checkbox_inon.png"), 4,4,4,4));
        checkboxOffOverNinePatch = new NinePatchDrawable(new NinePatch(new Texture("ui/options/checkbox_inoff.png"), 4,4,4,4));

        buttonUnpressed.setScaling(Scaling.fit);
        buttonPressed.setScaling(Scaling.fit);


        updateStyles();
    }

	/**
	 * Called whenever content needs to updated
	 * Typically does not reload textures, used primarily for updating text or resizing windows
	 */

	public void rebuild() {
		root.clear();
        updateStyles();
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

    /**
     * Format textbuttons using the predefined style
     * @param buttons buttons to format
     */
    public void alignButton(TextButton ...buttons) {
        for (TextButton button : buttons) {
            button.getCell(button.getLabel()).padLeft(Value.percentWidth(0.05f, button));
            button.getLabel().setAlignment(Align.left);
        }
    }

    private void updateStyles() {
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = buttonUnpressed.getDrawable();
        buttonStyle.down = buttonStyle.over = buttonStyle.checked = buttonPressed.getDrawable();
        buttonStyle.font = Main.buttonFont;
        buttonStyle.fontColor = new Color(0.5803922f, 0.5803922f, 0.5803922f, 1);

        keybindButtonStyle = new TextButton.TextButtonStyle();
        keybindButtonStyle.up = keybindBackgroundNinePatch;
        keybindButtonStyle.down = keybindButtonStyle.over = keybindButtonStyle.checked = keybindOverNinePatch;
        keybindButtonStyle.font = Main.smallFont;
        keybindButtonStyle.fontColor = new Color(0xeeeeeeff);

        dualButtonStyle = new TextButton.TextButtonStyle();
        dualButtonStyle.up = dualButtonUnpressed.getDrawable();
        dualButtonStyle.down = dualButtonStyle.over = dualButtonStyle.checked = dualButtonPressed.getDrawable();
        dualButtonStyle.font = Main.buttonFont;
        dualButtonStyle.fontColor = new Color(0.5803922f, 0.5803922f, 0.5803922f, 1);

        roundedButtonStyle = new TextButton.TextButtonStyle();
        roundedButtonStyle.up = dualButtonUnpressedNinePatch;
        roundedButtonStyle.down = roundedButtonStyle.over = roundedButtonStyle.checked = dualButtonPressedNinePatch;
        roundedButtonStyle.font = Main.buttonFont;
        roundedButtonStyle.fontColor = new Color(0.5803922f, 0.5803922f, 0.5803922f, 1);

        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = backgroundNinePatch;

        windowStyle = new Window.WindowStyle(Main.buttonFont, keybindButtonStyle.fontColor, backgroundDitheredNinePatch);
        windowStyle.stageBackground = disabledBackgroundNinePatch;

        textFieldStyle = new TextField.TextFieldStyle(Main.smallFont, Main.smallFont.getColor(), null, null, backgroundNinePatch);

        selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.backgroundDisabled = highlightBackgroundNinePatch;
        selectBoxStyle.background = backgroundNinePatch;
        selectBoxStyle.font = Main.smallFont;
        selectBoxStyle.fontColor = Color.WHITE;
        selectBoxStyle.listStyle = new List.ListStyle(Main.smallFont, Main.smallFont.getColor(), Color.WHITE, backgroundNinePatch);
        selectBoxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();
        selectBoxStyle.font = Main.smallFont;
        selectBoxStyle.scrollStyle.background = backgroundDitheredNinePatch;

        labelStyle = new Label.LabelStyle(Main.smallFont, Color.WHITE);

        checkboxStyle = new ImageButton.ImageButtonStyle();
        checkboxStyle.checked = checkboxOnNinePatch;
        checkboxStyle.up = checkboxOffNinePatch;
        checkboxStyle.checkedOver = checkboxOnOverNinePatch;
        checkboxStyle.over = checkboxOffOverNinePatch;

        sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = backgroundDitheredNinePatch;
        sliderStyle.knob = backgroundNinePatch;
        sliderStyle.knobOver = highlightBackgroundNinePatch;
    }
}
