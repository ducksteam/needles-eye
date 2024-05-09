package com.waddleworks.unseenrealms;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.waddleworks.unseenrealms.entity.enemies.EnemyEntity;
import com.waddleworks.unseenrealms.map.MapManager;
import com.waddleworks.unseenrealms.player.Player;
import com.waddleworks.unseenrealms.player.PlayerInput;

import java.util.ArrayList;

public class Main extends ApplicationAdapter {
	ModelBatch batch;
	Stage mainMenu;
	SpriteBatch batch2d;
	AssetManager assMan;
	public static PerspectiveCamera camera;
	MapManager mapMan;
	Environment environment;
	ArrayList<ModelInstance> modelInstances = new ArrayList<>();
	ArrayList<EnemyEntity> enemies = new ArrayList<>();
	PlayerInput input = new PlayerInput();
	public static Player player;
	public static boolean menu;
	Skin neonSkin;

	Thread loaderThread = new Thread(this::loadAssets);

	//public boolean loading;

	GameState gameState;

	public enum GameState{
		MAIN_MENU(0),
		IN_GAME(1),
		LOADING(2),
		PAUSED_MENU(3),
		DEAD_MENU(4);

		int id;
		GameState(int id){
			this.id=id;
		}

		int getId(){
			return this.id;
		}
	}
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		player = new Player(new Vector3(0,0,0));

		neonSkin = new Skin(Gdx.files.internal("skin/neon-ui.json"));
		mainMenu = new Stage();
		batch2d = new SpriteBatch();
		TextButton startButton = new TextButton("Start", neonSkin,"default");
		startButton.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
		startButton.setSize(Gdx.graphics.getWidth()/5,Gdx.graphics.getHeight()/10);
		startButton.setColor(0.9F,0.342F,0.52F,1);
		startButton.addListener(new InputListener(){
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				gameState = GameState.LOADING;
				Gdx.input.setInputProcessor(input);
				return true;
			}
		});
		mainMenu.addActor(startButton);

		environment = new Environment();
		batch = new ModelBatch();
		camera = new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		assMan = new AssetManager();
		mapMan = new MapManager();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		camera.near = 0.1f;

		camera.update();
		assMan.finishLoading();

		enemies.add(new EnemyEntity(new Vector3(0,0,0)) {
			@Override
			public String getModelAddress() {
				return "models/enemies/worm.g3dj";
			}
		});

		//loaderThread.run();
		gameState = GameState.MAIN_MENU;
		Gdx.input.setInputProcessor(mainMenu);
		//gameState = GameState.LOADING;
    }

	private void loadAssets(){
		enemies.forEach((EnemyEntity enemy)->{
			assMan.load(enemy.getModelAddress(),Model.class);
			assMan.finishLoadingAsset(enemy.getModelAddress());
			modelInstances.add(new ModelInstance((Model) assMan.get(enemy.getModelAddress())));
		});
		finishLoading();
	}
	private void finishLoading(){
		Gdx.app.debug("Loader thread", "Loading finished");
	}

	private void renderLoadingFrame(){
		batch2d.begin();
		batch2d.draw(new Texture("loading_background.png"),0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch2d.draw(new Texture("logo_temp.png"), (float) Gdx.graphics.getWidth()/4, (float) Gdx.graphics.getHeight()/4, (float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
		batch2d.end();
		batch2d.dispose();
	}

	private void renderMainMenuFrame(){
		Gdx.gl.glClearColor(0, 1, 0.7f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mainMenu.act();
		mainMenu.draw();
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		if(gameState.getId()==2){
			gameState = (loaderThread.isAlive())? GameState.LOADING:GameState.IN_GAME;
			renderLoadingFrame();
			return;
		}
		if(gameState.getId()==0){
				renderMainMenuFrame();
			return;
		}
		if (!player.getVel().equals(Vector3.Zero)) Gdx.app.debug("vel", player.getVel() + " vel | pos " + player.getPos());

		player.setPos(player.getPos().add(player.getVel()));
		camera.position.set(player.getPos()).add(0,0,5);
		camera.direction.set(player.getRot());

		batch.begin(camera);
		batch.render(modelInstances,environment);
		batch.end();

		camera.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
