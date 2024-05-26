package com.ducksteam.unseenrealms;

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
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.ducksteam.unseenrealms.entity.enemies.EnemyEntity;
import com.ducksteam.unseenrealms.map.MapManager;
import com.ducksteam.unseenrealms.entity.RoomInstance;
import com.ducksteam.unseenrealms.player.Player;
import com.ducksteam.unseenrealms.player.PlayerInput;

import java.util.ArrayList;

/**
 * The main class of the game
 * @author thechiefpotatopeeler
 * @author SkySourced
 * */
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
	Skin neonSkin;

	Thread loaderThread = new Thread(this::loadAssets);

	//public boolean loading;

	GameState gameState;

	/**
	 * The enum for managing the game state
	 * */
	public enum GameState{
		MAIN_MENU(0),
		IN_GAME(1),
		LOADING(2),
		PAUSED_MENU(3),
		DEAD_MENU(4);

		final int id;
		/**
		 * @param id assigns numeric id to state
		 * */
		GameState(int id){
			this.id=id;
		}

		/**
		 * @return the id of the current state
		 * */
		int getId(){
			return this.id;
		}
	}

	/**
	 * Establishes game at start of runtime
	 * */
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		player = new Player(new Vector3(0,0,0));

		neonSkin = new Skin(Gdx.files.internal("skin/neon-ui.json"));
		mainMenu = new Stage();
		batch2d = new SpriteBatch();
		TextButton startButton = new TextButton("Start", neonSkin,"default");
		startButton.setPosition((float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
		startButton.setSize((float) Gdx.graphics.getWidth() /5, (float) Gdx.graphics.getHeight() /10);
		startButton.setColor(0.9F,0.342F,0.52F,1);
		Image background = new Image(new Texture("loading_background.png"));
		background.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		startButton.addListener(new InputListener(){
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);

				gameState = GameState.LOADING;
				loaderThread.run();
				Gdx.input.setInputProcessor(input);

				return true;
			}
		});
		mainMenu.addActor(background);
		mainMenu.addActor(startButton);

		environment = new Environment();
		batch = new ModelBatch();
		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		assMan = new AssetManager();
		mapMan = new MapManager();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		camera.near = 0.1f;

		//camera.update();
		assMan.finishLoading();

		//enemies.add(new EnemyEntity(new Vector3(0,0,0)) {
		//	@Override
		//	public String getModelAddress() {
		//		return "models/rooms/brokenceiling.g3db";
		//	}
		//});

		//rooms.add(mapMan.getTestRoom());

		//loaderThread.run();
		gameState = GameState.MAIN_MENU;
		Gdx.input.setInputProcessor(mainMenu);
		//gameState = GameState.LOADING;
    }

	/**
	 * Method for loader thread to load assets
	 * */
	private void loadAssets(){
			enemies.forEach((EnemyEntity enemy) -> {
				assMan.load(enemy.getModelAddress(), Model.class);
				assMan.finishLoadingAsset(enemy.getModelAddress());
				((Model) assMan.get(enemy.getModelAddress())).materials.clear();
				((Model) assMan.get(enemy.getModelAddress())).materials.addAll(new Material(TextureAttribute.createDiffuse(new Texture(new String("/debug.jpg")))));
				modelInstances.add(new ModelInstance((Model) assMan.get(enemy.getModelAddress())));
			});
			mapMan.getCurrentLevel().getRooms().forEach((RoomInstance room) -> {
				assMan.load(room.getModelAddress(), Model.class);
				assMan.finishLoadingAsset(room.getModelAddress());
				modelInstances.add(new ModelInstance((Model) assMan.get(room.getModelAddress())));
			});
			Gdx.app.debug("Loader thread", "Loading finished");

	}
	/**
	 * Renders the loading screen while the assets are loading
	 * */
	private void renderLoadingFrame(){
		batch2d.begin();
		batch2d.draw(new Texture("loading_background.png"),0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch2d.draw(new Texture("logo_temp.png"), (float) Gdx.graphics.getWidth()/4, (float) Gdx.graphics.getHeight()/4, (float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
		batch2d.end();
		batch2d.dispose();
	}

	/**
	 * Renders the main menu
	 * */
	private void renderMainMenuFrame(){
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mainMenu.act();
		mainMenu.draw();
	}

	/**
	 *
	 * */
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

		//if (!player.getVel().equals(Vector3.Zero)) Gdx.app.debug("vel", player.getVel() + " vel | pos " + player.getPos());
		input.update();

		player.setPos(player.getPos().add(player.getVel().scl(Gdx.graphics.getDeltaTime())));

		camera.position.set(player.getPos()).add(0,0,5);

		camera.direction.set(player.getRot());
		//camera.lookAt(0f, 0f, 0f);
		batch.begin(camera);
		batch.render(modelInstances,environment);

		if (Config.doRenderColliders) {
			for (RoomInstance o : mapMan.getCurrentLevel().getRooms()) {
				if (o.collider != null) {
					batch.render(o.collider.render(), environment);
				}
			}
		}

		batch.end();

		camera.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
