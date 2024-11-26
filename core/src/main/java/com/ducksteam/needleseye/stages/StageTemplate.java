package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A template for any 2d stage visible
 * @author SkySourced
 */

public abstract class StageTemplate extends Stage {

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
		super(new ScreenViewport());
		build();
		this.setDebugAll(true);
	}

	/**
	 * Called when the stage is created
	 * Loads textures, fonts, etc. to be saved
	 */

	public void build() {
		isBuilt = true;
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
