package com.chiefsource.unseenrealms;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.chiefsource.unseenrealms.entity.enemies.EnemyEntity;
import com.chiefsource.unseenrealms.map.MapManager;
import com.chiefsource.unseenrealms.player.Player;
import com.chiefsource.unseenrealms.player.PlayerInput;

import java.util.ArrayList;

public class Main extends ApplicationAdapter {
	ModelBatch batch;
	AssetManager assMan;
	PerspectiveCamera camera;
	MapManager mapMan;
	Environment environment;
	ArrayList<ModelInstance> modelInstances = new ArrayList<>();
	ArrayList<EnemyEntity> enemies = new ArrayList<>();
	PlayerInput input = new PlayerInput();
	public static Player player;
	public static boolean menu;

	public boolean loading;
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		player = new Player(new Vector3(0,0,0));

		environment = new Environment();
		batch = new ModelBatch();
		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		assMan = new AssetManager();
		mapMan = new MapManager();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		camera.update();

		enemies.forEach((EnemyEntity enemy)->{
			assMan.load(enemy.getModelAddress(),Model.class);
			assMan.finishLoadingAsset(enemy.getModelAddress());
			modelInstances.add(new ModelInstance((Model) assMan.get(enemy.getModelAddress())));
		});

		Gdx.input.setInputProcessor(input);

		assMan.finishLoading();

		loading = true;
	}

	@Override
	public void render () {
		if(loading){
			ScreenUtils.clear(0.4F,0.9F,0.1F,1F);
			return;
		}
		Gdx.app.debug("vel", player.getVel() + " vel | pos " + player.getPos());
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		camera.position.set(player.getPos()).add(0,0,5);

		camera.lookAt(new Vector3(0,0,0));

		batch.begin(camera);
		batch.render(modelInstances,environment);
		batch.end();

		//camera.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
