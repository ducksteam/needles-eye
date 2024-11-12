package com.ducksteam.needleseye.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A template for any 2d menu visible
 * @author SkySourced
 */

public abstract class StageTemplate extends Stage {

	public boolean isBuilt = false;
	protected Table root = new Table();

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

	public void update() {
		update(Gdx.graphics.getDeltaTime());
	}
}