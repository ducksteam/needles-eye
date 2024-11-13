package com.ducksteam.needleseye;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ducksteam.needleseye.entity.*;
import com.ducksteam.needleseye.entity.bullet.CollisionListener;
import com.ducksteam.needleseye.entity.effect.DamageEffectManager;
import com.ducksteam.needleseye.entity.effect.ParalysisEffectManager;
import com.ducksteam.needleseye.entity.effect.SoulFireEffectManager;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.entity.pickups.UpgradeEntity;
import com.ducksteam.needleseye.map.MapManager;
import com.ducksteam.needleseye.map.RoomTemplate;
import com.ducksteam.needleseye.player.Player;
import com.ducksteam.needleseye.player.PlayerInput;
import com.ducksteam.needleseye.player.Upgrade;
import com.ducksteam.needleseye.player.Upgrade.BaseUpgrade;
import com.ducksteam.needleseye.stages.*;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * The main class of the game
 * @author thechiefpotatopeeler
 * @author SkySourced
 * */
public class Main extends Game {
	// 3d rendering utils
	static ModelBatch batch; // used for rendering 3d models
	public static PerspectiveCamera camera; // the camera
	public static FitViewport viewport; // the viewport
	PointLight playerLantern; // the player's spotlight
	Color playerLanternColour; // the colour for the light
	public static ParticleSystem particleSystem; // the particle system
	public static BillboardParticleBatch generalBBParticleBatch; // the particle batch for billboards that use the general_particle
	public static BillboardParticleBatch paralyseBBParticleBatch; // the particle batch for billboards that use the paralyse_particle

    static Music menuMusic; // the music for the menu
	public static HashMap<String, Sound> sounds; // the sound for walking


	// stages for 2d UI components
	Stage debug;

	// other 2d rendering utils
	SpriteBatch batch2d; // used for rendering other 2d sprites
	HashMap<String,Texture> spriteAssets = new HashMap<>(); // textures mapped to their addresses
	public static BitmapFont uiFont; // font for text
	public static BitmapFont titleFont; // font for titles
	public static GlyphLayout layout; // layout for text

	// asset manager
	public static AssetManager assMan;

	// level & room manager
	public static MapManager mapMan;

	// objects to be rendered
	public static SceneManager sceneMan;
	public static ConcurrentHashMap<Integer, Entity> entities = new ConcurrentHashMap<>(); // key = entity.id
	// sprites to be loaded into asset manager
	static ArrayList<String> spriteAddresses = new ArrayList<>();

	// physics utils
	public static btDynamicsWorld dynamicsWorld; // contains all physics object
	public static btConstraintSolver constraintSolver; // solves constraints
	public static btBroadphaseInterface broadphase; // detects overlapping AABBs
	public static btCollisionConfiguration collisionConfig; // configure collision settings
	public static btDispatcher dispatcher; // dispatches collision calculations for overlapping pairs
	public static DebugDrawer debugDrawer;
	public static CollisionListener contactListener; // custom collision event listener

	@Deprecated
	static long time = 0; // ms since first render

	static long timeAtLastRender;
	float dT;

	// input & player
	GlobalInput globalInput = new GlobalInput();
	public static Player player;

	// ui animation resources
	static Animation<TextureRegion> activeUIAnim; // the active UI animation
	static float animTime; // the progress through the active UI animation
	public static float attackAnimTime; // the progress through the attack animation
	public static float crackAnimTime; // the progress through the ability animation
	static Runnable animPreDraw; // runs every frame when an animation is active
	static Runnable animFinished; // runs once when the animation finishes
	static int[] threadAnimState = {0, 0, 0}; // information about the thread menu animation
	public static Animation<TextureRegion> transitionAnimation; // the main menu -> thread menu animation

	//Runtime info
	public static GameState gameState; // current game state
	private static String gameStateCheck;

	//Runtime utils
	private SplashWorker splashWorker; // splash screen

	/**
	 * The enum for managing the game state
	 * */
	public enum GameState{
		MAIN_MENU(0),
		LOADING(1),
		THREAD_SELECT(2),
		IN_GAME(3),
		PAUSED_MENU(4),
		DEAD_MENU(5),
		INSTRUCTIONS(6),
		OPTIONS(7);

		final int id;
		InputProcessor inputProcessor; // the input manager for each game state
		StageTemplate stage; // the stage for each game state

		/**
		 * @param id assigns numeric id to state
		 * */
		GameState(int id){
			this.id=id;
		}

