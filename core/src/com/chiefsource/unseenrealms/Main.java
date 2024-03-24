package com.chiefsource.unseenrealms;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.chiefsource.unseenrealms.map.MapManager;

public class Main extends ApplicationAdapter {
	ModelBatch batch;
	AssetManager assMan;
	PerspectiveCamera camera;
	MapManager mapMan;
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		batch = new ModelBatch();
		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		assMan = new AssetManager();
		mapMan = new MapManager();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1, true);
		batch.begin(camera);
		//batch.render();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