		/**
		 * @return the input processor of the current state
		 * */
		InputProcessor getInputProcessor(){
			return this.inputProcessor;
		}
		/**
		 * @param inputProcessor sets the input processor of the current state
		 * */
		void setInputProcessor(InputProcessor inputProcessor){
			this.inputProcessor = inputProcessor;
		}

		/**
		 * @return the stage of the current state
		 * */
		StageTemplate getStage(){
			if (this.stage != null) return this.stage;
			else return null;
		}

		/**
		 * @param stage sets the stage of the current state
		 * */

		void setStage(StageTemplate stage){
			this.stage = stage;
		}
	}

	/**
	 * Sets the input processor and screens assigned to each GameState
	 * */
	public void initialiseGameStates(){
		GameState.MAIN_MENU.setStage(new MainStage());
		GameState.THREAD_SELECT.setStage(new ThreadStage());
		GameState.PAUSED_MENU.setStage(new PauseStage());
		GameState.DEAD_MENU.setStage(new DeathStage());
		GameState.INSTRUCTIONS.setStage(new InstructionsStage());

		// An input processor for menus that can be exited, to be multiplexed with other
		InputAdapter menuEscape = new InputAdapter(){
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.ESCAPE) {
					setGameState(GameState.MAIN_MENU);
					return true;
				}
				return false;
			}
		};

		// actually set the input processors
		GameState.IN_GAME.setInputProcessor(new InputMultiplexer(globalInput, new PlayerInput()));
		GameState.MAIN_MENU.setInputProcessor(new InputMultiplexer(globalInput, GameState.MAIN_MENU.getStage()));
		GameState.THREAD_SELECT.setInputProcessor(new InputMultiplexer(menuEscape, globalInput, GameState.THREAD_SELECT.getStage()));
		GameState.LOADING.setInputProcessor(globalInput);
		GameState.PAUSED_MENU.setInputProcessor(new InputMultiplexer(globalInput, GameState.PAUSED_MENU.getStage()));
		GameState.DEAD_MENU.setInputProcessor(new InputMultiplexer(menuEscape, globalInput, GameState.DEAD_MENU.getStage()));
		GameState.INSTRUCTIONS.setInputProcessor(new InputMultiplexer(menuEscape, globalInput, GameState.INSTRUCTIONS.getStage()));
		GameState.OPTIONS.setInputProcessor(new InputMultiplexer(menuEscape, globalInput));
	}

	/**
	 * Sets the game state
	 * @param gameState the state to set the game to
	 * */
	public static void setGameState(GameState gameState){
		Main.gameState = gameState;
		Gdx.input.setInputProcessor(gameState.getInputProcessor());
		if (gameState == GameState.PAUSED_MENU) Gdx.input.setCursorCatched(false);
		//Switches music
		if(menuMusic!=null) {
			if ((gameState == GameState.MAIN_MENU || gameState == GameState.THREAD_SELECT || gameState == GameState.LOADING || gameState == GameState.IN_GAME|| gameState == GameState.DEAD_MENU)) menuMusic.play();
			else menuMusic.pause();
		}
		//Checks against previous state
		gameStateCheck = gameState.toString();
		if(gameState == GameState.IN_GAME) {
			timeAtLastRender = System.currentTimeMillis();
			Gdx.input.setCursorCatched(true);
			PlayerInput.KEYS.forEach((key, value) -> PlayerInput.KEYS.put(key, false));
		}
	}

	public static Animation<TextureRegion> getActiveUIAnim() {
		return activeUIAnim;
	}

	public static void setActiveUIAnim(Animation<TextureRegion> anim, Runnable preDraw, Runnable finished) {
		activeUIAnim = anim;
		animTime = 0;
		animPreDraw = preDraw;
		animFinished = finished;
	}

	/**
	 * Begins the loading of assets
	 * */
	public static void beginLoading(){
		if (gameState == GameState.LOADING) return;
		setGameState(GameState.LOADING);
		loadAssets();
	}

	/**
	 * Establishes game at start of runtime and starts all necessary processes
	 * */
	@Override
	public void create () {
		// remove splash screen
		splashWorker.closeSplashScreen();

		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		//Runs registries
		Upgrade.registerUpgrades();
		EnemyRegistry.initEnemies();

		//Registers upgrade icon addresses
		UpgradeRegistry.registeredUpgrades.forEach((id,upgradeClass)->{
			if(upgradeClass == null) return;
			try {
				// add the upgrade icon to the list of addresses to load in
				spriteAddresses.add(Objects.requireNonNull(UpgradeRegistry.getUpgradeInstance(upgradeClass)).getIconAddress());
			} catch (NullPointerException e) {
				Gdx.app.error("Main", "Failed to load icon for "+id,e);
			}
        });
		Gdx.app.debug("SpriteAddresses", spriteAddresses.toString());

		//Registers other assets
		spriteAddresses.add("ui/ingame/heart.png");
		spriteAddresses.add("ui/ingame/empty_heart.png");
		spriteAddresses.add("ui/ingame/first_heart.png");
		spriteAddresses.add("ui/ingame/damage.png");

		// load music/sfx
		try {
			menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/throughtheeye.mp3"));
			menuMusic.setLooping(true);
			menuMusic.setVolume(0f);
		} catch (GdxRuntimeException e) {
			Gdx.app.error("Main", "Failed to load music file",e);
		}

		sounds = new HashMap<>();
		sounds.put("audio/sfx/walking_2.mp3",null);
		sounds.put("audio/sfx/whip_crack_1.mp3",null);
		sounds.put("audio/sfx/whip_lash_1.mp3",null);

		//Establishes physics

		Bullet.init(true, false);

		contactListener = new CollisionListener();
		contactListener.enable();
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);

		// create player
		player = new Player(Config.PLAYER_START_POSITION.cpy());

		// initialise drawing utils
		batch2d = new SpriteBatch();
		layout = new GlyphLayout();

		debugDrawer = new DebugDrawer();
		debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_FastWireframe);

		//Builds UI elements
		buildFonts();

		//Sets up environment and camera
		playerLanternColour = new Color(0.85f*Config.LIGHT_INTENSITY, 0.85f*Config.LIGHT_INTENSITY, 0.6f*Config.LIGHT_INTENSITY, 1f);
		playerLantern = new PointLight().set(playerLanternColour, player.getPosition(), 10);
		//environment = new Environment();
		batch = new ModelBatch();
		sceneMan = new SceneManager();
		camera = new PerspectiveCamera();
		viewport = new FitViewport(640, 360, camera);
		//environment.set(new ColorAttribute(ColorAttribute.AmbientLight,Config.LIGHT_COLOUR));
		//environment.add(playerLantern);
		camera.near = 0.1f;
		sceneMan.setCamera(camera);
		//sceneMan.environment = environment;

		sceneMan.setAmbientLight(0.0001f);
		sceneMan.environment.add(playerLantern);

		// init particles
		generalBBParticleBatch = new BillboardParticleBatch();
		paralyseBBParticleBatch = new BillboardParticleBatch();
		particleSystem = new ParticleSystem();
		generalBBParticleBatch.setCamera(camera);
		paralyseBBParticleBatch.setCamera(camera);
		particleSystem.add(generalBBParticleBatch);
		particleSystem.add(paralyseBBParticleBatch);

		//Sets up game managers
		assMan = new AssetManager();
		mapMan = new MapManager();

		//Sets up animations
		Texture transitionMap = new Texture(Gdx.files.internal("ui/thread/thread-transition.png"));
		TextureRegion[] transitionFrames = TextureRegion.split(transitionMap, 640, 360)[0];
		transitionAnimation = new Animation<>(Config.LOADING_ANIM_SPEED, transitionFrames);

		//Finalises
		assMan.finishLoading();
		initialiseGameStates();
		setGameState(GameState.MAIN_MENU);
    }

	/**
	 * Construct font assets
	 * */
	private void buildFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/JetBrainsMono.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = (int) (0.02 * Gdx.graphics.getHeight()); // scale font to window size
		uiFont = generator.generateFont(parameter);
		parameter.size = (int) (0.08 * Gdx.graphics.getHeight());
		titleFont = generator.generateFont(parameter);
	}

	/**
	 * Construct debug menu from actors
	 * */
	private void buildDebugMenu(){
		if (debug != null) debug.dispose(); // clear previous debug menu
		debug = new Stage();

		ArrayList<Label> labels = new ArrayList<>();

		Label gameState = new Label("Game state: " + gameStateCheck, new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(gameState);

		Label coords = new Label("Location: "+player.getPosition().toString(), new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(coords);

		Label rotation = new Label("Rotation: " + player.getRotation().toString(), new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(rotation);

		Label eulerAngles = new Label("Camera angle: " + player.eulerRotation, new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(eulerAngles);

		Label velocity = new Label("Velocity: " + player.getVelocity().len() + " " + player.getVelocity().toString(), new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(velocity);

		Label fps = new Label("FPS: " + Gdx.graphics.getFramesPerSecond(), new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(fps);

		Label attackTime = new Label("Attack time: " + player.getAttackTimeout(), new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(attackTime);

		Label damageBoost = new Label("Coal damage boost: " + player.coalDamageBoost, new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(damageBoost);

		Label isJumping = new Label("Jumping: " + player.isJumping, new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(isJumping);

		Vector2 mapSpaceCoords = MapManager.getRoomSpacePos(player.getPosition(), true);
		Vector2 mapSpaceRealCoords = MapManager.getRoomSpacePos(player.getPosition(), false);
		Label mapSpace = new Label("Room space: " + mapSpaceCoords + " " + mapSpaceRealCoords, new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(mapSpace);

		if (!mapMan.levels.isEmpty()) {
			RoomInstance[] currentRooms = mapMan.getCurrentLevel().getRooms().stream().filter(room -> room.getRoomSpacePos().equals(mapSpaceCoords)).toArray(RoomInstance[]::new);

			if (currentRooms.length != 0) {
				// get names of room(s) the player is standing in
				StringBuilder names = new StringBuilder();
				for (RoomInstance room : currentRooms) {
                    if (!(room instanceof HallwayPlaceholderRoom)) {
                        names.append(room.getRoom().getName()).append(room.getRot()).append(" ").append(room.transform.getTranslation(new Vector3())).append(", ");
                    } else {
                        names.append(((HallwayPlaceholderRoom) room).getAssociatedRoom().getRoom().getName()).append(((HallwayPlaceholderRoom) room).getAssociatedRoom().getRot()).append("-assoc, ");
                    }
                }
				Label roomName = new Label("Room: " + names, new Label.LabelStyle(uiFont, uiFont.getColor()));
				labels.add(roomName);

				// get enemies registered to the room the player is standing in
				StringBuilder enemiesSB = new StringBuilder();
				for (EnemyEntity enemy : currentRooms[0].getEnemies().values()) enemiesSB.append(enemy).append(", \n");
				Label enemies = new Label("Enemies: " + enemiesSB, new Label.LabelStyle(uiFont, uiFont.getColor()));
				labels.add(enemies);
			}
		}

		Label mainTime = new Label("Time: " + getTime(), new Label.LabelStyle(uiFont, uiFont.getColor()));
		labels.add(mainTime);

		// set positions of labels
		labels.forEach(label -> {
			label.setPosition(16F, (float) (Gdx.graphics.getHeight() - (0.04 * (labels.indexOf(label)+1) * Gdx.graphics.getHeight())));
			debug.addActor(label);
		});
	}

	/**
	 * Receives enemy data from rooms and releases them to level
	 * */
	public void spawnEnemies(){
		// for each room in the level
		mapMan.getCurrentLevel().getRooms().forEach((RoomInstance room) -> {
			// for each assigned enemy
			for(Map.Entry<Integer,EnemyEntity> entry : room.getEnemies().entrySet()){
				// ensure that each enemy has a model
				if(entry.getValue().getScene()==null){
					entry.getValue().setScene(EnemyRegistry.enemyScenes.get(entry.getValue().getClass()));
				}
				// position the enemy
				EnemyEntity enemy = entry.getValue();
				Matrix4 transform = new Matrix4();
				enemy.motionState.getWorldTransform(transform);
				transform.setTranslation(room.getPosition().add(new Vector3(0,2,0)));
				// add the enemy to the entities array
				entities.put(enemy.id, enemy);
			}
		});
	}

	/**
	 * Performs any post room loading tasks
	 * */
	private void postLevelLoad() {
		EnemyRegistry.postLoadEnemyAssets(assMan);
		spriteAddresses.forEach((String address)-> spriteAssets.put(address,assMan.get(address)));
		for (Map.Entry<String, Sound> entry : sounds.entrySet()) {
			String address = entry.getKey();
			entry.setValue(assMan.get(address, Sound.class));
		}
		// Tell the particle managers to load the effect
		SoulFireEffectManager.loadStaticEffect();
		DamageEffectManager.loadStaticEffect();
		ParalysisEffectManager.loadStaticEffect();

		UpgradeRegistry.iconsLoaded = true;

		mapMan.levelIndex = 1;
		mapMan.generateLevel();
		spawnEnemies();
	}

	/**
	 * Loads all models and sprites to the game
	 * */
	private static void loadAssets() {
		Gdx.app.debug("Loader thread", "Loading started");
		// Load enemy models
		EnemyRegistry.loadEnemyAssets(assMan);
		// Load room models
		assMan.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
		MapManager.roomTemplates.forEach((RoomTemplate room) -> {
			if (room.getModelPath() == null) return;
			assMan.load(room.getModelPath(), SceneAsset.class);
		});
		//Walls
		assMan.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
		assMan.load(WallObject.MODEL_ADDRESS, SceneAsset.class);
		assMan.load(WallObject.MODEL_ADDRESS_DOOR, SceneAsset.class);
		//Sprites
		spriteAddresses.forEach((String address) -> {
			if (address == null) return;
			assMan.load(address, Texture.class);
		});
		//Upgrade sprites
		assMan.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
		UpgradeRegistry.registeredUpgrades.values().forEach((upgradeClass) -> {
			if (Objects.requireNonNull(UpgradeRegistry.getUpgradeInstance(upgradeClass)).getModelAddress() == null)
				return;
			try {
				assMan.load(Objects.requireNonNull(UpgradeRegistry.getUpgradeInstance(upgradeClass)).getModelAddress(), SceneAsset.class);
			} catch (NullPointerException ignored) {
			}
		});

		// Sounds
		assMan.setLoader(Sound.class, ".mp3", new SoundLoader(new InternalFileHandleResolver()));
		for (Map.Entry<String, Sound> entry : sounds.entrySet()) {
			String address = entry.getKey();
			assMan.load(address, Sound.class);
		}

		//Particle effects
		Array<ParticleBatch<?>> generalBatches = new Array<>();
		generalBatches.add(generalBBParticleBatch);
		ParticleEffectLoader.ParticleEffectLoadParameter generalLoadParameter = new ParticleEffectLoader.ParticleEffectLoadParameter(generalBatches);

		Array<ParticleBatch<?>> paralyseBatches = new Array<>();
		paralyseBatches.add(paralyseBBParticleBatch);
		ParticleEffectLoader.ParticleEffectLoadParameter paralyseLoadParameter = new ParticleEffectLoader.ParticleEffectLoadParameter(paralyseBatches);

		assMan.load(SoulFireEffectManager.getStaticEffectAddress(), ParticleEffect.class, generalLoadParameter);
		assMan.load(ParalysisEffectManager.getStaticEffectAddress(), ParticleEffect.class, paralyseLoadParameter);
		assMan.load(DamageEffectManager.getStaticEffectAddress(), ParticleEffect.class, generalLoadParameter);
	}

	/**
	 * Game overlay rendering protocol
	 * */
	private void renderGameOverlay(){
		batch2d.begin();

		// Draws damage filter
		if (player.getDamageTimeout() > Config.DAMAGE_TIMEOUT - Config.DAMAGE_SCREEN_FLASH) {
			batch2d.draw(spriteAssets.get("ui/ingame/damage.png"), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}

		//Draws health
		for (int i = player.getMaxHealth() - 1; i>=0; i--){
			int x = Math.round((((float) Gdx.graphics.getWidth())/32F)+ (((float) (i * Gdx.graphics.getWidth()))/32F));
			int y = Gdx.graphics.getHeight() - 24 - Math.round(((float) Gdx.graphics.getHeight())/32F);
			if (i < player.getHealth()) {
				if (i == 0) {
					batch2d.draw(spriteAssets.get("ui/ingame/first_heart.png"), x, y, (float) (Gdx.graphics.getWidth()) /30 * Config.ASPECT_RATIO, (float) ((double) Gdx.graphics.getHeight() /30 *(Math.pow(Config.ASPECT_RATIO, -1))));
				} else {
					batch2d.draw(spriteAssets.get("ui/ingame/heart.png"), x, y, (float) (Gdx.graphics.getWidth()) / 30 * Config.ASPECT_RATIO, (float) ((double) Gdx.graphics.getHeight() / 30 * (Math.pow(Config.ASPECT_RATIO, -1))));
				}
			} else {
				batch2d.draw(spriteAssets.get("ui/ingame/empty_heart.png"), x, y, (float) (Gdx.graphics.getWidth()) /30 * Config.ASPECT_RATIO, (float) ((double) Gdx.graphics.getHeight() /30 *(Math.pow(Config.ASPECT_RATIO, -1))));
			}
		}

		//Draws upgrades
		UpgradeRegistry.registeredUpgrades.forEach((id,upgradeClass)->{
			if(upgradeClass == null||player.upgrades==null) return;
			int counter = 0;
			for(Upgrade upgrade : player.upgrades){
				if(upgrade == null) continue; // ensure upgrade exists
				if(upgrade.getIcon() == null) upgrade.setIconFromMap(spriteAssets); // ensure upgrade has icon
				if(upgrade.getClass().equals(upgradeClass)){ // if the upgrade matches the current upgrade class
					try {
						// draw upgrade icon to screen
						Vector2 pos = new Vector2(
								Math.round((float) Gdx.graphics.getWidth() - ((float) Gdx.graphics.getWidth()) / 24F) - (((float) ((counter%7)*Gdx.graphics.getWidth())) / 24F),
                                (float) (Gdx.graphics.getHeight() - (Math.floor(counter/7)+1)*(24 + Math.round(((float) Gdx.graphics.getHeight()) / 24F))));
						batch2d.draw(upgrade.getIcon(), pos.x, pos.y, (float) (Gdx.graphics.getHeight()) / 30 * Config.ASPECT_RATIO, ((float) Gdx.graphics.getHeight() / 30 * Config.ASPECT_RATIO ));
					} catch (Exception e){
						Gdx.app.error("Upgrade icon", "Failed to draw icon for upgrade "+id,e);
					}
				}
				counter++;
			}
		});

		if (Config.doRenderColliders) { // debug model viewer
			debugDrawer.begin(camera);
			dynamicsWorld.debugDrawWorld();
			debugDrawer.end();
		}

		batch2d.end(); // finish drawing and draw to screen
	}

	/**
	 * Called on the death of the player
	 * Sets the game state to the dead menu
	 * */
	public static void onPlayerDeath() {
		if (GameState.DEAD_MENU.getStage() != null) {
			GameState.DEAD_MENU.getStage().rebuild();
		}
		resetGame();
		setGameState(GameState.DEAD_MENU);
		Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
	}

	/**
	 * Resets important information about the game
	 */
	public static void resetGame() {
		Gdx.app.log("Main", "Player died");

		// reset levels
		mapMan.levels.clear();

		// recreate drawing utils
		batch.dispose();
		batch = new ModelBatch();
		threadAnimState = new int[]{0, 0, 0};

        // clear rendered scenes (keeps any scene lights, but this is currently unused)
        sceneMan.getRenderableProviders().clear();

		// reset entities map
		entities.clear(); // this must come after the above scene removal

        // reinit player
        player.destroy();
        player = new Player(Config.PLAYER_START_POSITION.cpy()); // this must come after the entity map clear
        player.baseUpgrade = BaseUpgrade.NONE;

		// recreate contact listener
		contactListener.dispose();
		contactListener = new CollisionListener();
		contactListener.enable();
	}

	/**
	 * Moves the player to a new level
	 * */
	public static void advanceLevel(){
		String playerSerial = player.serialize(); // store important information about the player
		player.destroy(); // delete player

		// restore information to new player instance
		player = new Player(Config.PLAYER_START_POSITION.cpy());
		player.setFromSerial(playerSerial);
		player.heal(1);

		// clear non-player entities
		entities.forEach((Integer i, Entity e) -> {
			if (e instanceof Player) return;
			entities.remove(i);
		});

		// clear active particles
		SoulFireEffectManager.effects.clear();
		DamageEffectManager.effects.clear();
		ParalysisEffectManager.effects.clear();
		particleSystem.removeAll();

		// generate new level
		mapMan.generateLevel();
	}

	/**
	 * Runs every frame to render the game
	 * */
	@Override
	public void render () {
		// clear the screen
		super.render();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// increment system time
		dT = (System.currentTimeMillis() - timeAtLastRender) / 1000f;
		timeAtLastRender = System.currentTimeMillis();

//		time += (long) (Gdx.graphics.getDeltaTime()*1000);

		// ensure the game state matches
		if(gameState!=null) {
			if (!gameState.toString().equals(gameStateCheck)) {
				setGameState(gameState);
				gameStateCheck = gameState.toString();
			}
		}
		if(gameState==GameState.LOADING){
			float progress = assMan.getProgress();
			renderLoadingFrame(progress);
			if(assMan.update()){
				assMan.finishLoading();
				postLevelLoad();
				setGameState(GameState.IN_GAME);
			}
			return;
		}

		// update UI animation
		if(activeUIAnim != null){
			if (animPreDraw != null) animPreDraw.run();
			animTime += Gdx.graphics.getDeltaTime(); // update progress time

			// draw current frame
			TextureRegion currentFrame = activeUIAnim.getKeyFrame(animTime);
			batch2d.begin();
			batch2d.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch2d.end();

			if(activeUIAnim.isAnimationFinished(animTime)){
				activeUIAnim = null;
				animTime = 0;
				if (animFinished != null) animFinished.run();
			}
			return;
		}

		if (gameState == GameState.PAUSED_MENU) {
			// render the world without stepping physics for a transparent effect
			sceneMan.update(Gdx.graphics.getDeltaTime());
			sceneMan.render();
		}

		if (gameState != null && gameState.getStage() != null) {
			gameState.getStage().update();
		}

		if (gameState == GameState.IN_GAME){
			// Update player input
			PlayerInput.update(dT);

			// Update camera pos
			camera.position.set(player.getPosition()).add(0, 0.2f, 0);
			camera.direction.set(player.getEulerRotation());

			// Draw player lantern
			playerLantern.set(playerLanternColour,player.getPosition().add(0, 0.5f, 0),10);

			// Update particles
			SoulFireEffectManager.update();
			DamageEffectManager.update();
			ParalysisEffectManager.update();

			entities.forEach((Integer id, Entity entity) -> {
				if (entity instanceof RoomInstance) {
					if (((RoomInstance) entity).getRoom().getType() == RoomTemplate.RoomType.HALLWAY_PLACEHOLDER) return;
				}
				if (entity instanceof IHasHealth) ((IHasHealth) entity).update(Gdx.graphics.getDeltaTime());
				if (entity instanceof UpgradeEntity) entity.update(Gdx.graphics.getDeltaTime());
			});

			entities.forEach((Integer id, Entity entity) -> {
				boolean flag = false;
				for (RenderableProvider renderableProvider : sceneMan.getRenderableProviders()) {
					if (renderableProvider.equals(entity.getScene())) {
						flag = true;
						break;
					}
				}

				if (entity.isRenderable && entity.getScene() != null && !flag){
					sceneMan.addScene(entity.getScene());
				}
				if(!entity.isRenderable && entity.getScene() != null && flag) {
					sceneMan.removeScene(entity.getScene());
				}
			});

			// draw scenes
			sceneMan.update(Gdx.graphics.getDeltaTime());
			sceneMan.render();

			batch.begin(camera);

			// draw particles
			particleSystem.update(dT);
			particleSystem.begin();
			particleSystem.draw();
			particleSystem.end();
			batch.render(particleSystem);
			batch.end();

			renderGameOverlay();

			//Update physics
			dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 15, 1/180f);

			// Update the attack animations
			if (attackAnimTime > 0 && player.baseUpgrade.SWING_ANIM != null) {
				// Update time
				attackAnimTime += Gdx.graphics.getDeltaTime();
				// Get current frame
				TextureRegion currentFrame = player.baseUpgrade.SWING_ANIM.getKeyFrame(attackAnimTime);
				batch2d.begin();
				batch2d.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				batch2d.end();
				// Reset time if animation is finished
				if(player.baseUpgrade.SWING_ANIM.isAnimationFinished(attackAnimTime)) attackAnimTime = 0;
			}

			if (crackAnimTime > 0 && player.baseUpgrade.CRACK_ANIM != null) {
				// Update time
				crackAnimTime += Gdx.graphics.getDeltaTime();
				// Get current frame
				TextureRegion currentFrame = player.baseUpgrade.CRACK_ANIM.getKeyFrame(crackAnimTime);
				batch2d.begin();
				batch2d.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				batch2d.end();
				// Reset time if animation is finished
				if(player.baseUpgrade.CRACK_ANIM.isAnimationFinished(crackAnimTime)) crackAnimTime = 0;
			}

			camera.update();

			// if enemies are all dead, tell the player they can advance
			if (entities.values().stream().filter(e -> e instanceof EnemyEntity).map(e -> (EnemyEntity) e).collect(Collectors.toCollection(ArrayList::new)).isEmpty()) {
				drawAdvanceText();
			}

			if (Player.timeSincePickup < Config.UPGRADE_TEXT_DISPLAY_TIMEOUT){
				batch2d.begin();
				layout.setText(uiFont, player.upgrades.getLast().getName());
				uiFont.draw(batch2d, player.upgrades.getLast().getName(), (float) Gdx.graphics.getWidth() / 2 - layout.width / 2, 150);
				layout.setText(uiFont, player.upgrades.getLast().getDescription());
				uiFont.draw(batch2d, player.upgrades.getLast().getDescription(), (float) Gdx.graphics.getWidth() / 2 - layout.width / 2, 100);
				batch2d.end();
			}
		}

		if (Config.debugMenu) {
			// update & draw debug menu
			buildDebugMenu();
			debug.act();
			debug.draw();
		}

		// check if player has died, and send to death menu
		if (player.getHealth() <= 0 && gameState == GameState.IN_GAME && player.baseUpgrade != BaseUpgrade.NONE) onPlayerDeath();
	}

	private void renderLoadingFrame(float progress) {
		Texture loadingItem = new Texture("ui/main/loading-item.png");
		batch2d.begin();
		batch2d.draw(new Texture("ui/main/loading-bg.png"),0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		for (int i = 0; i < (int) (progress * 75); i++){ // add one loading bar texture for every 1/75th of the loading complete
			batch2d.draw(loadingItem, (float) ((170 + (i * 4)) * Gdx.graphics.getWidth()) / 640, (float) (141 * Gdx.graphics.getHeight()) / 360, (float) (4 * Gdx.graphics.getWidth()) / 640, (float) (18 * Gdx.graphics.getHeight()) / 360);
		}
		batch2d.end();
	}

	/**
	 * Draws the advance text for level progression
	 * */
	private void drawAdvanceText() {
		batch2d.begin();
		uiFont.draw(batch2d, "Press " + Input.Keys.toString(Config.keys.get("advance")) + " to advance to the next level", 100, 100);
		batch2d.end();
	}

	/**
	 * Returns the current tracking time in milliseconds
	 * */
	public static long getTime() {
		return System.currentTimeMillis();
//		return time; // this is so incredibly wrong it only counts the frames where render is called so if you're standing still without debug menu open it will run 10x slower than if you do have it open. debug menu essentially forces incredibly quick updates because it has the bullet vectors that are always changing slightly
	}

	/**
	 * @param width The new width of the window
	 * @param height The new height of the window
	 * Called when the window is resized
	 * */
	@Override
	public void resize(int width, int height) {
		// update cameras
		super.resize(Config.TARGET_WIDTH, Config.TARGET_HEIGHT);
		viewport.update(Config.TARGET_WIDTH, Config.TARGET_HEIGHT);

		// update fonts
		buildFonts();

		// update menus
		for (GameState state : GameState.values()) {
			if (state.getStage() != null && state.getStage().isBuilt) state.getStage().rebuild();
		}

		Gdx.app.debug("Main", "Resized to "+width+"x"+height);
	}

	/**
	 * Disposes resources
	 * */
	@Override
	public void dispose() {
		super.dispose();
		for (GameState state : GameState.values()) {
			if (state.getStage() != null) state.getStage().dispose();
		}

		if (batch != null) batch.dispose();
		if (batch2d != null) batch2d.dispose();
		if (menuMusic != null) menuMusic.dispose();
		if (debug != null) debug.dispose();
		if (assMan != null) assMan.dispose();
		if (uiFont != null) uiFont.dispose();
		if (titleFont != null) titleFont.dispose();
        if (dynamicsWorld != null  && !dynamicsWorld.isDisposed()) dynamicsWorld.dispose();
        if (constraintSolver != null && !constraintSolver.isDisposed()) constraintSolver.dispose();
        if (broadphase != null && !broadphase.isDisposed()) broadphase.dispose();
        if (collisionConfig != null && !collisionConfig.isDisposed()) collisionConfig.dispose();
        if (dispatcher != null && !dispatcher.isDisposed()) dispatcher.dispose();
        if (debugDrawer != null && !debugDrawer.isDisposed()) debugDrawer.dispose();
		if (entities != null) entities.values().forEach(Entity::destroy);
    }

	/**
	 * @return SplashWorker the splash worker instance
	 * */
	public SplashWorker getSplashWorker() {
		return splashWorker;
	}

	/**
	 * @param splashWorker Target SplashWorker to run game
	 * */
	public void setSplashWorker(SplashWorker splashWorker) {
		this.splashWorker = splashWorker;
	}
}