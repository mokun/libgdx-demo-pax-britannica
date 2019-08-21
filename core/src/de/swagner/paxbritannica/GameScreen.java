package de.swagner.paxbritannica;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import java.util.Map;

import de.swagner.paxbritannica.background.BackgroundFXRenderer;
import de.swagner.paxbritannica.factory.EasyEnemyProduction;
import de.swagner.paxbritannica.factory.FactoryProduction;
import de.swagner.paxbritannica.factory.HardEnemyProduction;
import de.swagner.paxbritannica.factory.MediumEnemyProduction;
import de.swagner.paxbritannica.factory.PlayerProduction;
import de.swagner.paxbritannica.mainmenu.MainMenu;

//import com.badlogic.gdx.controllers.Controller;
//import com.badlogic.gdx.Controllers;

public class GameScreen extends DefaultScreen implements InputProcessor {

    private final static int FPS_HEIGHT = 15; //90
    private final static int FPS_WIDTH = 85; //45

	private final static int OFFSET = 20;
	private final static String NONE = "None";

    private double startTime = 0;
	private BackgroundFXRenderer backgroundFX = new BackgroundFXRenderer();

	private BitmapFont font;

	private float fade = 1.0f;
	private Sprite blackFade;
	private Sprite stouchAreaP1;
	private Sprite stouchAreaP2;
	private Sprite stouchAreaP3;
	private Sprite stouchAreaP4;
	private Sprite p1;
	private Sprite p2;
	private Sprite p3;
	private Sprite p4;

	private SpriteBatch fadeBatch;
	private SpriteBatch gameBatch;

	private FactoryProduction playerProduction;
	private FactoryProduction enemyProduction;

//	ShapeRenderer shapeRenderer = new ShapeRenderer();
	private OrthographicCamera cam;
	
	private boolean gameOver = false;
	private float gameOverTimer =5;
	
	private BoundingBox touchAreaP1;
	private BoundingBox touchAreaP2;
	private BoundingBox touchAreaP3;
	private BoundingBox touchAreaP4;
	private int pointerP1;
	private int pointerP2;
	private int pointerP3;
	private int pointerP4;
	private float touchFadeP1 = 1.0f;
	private float touchFadeP2 = 1.0f;
	private float touchFadeP3 = 1.0f;
	private float touchFadeP4 = 1.0f;
	private boolean touchedP1 = false;
	private boolean touchedP2 = false;
	private boolean touchedP3 = false;
	private boolean touchedP4 = false;

	private int numPlayers = 0;

	private int offset;

	private String teamString;

	private Ray collisionRay;

	private Array<Vector2> POSITIONS = new Array<Vector2>();

	private Vector2 CENTER = new Vector2(300, 180);

	private Controller pad;

	private int width = 800;
	private int height = 480;

	private Array<Integer> playerList;
	private Array<Integer> cpuList;

	private Array<Ship> factorys;

    private Map<Integer, Ship> factoryMap;

	private Map<Integer, Integer> teamMap;

    private Map<Integer, Integer> targetingMap;

    private boolean m1 = false;
	private boolean m2 = false;
	private boolean m3 = false;
	private boolean m4 = false;


	public GameScreen(Game game, Array<Integer> playerList, Array<Integer> cpuList) {
		super(game);
		this.playerList = playerList;
		this.cpuList = cpuList;

		if (playerList.contains(0, true)) {
			Gdx.app.log("GameScreen", "m1 is true");
			m1 = true;
		} else if (playerList.contains(1, true)) {
			Gdx.app.log("GameScreen", "m2 is true");
			m2 = true;
		} else if (playerList.contains(2, true)) {
			Gdx.app.log("GameScreen", "m3 is true");
			m3 = true;
		} else if (playerList.contains(3, true)) {
			Gdx.app.log("GameScreen", "m4 is true");
			m4 = true;
		}

		for (Controller c : Controllers.getControllers()) {
            if(c.getName().contains("Xbox") || c.getName().contains("360")) {
                pad = c;
				Gdx.app.log("GameScreen", c.getName());
				System.out.println(c.getName());
            }
        }

        //if(xbox==null){
            //no xbox controller found
            //we could fallback to the first controller, like so:
            //pad = controllers.get(0)
        //}


		GameInstance.getInstance().setPlayerList(playerList);
		GameInstance.getInstance().setCpuList(cpuList);
		for (int i : playerList)
			Gdx.app.log("GameScreen", "Player " + (i + 1) + " initialized.");
		for (int i : cpuList)
			Gdx.app.log("GameScreen", "CPU " + (i + 1) + " initialized.");


		// read teamMap
		teamMap = GameInstance.getInstance().teamMap;

		// read targetingMap
		targetingMap = GameInstance.getInstance().targetingMap;

		//font = new BitmapFont();

        // Set up generator for ttf creation
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/BLKCHCRY.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 18;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:";

        font = generator.generateFont(parameter);
        generator.dispose();

		//font = new BitmapFont(Gdx.files.internal("data/default.fnt"),Gdx.files.internal("data/default_00.png"),false);
		//font.set.setScale(.2f);

		Gdx.input.setCatchBackKey( true );
		Gdx.input.setInputProcessor(this);

		cam = new OrthographicCamera(width, height);

		cam.position.x = 200; //400;
		cam.position.y = 120; //240;
		cam.update();
		
		numPlayers = playerList.size;
		
		if(numPlayers==1) {
			touchAreaP1 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+(this.width), -((this.height-480)/2)+this.height, 0));
		} else if(numPlayers == 2) {
			touchAreaP1 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+this.height, 0));
			touchAreaP2 = new BoundingBox(new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+this.width, -((this.height-480)/2)+this.height, 0));
		} else if(numPlayers == 3) {
			touchAreaP1 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+(this.height/2), 0));
			touchAreaP2 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2)+(this.height/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+this.height, 0));
			touchAreaP3 = new BoundingBox(new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+this.width, -((this.height-480)/2)+this.height, 0));
		} else if(numPlayers == 4) {
			touchAreaP1 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+(this.height/2), 0));
			touchAreaP2 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2)+(this.height/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+this.height, 0));
			touchAreaP3 = new BoundingBox(new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+this.width, -((this.height-480)/2)+(this.height/2), 0));
			touchAreaP4 = new BoundingBox(new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+(this.height/2), 0),new Vector3(-((this.width-800)/2)+this.width, -((this.height-480)/2)+this.height, 0));
		}

//		camera = new OrthographicCamera(800, 480);
//		camera.translate(400, 240, 0);

		if(playerList.size + cpuList.size != 3) {
			POSITIONS.add(new Vector2(150, 180));
			POSITIONS.add(new Vector2(450, 180));
			POSITIONS.add(new Vector2(300, 335));
			POSITIONS.add(new Vector2(300, 25));
		} else {
			POSITIONS.add(new Vector2(170, 92));
			POSITIONS.add(new Vector2(432, 100));
			POSITIONS.add(new Vector2(300, 335));
		}
		
		
		// Fade
		blackFade = Resources.getInstance().blackFade;
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 2, 2);

		stouchAreaP1 = Resources.getInstance().touchArea1;
		stouchAreaP2 = Resources.getInstance().touchArea2;
		stouchAreaP3 = Resources.getInstance().touchArea3;
		stouchAreaP4 = Resources.getInstance().touchArea4;

		if (playerList.size > 0 && playerList.get(0) == 0) {
			p1 = Resources.getInstance().factoryP1Small;
		} else if (playerList.size > 0 && playerList.get(0) == 1) {
			p1 = Resources.getInstance().factoryP2Small;
		} else if (playerList.size > 0 && playerList.get(0) == 2) {
			p1 = Resources.getInstance().factoryP3Small;
		} else if (playerList.size > 0 && playerList.get(0) == 3) {
			p1 = Resources.getInstance().factoryP4Small;		
		}

		if (playerList.size > 1 && playerList.get(1) == 0) {
			p2 = Resources.getInstance().factoryP1Small;
		} else if (playerList.size > 1 && playerList.get(1) == 1) {
			p2 = Resources.getInstance().factoryP2Small;
		} else if (playerList.size > 1 && playerList.get(1) == 2) {
			p2 = Resources.getInstance().factoryP3Small;
		} else if (playerList.size > 1 && playerList.get(1) == 3) {
			p2 = Resources.getInstance().factoryP4Small;		
		}

		if (playerList.size > 2 && playerList.get(2) == 0) {
			p3 = Resources.getInstance().factoryP1Small;
		} else if (playerList.size > 2 && playerList.get(2) == 1) {
			p3 = Resources.getInstance().factoryP2Small;
		} else if (playerList.size > 2 && playerList.get(2) == 2) {
			p3 = Resources.getInstance().factoryP3Small;
		} else if (playerList.size > 2 && playerList.get(2) == 3) {
			p3 = Resources.getInstance().factoryP4Small;		
		}

		if (playerList.size > 3 && playerList.get(3) == 0) {
			p4 = Resources.getInstance().factoryP1Small;
		} else if (playerList.size > 3 && playerList.get(3) == 1) {
			p4 = Resources.getInstance().factoryP2Small;
		} else if (playerList.size > 3 && playerList.get(3) == 2) {
			p4 = Resources.getInstance().factoryP3Small;
		} else if (playerList.size > 3 && playerList.get(3) == 3) {
			p4 = Resources.getInstance().factoryP4Small;		
		}
		
		if(playerList.size>0) p1.setScale(.2f);
		if(playerList.size>1) p2.setScale(.2f);
		if(playerList.size>2) p3.setScale(.2f);
		if(playerList.size>3) p4.setScale(.2f);
		
		if(playerList.size>0) p1.rotate(-90);
		if(playerList.size>1) p2.rotate(90);
		if(playerList.size>2) p3.rotate(-90);
		if(playerList.size>3) p4.rotate(90);

		stouchAreaP1.setRotation(-90);
		stouchAreaP2.setRotation(90);
		stouchAreaP1.setRotation(-90);
		stouchAreaP2.setRotation(90);
		
		gameBatch = new SpriteBatch();
		gameBatch.getProjectionMatrix().set(cam.combined);


		// init player positions
//		Array<Vector2> positons = generatePositions(numPlayers + 1);
		
		int currentPos = 0;
		int teamID = 0;

		// Set up a local instance to save from calling the singleton everytime
		//if (factorys == null)
			factorys = GameInstance.getInstance().factorys;

        //if (factoryMap == null)
            factoryMap = GameInstance.getInstance().factoryMap;

		for(int i=0;i < playerList.size; ++i) {
			Vector2 temp1 = new Vector2(POSITIONS.get(currentPos).x, POSITIONS.get(currentPos).y);
			Vector2 temp2 = new Vector2(POSITIONS.get(currentPos).x, POSITIONS.get(currentPos).y);
			Vector2 facing = new Vector2(-temp1.sub(CENTER).y, temp2.sub(CENTER).x).nor();


//			// Set up teamID, Team 1 consists of player 1 & 2; team 2 consists of player 3 & 4.
//			if (playerList.get(i) == 1 || playerList.get(i) == 2)
//				teamID = 1;
//			else if (playerList.get(i) == 3 || playerList.get(i) == 4)
//				teamID = 2;
//
//			teamMap.put(i, teamID);

			// Read the teamID of this player
			teamID = teamMap.get(playerList.get(i));

			playerProduction = new PlayerProduction(playerList.get(i), teamID, POSITIONS.get(currentPos), facing);

            factorys.add(playerProduction);
			factoryMap.put(playerList.get(i), playerProduction);
			++currentPos;
		}
		
		for(int i=0; i < cpuList.size; ++i) {
			Vector2 temp1 = new Vector2(POSITIONS.get(currentPos).x, POSITIONS.get(currentPos).y);
			Vector2 temp2 = new Vector2(POSITIONS.get(currentPos).x, POSITIONS.get(currentPos).y);
			Vector2 facing = new Vector2(-temp1.sub(CENTER).y, temp2.sub(CENTER).x).nor();

			// Set up teamID, Team 1 consists of player 1 & 2; team 2 consists of player 3 & 4.
//			if (cpuList.get(i) == 1 || cpuList.get(i) == 2)
//				teamID = 1;
//			else if (cpuList.get(i) == 3 || cpuList.get(i) == 4)
//				teamID = 2;
//
//			teamMap.put(i, teamID);

			// Read the teamID of this player
			teamID = teamMap.get(cpuList.get(i));

			if(GameInstance.getInstance().difficultyConfig == 0) {
				enemyProduction = new EasyEnemyProduction(cpuList.get(i), teamID, POSITIONS.get(currentPos), facing);
			} else if(GameInstance.getInstance().difficultyConfig == 1) {
				enemyProduction = new MediumEnemyProduction(cpuList.get(i), teamID, POSITIONS.get(currentPos), facing);
			} else {
				enemyProduction = new HardEnemyProduction(cpuList.get(i), teamID, POSITIONS.get(currentPos), facing);
			}

            factorys.add(enemyProduction);
			factoryMap.put(cpuList.get(i), enemyProduction);

			++currentPos;
		}

//		// add cpu if only one player plays
//		if (idP2 == -1) {
//			temp1 = new Vector2(POSITIONS.get(1).x, POSITIONS.get(1).y);
//			temp2 = new Vector2(POSITIONS.get(1).x, POSITIONS.get(1).y);
//			facing = new Vector2(-temp1.sub(CENTER).y, temp2.sub(CENTER).x).nor();
//			if(GameInstance.getInstance().difficultyConfig == 0) {
//				enemyProduction = new EasyEnemyProduction((idP1+1)%4, POSITIONS.get(1), facing);
//			} else if(GameInstance.getInstance().difficultyConfig == 1) {
//				enemyProduction = new MediumEnemyProduction((idP1+1)%4, POSITIONS.get(1), facing);
//			} else {
//				enemyProduction = new HardEnemyProduction((idP1+1)%4, POSITIONS.get(1), facing);
//			}
//			GameInstance.getInstance().factorys.add(enemyProduction);
//			touchedP2 = true;
//			touchFadeP2 = 0;
//			
//			temp1 = new Vector2(POSITIONS.get(2).x, POSITIONS.get(2).y);
//			temp2 = new Vector2(POSITIONS.get(2).x, POSITIONS.get(2).y);
//			facing = new Vector2(-temp1.sub(CENTER).y, temp2.sub(CENTER).x).nor();
//			if(GameInstance.getInstance().difficultyConfig == 0) {
//				enemyProduction = new EasyEnemyProduction((idP1+2)%4, POSITIONS.get(2), facing);
//			} else if(GameInstance.getInstance().difficultyConfig == 1) {
//				enemyProduction = new MediumEnemyProduction((idP1+2)%4, POSITIONS.get(2), facing);
//			} else {
//				enemyProduction = new HardEnemyProduction((idP1+2)%4, POSITIONS.get(2), facing);
//			}
//			GameInstance.getInstance().factorys.add(enemyProduction);
//			touchedP2 = true;
//			touchFadeP2 = 0;
//			
//			temp1 = new Vector2(POSITIONS.get(3).x, POSITIONS.get(3).y);
//			temp2 = new Vector2(POSITIONS.get(3).x, POSITIONS.get(3).y);
//			facing = new Vector2(-temp1.sub(CENTER).y, temp2.sub(CENTER).x).nor();
//			if(GameInstance.getInstance().difficultyConfig == 0) {
//				enemyProduction = new EasyEnemyProduction((idP1+3)%4, POSITIONS.get(3), facing);
//			} else if(GameInstance.getInstance().difficultyConfig == 1) {
//				enemyProduction = new MediumEnemyProduction((idP1+3)%4, POSITIONS.get(3), facing);
//			} else {
//				enemyProduction = new HardEnemyProduction((idP1+3)%4, POSITIONS.get(3), facing);
//			}
//			GameInstance.getInstance().factorys.add(enemyProduction);
//			touchedP2 = true;
//			touchFadeP2 = 0;
//		} else {
//			temp1 = new Vector2(POSITIONS.get(1).x, POSITIONS.get(1).y);
//			temp2 = new Vector2(POSITIONS.get(1).x, POSITIONS.get(1).y);
//			facing = new Vector2(-temp1.sub(CENTER).y, temp2.sub(CENTER).x).nor();
//			playerProduction = new PlayerProduction(idP2, POSITIONS.get(1), facing);
//			GameInstance.getInstance().factorys.add(playerProduction);
//		}
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

	}
	
	Vector3 tmp = new Vector3();
	
	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;

		if (width == 480 && height == 320) {
			cam = new OrthographicCamera(700, 466);
			this.width = 700;
			this.height = 466;
		} else if (width == 320 && height == 240) {
			cam = new OrthographicCamera(700, 525);
			this.width = 700;
			this.height = 525;
		} else if (width == 400 && height == 240) {
			cam = new OrthographicCamera(800, 480);
			this.width = 800;
			this.height = 480;
		} else if (width == 432 && height == 240) {
			cam = new OrthographicCamera(700, 389);
			this.width = 700;
			this.height = 389;
		} else if (width == 960 && height == 640) {
			cam = new OrthographicCamera(800, 533);
			this.width = 800;
			this.height = 533;
		} else if (width == 1024 && height == 768) {
			cam = new OrthographicCamera(800, 600);
			this.width = 800;
			this.height = 600;
		} else if (width == 1366 && height == 768) {
			cam = new OrthographicCamera(1280, 720);
			this.width = 1280;
			this.height = 720;
		} else if (width == 1366 && height == 720) {
			cam = new OrthographicCamera(1280, 675);
			this.width = 1280;
			this.height = 675;
		} else if (width > 1366) {
			cam = new OrthographicCamera(1280, 800);
			this.width = 1280;
			this.height = 800;
		} else {
			cam = new OrthographicCamera(width, height);
			this.width = 1280;
			this.height = 800;
		}
		cam.position.x = 400;
		cam.position.y = 240;
		cam.update();	
		backgroundFX.resize(width, height);
		gameBatch.getProjectionMatrix().set(cam.combined);		
		
		if (numPlayers == 1) {
			p1.setRotation(-90);
			
			stouchAreaP1.setRotation(-90);
			
			touchAreaP1 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+(this.width), -((this.height-480)/2)+this.height, 0));
			
			stouchAreaP1.setPosition(touchAreaP1.min.x, touchAreaP1.getCenterY()-40);
			p1.setPosition(touchAreaP1.min.x+10, touchAreaP1.getCenterX()-105);

		} else if(numPlayers == 2) {
			p1.setRotation(-90);
			p2.setRotation(90);

			stouchAreaP1.setRotation(-90);
			stouchAreaP2.setRotation(90);
			
			touchAreaP1 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+this.height, 0));
			touchAreaP2 = new BoundingBox(new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+this.width, -((this.height-480)/2)+this.height, 0));
			
			stouchAreaP1.setPosition(touchAreaP1.min.x, touchAreaP1.getCenterY()-40);
			p1.setPosition(touchAreaP1.min.x+10, touchAreaP1.getCenterY()-105);
			stouchAreaP2.setPosition(touchAreaP2.max.x - 170, touchAreaP2.getCenterY()-40);
			p2.setPosition(touchAreaP2.max.x-190, touchAreaP2.getCenterY()-15);

		} else if(numPlayers == 3) {
			p1.setRotation(-90);
			p2.setRotation(-90);
			p3.setRotation(90);

			stouchAreaP1.setRotation(-90);
			stouchAreaP2.setRotation(-90);
			stouchAreaP3.setRotation(90);
			
			touchAreaP1 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+(this.height/2), 0));
			touchAreaP2 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2)+(this.height/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+this.height, 0));
			touchAreaP3 = new BoundingBox(new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+this.width, -((this.height-480)/2)+this.height, 0));
			
			stouchAreaP1.setPosition(touchAreaP1.min.x, touchAreaP1.getCenterY()-40);
			p1.setPosition(touchAreaP1.min.x+10, touchAreaP1.getCenterY()-105);
			stouchAreaP2.setPosition(touchAreaP2.min.x, touchAreaP2.getCenterY()-40);
			p2.setPosition(touchAreaP2.min.x+10, touchAreaP2.getCenterY()-105);
			stouchAreaP3.setPosition(touchAreaP3.max.x - 170, touchAreaP3.getCenterY()-40);
			p3.setPosition(touchAreaP3.max.x-190, touchAreaP3.getCenterY()-15);

		} else if(numPlayers == 4) {
			p1.setRotation(-90);
			p2.setRotation(-90);
			p3.setRotation(90);
			p4.setRotation(90);

			stouchAreaP1.setRotation(-90);
			stouchAreaP2.setRotation(-90);
			stouchAreaP3.setRotation(90);
			stouchAreaP4.setRotation(90);
			
			touchAreaP1 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+(this.height/2), 0));
			touchAreaP2 = new BoundingBox(new Vector3(-((this.width-800)/2), -((this.height-480)/2)+(this.height/2), 0),new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+this.height, 0));
			touchAreaP3 = new BoundingBox(new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2), 0),new Vector3(-((this.width-800)/2)+this.width, -((this.height-480)/2)+(this.height/2), 0));
			touchAreaP4 = new BoundingBox(new Vector3(-((this.width-800)/2)+(this.width/2), -((this.height-480)/2)+(this.height/2), 0),new Vector3(-((this.width-800)/2)+this.width, -((this.height-480)/2)+this.height, 0));
			
			stouchAreaP1.setPosition(touchAreaP1.min.x, touchAreaP1.getCenterY()-40);
			p1.setPosition(touchAreaP1.min.x+10, touchAreaP1.getCenterY()-105);
			stouchAreaP2.setPosition(touchAreaP2.min.x, touchAreaP2.getCenterY()-40);
			p2.setPosition(touchAreaP2.min.x+10, touchAreaP2.getCenterY()-105);
			stouchAreaP3.setPosition(touchAreaP3.max.x - 170, touchAreaP3.getCenterY()-40);
			p3.setPosition(touchAreaP3.max.x-190, touchAreaP3.getCenterY()-15);
			stouchAreaP4.setPosition(touchAreaP4.max.x - 170, touchAreaP4.getCenterY()-40);
			p4.setPosition(touchAreaP4.max.x-190, touchAreaP4.getCenterY()-15);
		}
	}

	public Array<Vector2> generatePositions(int n) {
		Array<Vector2> positions = new Array<Vector2>();
		for (int i = 1; i <= n; ++i) {
			positions.add(new Vector2(MathUtils.cos(i / n), MathUtils.sin(i / n)).scl(200));
		}
		return positions;
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		delta = Math.min(0.06f, delta);
		
		backgroundFX.render();		

		Collision.collisionCheck();

		gameBatch.begin();
		// Bubbles
		GameInstance.getInstance().bubbleParticles.draw(gameBatch);
		GameInstance.getInstance().bigBubbleParticles.draw(gameBatch);

		// Factorys
		for (Ship ship : factorys) {
			if (ship.alive) {
				ship.draw(gameBatch);
			} else {
				factorys.removeValue(ship, true);
                factoryMap.remove(ship);

                if (playerList.contains(ship.getID(), true)) {
					playerList.removeValue(ship.getID(), true);
					GameInstance.getInstance().getPlayerList().removeValue(ship.getID(), true);
				}
				else if (cpuList.contains(ship.getID(), true)) {
					cpuList.removeValue(ship.getID(), true);
					GameInstance.getInstance().getCpuList().removeValue(ship.getID(), true);
				}

                //GameInstance.getInstance().removeFactory(ship.getID());
				if(factorys.size < 2) gameOver = true;
			}
		}
		// Frigate
		for (Ship ship : GameInstance.getInstance().frigates) {
			if (ship.alive) {
				ship.draw(gameBatch);
			} else {
				GameInstance.getInstance().frigates.removeValue(ship, true);
			}
		}
		// Bomber
		for (Ship ship : GameInstance.getInstance().bombers) {
			if (ship.alive) {
				ship.draw(gameBatch);
			} else {
				GameInstance.getInstance().bombers.removeValue(ship, true);
			}
		}
		// Fighter
		for (Ship ship : GameInstance.getInstance().fighters) {
			if (ship.alive) {
				ship.draw(gameBatch);
			} else {
				GameInstance.getInstance().fighters.removeValue(ship, true);
			}
		}
		
		// Laser
		for (Ship ship : GameInstance.getInstance().bullets) {
			if (ship.alive) {
				ship.draw(gameBatch);
			} else {
				GameInstance.getInstance().bullets.removeValue((Bullet) ship, true);
			}
		}

		// Explosions
		GameInstance.getInstance().sparkParticles.draw(gameBatch);
		GameInstance.getInstance().explosionParticles.draw(gameBatch);

        font.setColor(Color.WHITE);
		//font.draw(gameBatch, "FPS : " + Gdx.graphics.getFramesPerSecond(), -90, height - FPS_HEIGHT);
        //font.drawMultiLine(gameBatch, "your text", x, y, widthOfTheLine, HAlignment.LEFT);

		font.draw(gameBatch, "Health" , -FPS_WIDTH, FPS_HEIGHT * 2);
		font.draw(gameBatch, "Kills" ,5, FPS_HEIGHT * 2);
		font.draw(gameBatch, "Score" ,FPS_WIDTH, FPS_HEIGHT * 2);
		font.draw(gameBatch, "Target" ,FPS_WIDTH*2, FPS_HEIGHT * 2);
		font.draw(gameBatch, "Team" ,FPS_WIDTH*3, FPS_HEIGHT * 2);

        int[][] counts = GameInstance.getInstance().getCounts();

		for (int i = 0; i < 4; i++) {

			if (i == 0)
                font.setColor(Color.CYAN);
			else if (i == 1)
				font.setColor(Color.RED);
			else if (i == 2)
				font.setColor(Color.GREEN);
			else
				font.setColor(Color.YELLOW);

            if (factoryMap.containsKey(i)) {
                //Gdx.app.log("[GS] ", "factoryMap.get(i-1) : " + factoryMap.get(i-1));
                //Gdx.app.log("[GS] ", "factoryMap.size() : " + factoryMap.size());
                //Gdx.app.log("[GS] ", "factorys.size : " + factorys.size);

                double health = factoryMap.get(i).health()* 100.0;

                font.draw(gameBatch,
		        				Math.round(health * 10.0) / 10.0 + " %",
						-FPS_WIDTH + 5, FPS_HEIGHT * 2 - (i + 1) * 20);

                font.draw(gameBatch,
						counts[i][0]
								+ "   " + counts[i][1]
								+ "   " + counts[i][2]
                                //+ " " + counts[3]
                                + "]",
						0, FPS_HEIGHT * 2 - 5 - (i + 1) * 20);

                font.draw(gameBatch,
						" " + counts[i][3],
						FPS_WIDTH + 5, FPS_HEIGHT * 2 - (i + 1) * 20);

				int j = targetingMap.get(i);

				String target = null;

				if (j == 0) {
					font.setColor(Color.CYAN);
					target = "Cyan";
				} else if (j == 1) {
					font.setColor(Color.RED);
					target = "Red";
				} else if (j == 2) {
					font.setColor(Color.GREEN);
					target = "Green";
				} else if (j == 3) {
					font.setColor(Color.YELLOW);
					target = "Yellow";
				} else {
					font.setColor(Color.WHITE);
					target = "Any";
				}

				font.draw(gameBatch,
						" " + target,
						FPS_WIDTH * 2 + 5, FPS_HEIGHT * 2 - (i + 1) * 20);


				int tid = teamMap.get(i);
				if (tid != 0) {
					teamString = tid + "";
					offset = 0;
				}
				else {
					teamString = NONE;
					offset = OFFSET;
				}

				font.setColor(Color.GOLDENROD);
				font.draw(gameBatch,
						teamString,
						FPS_WIDTH * 3 + 15 - offset, FPS_HEIGHT * 2 - (i + 1) * 20);

			}
        }

		gameBatch.end();

		// Show touch area notification
		// Player 1
		if(numPlayers > 0 && touchedP1) {
			touchFadeP1 = Math.max(touchFadeP1 - delta / 2.f, 0);
		}
		if(numPlayers > 0 && (!touchedP1 || touchFadeP1>0)) {
			gameBatch.begin();
			stouchAreaP1.setColor(stouchAreaP1.getColor().r, stouchAreaP1.getColor().g, stouchAreaP1.getColor().b, touchFadeP1);
			stouchAreaP1.draw(gameBatch);
			p1.setColor(p1.getColor().r, p1.getColor().g, p1.getColor().b, touchFadeP1);
			p1.draw(gameBatch);
			gameBatch.end();
		}

		// Player 2
		if(numPlayers > 1 && touchedP2) {
			touchFadeP2 = Math.max(touchFadeP2 - delta / 2.f, 0);
		}
		if(numPlayers > 1 && (!touchedP2 || touchFadeP2>0)) {
			gameBatch.begin();
			stouchAreaP2.setColor(stouchAreaP2.getColor().r, stouchAreaP2.getColor().g, stouchAreaP2.getColor().b, touchFadeP2);
			stouchAreaP2.draw(gameBatch);
			p2.setColor(p2.getColor().r, p2.getColor().g, p2.getColor().b, touchFadeP2);
			p2.draw(gameBatch);
			gameBatch.end();
		}

		// Player 3
		if(numPlayers > 2 && touchedP3) {
			touchFadeP3 = Math.max(touchFadeP3 - delta / 2.f, 0);
		}
		if(numPlayers > 2 && (!touchedP3 || touchFadeP3>0)) {
			gameBatch.begin();
			stouchAreaP3.setColor(stouchAreaP3.getColor().r, stouchAreaP3.getColor().g, stouchAreaP3.getColor().b, touchFadeP3);
			stouchAreaP3.draw(gameBatch);
			p3.setColor(p3.getColor().r, p3.getColor().g, p3.getColor().b, touchFadeP3);
			p3.draw(gameBatch);
			gameBatch.end();
		}

		// Player 4
		if(numPlayers > 3 && touchedP4) {
			touchFadeP4 = Math.max(touchFadeP4 - delta / 2.f, 0);
		}
		if(numPlayers > 3 && (!touchedP4 || touchFadeP4>0)) {
			gameBatch.begin();
			stouchAreaP4.setColor(stouchAreaP4.getColor().r, stouchAreaP4.getColor().g, stouchAreaP4.getColor().b, touchFadeP4);
			stouchAreaP4.draw(gameBatch);
			p4.setColor(p4.getColor().r, p4.getColor().g, p4.getColor().b, touchFadeP4);
			p4.draw(gameBatch);
			gameBatch.end();
		}
		
		if (!gameOver && fade > 0 && fade < 100) {
			fade = Math.max(fade - delta / 2.f, 0);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g, blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
		}
		
		if(gameOver) {
			gameOverTimer -= delta;
		}
		if (gameOver && gameOverTimer <= 0) {
			fade = Math.min(fade + delta / 2.f, 1);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g, blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
			if(fade>=1) game.setScreen(new MainMenu(game));
		}
		
//		shapeRenderer.setProjectionMatrix(cam.combined);
//		 
//		 shapeRenderer.begin(ShapeType.Line);
//		 shapeRenderer.setColor(1, 1, 0, 1);
//		 shapeRenderer.line(touchAreaP1.min.x, touchAreaP1.min.y, touchAreaP1.max.x, touchAreaP1.max.y);
//		 shapeRenderer.line(touchAreaP2.min.x, touchAreaP2.min.y, touchAreaP2.max.x, touchAreaP2.max.y);
//		 shapeRenderer.line(touchAreaP3.min.x, touchAreaP3.min.y, touchAreaP3.max.x, touchAreaP3.max.y);
//		 shapeRenderer.line(touchAreaP4.min.x, touchAreaP4.min.y, touchAreaP4.max.x, touchAreaP4.max.y);
//		 shapeRenderer.end();
		 
	}

	@Override
	public void hide() {
		
	}

	public boolean targetExisted(int target_id) {
		if (playerList.contains(target_id, true))
			return true;
        else return cpuList.contains(target_id, true);

	}

	// Change the target color to the next
	public void rotateTarget(int player_id) {
		// target_id is 0, 1, 2, 3, or 4 (Note that 0 means all ships)
		int target_id = targetingMap.get(player_id);
		target_id++;
		if (target_id > 4)
			// reset it back to zero
			target_id = 0;
		if (target_id != 4)
			// go to the next color
			target_id = determineTarget(player_id, target_id);
		//Gdx.app.log("[GameScreen]", "rotateTarget() target_id : " + target_id);
				// update the target_id
		targetingMap.put(player_id, target_id);
	}

	public int determineTarget(int player_id, int target_id) {
		// repeat this process again
		if (targetExisted(target_id)) { // if the target playger is not dead
			//Gdx.app.log("[GameScreen]", "targetExisted() is true; target_id : " + target_id);
			if (target_id != player_id) { // if this is player's own color
				//Gdx.app.log("[GameScreen]", "target_id is not the same as player_id; target_id : " + target_id);
				if (teamMap.get(player_id) == GameInstance.TARGET_ANY
						|| GameInstance.getInstance().isFinalEpicBattle()
						|| teamMap.get(player_id) != teamMap.get(target_id)){ // if this color is on the same team
					//Gdx.app.log("[GameScreen]", "they are not the same team; target_id : " + target_id);
					return target_id;
				}
				else {
					target_id = getNextTarget(player_id, target_id);
				}
			}
			else {
				target_id = getNextTarget(player_id, target_id);
			}

		}
		else {
			target_id = getNextTarget(player_id, target_id);
		}

		return target_id;
	}

	public int getNextTarget(int player_id, int target_id) {
		target_id++;
		if (target_id > 4)
			// reset it back to zero
			target_id = 0;
		if (target_id != 4)
			// call recursively to go to the next color
			return determineTarget(player_id, target_id);
		else
			return target_id;
	}

	public void shieldUp(int player_id, boolean value) {
		((FactoryProduction) (factoryMap.get(player_id))).setShieldUp(value);
	}

	@Override
	public boolean keyDown(int keycode) {
//		if(keycode == Input.Keys.BACK) {
//			gameOver = true;
//			gameOverTimer=0;
//		}
		
		if(keycode == Input.Keys.ESCAPE) {
			gameOver = true;
			gameOverTimer=0;
		}

		if (factorys.size > 0) {
			// Player 1
			if (factoryMap.get(0) != null) {
				if (keycode == Input.Keys.A) {
					((FactoryProduction) factoryMap.get(0)).button_held = true;
					touchedP1 = true;
				}

				if (keycode == Input.Keys.S) {
					shieldUp(0, true);
				} else
					shieldUp(0, false);

				if (keycode == Input.Keys.D) {
					rotateTarget(0);
				}
			}

			// Player 2
			if (factoryMap.get(1) != null) {
				if (keycode == Input.Keys.J) {
					((FactoryProduction) factoryMap.get(1)).button_held = true;
					touchedP2 = true;
				}

				if (keycode == Input.Keys.K) {
					shieldUp(1, true);
				} else
					shieldUp(1, false);

				if (keycode == Input.Keys.L) {
					rotateTarget(1);
				}
			}
			// Player 3
			if (factoryMap.get(2) != null) {
				if (keycode == Input.Keys.LEFT) {
					((FactoryProduction) factoryMap.get(2)).button_held = true;
					touchedP3 = true;
				}

				if (keycode == Input.Keys.DOWN) {
					shieldUp(2, true);
				} else
					shieldUp(2, false);

				if (keycode == Input.Keys.RIGHT) {
					rotateTarget(2);
				}
			}

			// Player 4
			if (factoryMap.get(3) != null) {
				if (keycode == Input.Keys.NUMPAD_4) {
					((FactoryProduction) factoryMap.get(3)).button_held = true;
					touchedP4 = true;
				}

				if (keycode == Input.Keys.NUMPAD_2) {
					shieldUp(3, true);
				} else
					shieldUp(3, false);

				if (keycode == Input.Keys.NUMPAD_6) {
					rotateTarget(3);
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (factorys.size > 0) {
			if (keycode == Input.Keys.A && factoryMap.get(0) != null) {
				((FactoryProduction) factoryMap.get(0)).button_held = false;
			}
			if (keycode == Input.Keys.J && factoryMap.get(1) != null) {
				((FactoryProduction) factoryMap.get(1)).button_held = false;
			}
			if (keycode == Input.Keys.LEFT && factoryMap.get(2) != null) {
				((FactoryProduction) factoryMap.get(2)).button_held = false;
			}
			if (keycode == Input.Keys.NUMPAD_4 && factoryMap.get(3) != null) {
				((FactoryProduction) factoryMap.get(3)).button_held = false;
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		collisionRay = cam.getPickRay(x, y);

		if (collisionRay != null && factorys.size > 0) {
			// Player 1
			if (m1 && !m2 && !m3 && !m4 && factoryMap.get(0) != null) {
//				Gdx.app.log("GameScreen", " button : " + button);
//				Gdx.app.log("GameScreen", " touchedP1 : " + touchedP1);
//				Gdx.app.log("GameScreen", " Collided P1 : " + Intersector.intersectRayBoundsFast(collisionRay, touchAreaP1));
//				Gdx.app.log("GameScreen", " pointer : " + pointer);
//				Gdx.app.log("GameScreen", " pointerP1 : " + pointerP1);

				if (button == Input.Buttons.RIGHT) {
//					Gdx.app.log("GameScreen", "rotateTarget(1)");
					rotateTarget(0);
				}

				if (button == Input.Buttons.LEFT
//						||	(touchAreaP1 != null
//						&& Intersector.intersectRayBoundsFast(collisionRay, touchAreaP1))
				) {
					((FactoryProduction) factoryMap.get(0)).button_held = true;
					pointerP1 = pointer;
					touchedP1 = true;
				}

				if (button == Input.Buttons.MIDDLE) {
					shieldUp(0, true);
				}
//				else
//					shieldUp(0, false);

			}

			// Player 2
			if (m2 && !m1 && !m3 && !m4 && factoryMap.get(1) != null) {

				if (button == Input.Buttons.RIGHT) {
					//Gdx.app.log("GameScreen", "rotateTarget(2)");
					rotateTarget(1);
				}

				if (button == Input.Buttons.LEFT
//						|| (touchAreaP2 != null
//						&& Intersector.intersectRayBoundsFast(collisionRay, touchAreaP2))
				) {
					((FactoryProduction) factoryMap.get(1)).button_held = true;
					pointerP2 = pointer;
					touchedP2 = true;
				}

				if (button == Input.Buttons.MIDDLE) {
					shieldUp(1, true);
				}
//				else
//					shieldUp(1, false);

			}

			// Player 3
			if (m3 && !m1 && !m2 && !m4 && factoryMap.get(2) != null) {

				if (button == Input.Buttons.RIGHT) {
					//Gdx.app.log("GameScreen", "rotateTarget(3)");
					rotateTarget(2);
				}

				if (button == Input.Buttons.LEFT
//						|| (touchAreaP3 != null
//						&& Intersector.intersectRayBoundsFast(collisionRay, touchAreaP3))
				) {
					((FactoryProduction) factoryMap.get(2)).button_held = true;
					pointerP3 = pointer;
					touchedP3 = true;
				}

				if (button == Input.Buttons.MIDDLE) {
					shieldUp(2, true);
				}
//				else
//					shieldUp(2, false);

			}

			// Player 4
			if (m4 && !m1 && !m2 && !m3 && factoryMap.get(3) != null) {

				if (button == Input.Buttons.RIGHT) {
					//Gdx.app.log("GameScreen", "rotateTarget(4)");
					rotateTarget(3);
				}

				if (button == Input.Buttons.LEFT
//						|| (touchAreaP4 != null
//						&& Intersector.intersectRayBoundsFast(collisionRay, touchAreaP4))
				) {
					((FactoryProduction) factoryMap.get(3)).button_held = true;
					pointerP4 = pointer;
					touchedP4 = true;
				}

				if (button == Input.Buttons.MIDDLE) {
					shieldUp(3, true);
				}
//				else
//					shieldUp(3, false);

			}
			return false;
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		collisionRay = cam.getPickRay(x, y);

		if (factorys.size > 0) {

			if (m1 && !m2 && !m3 && !m4 && factoryMap.get(0) != null) {

				if (pointer == pointerP1 && button == Input.Buttons.LEFT) {
					((FactoryProduction) factoryMap.get(0)).button_held = false;
					pointerP1 = -1;
					touchedP1 = false;
//					Gdx.app.log("GameScreen", "	2-");
				}

//				if (pointer == pointerP1 && touchedP1 && Intersector.intersectRayBoundsFast(collisionRay, touchAreaP1)) {
//					((FactoryProduction) factoryMap.get(0)).button_held = false;
//					pointerP1 = -1;
//					touchedP1 = false;
////					Gdx.app.log("GameScreen", "	3-");
//				}

				if (button == Input.Buttons.MIDDLE)
					shieldUp(0, false);

			}

			if (m2 && !m1 && !m3 && !m4 && factoryMap.get(1) != null) {

				if (pointer == pointerP2 && button == Input.Buttons.LEFT) {
					((FactoryProduction) factoryMap.get(1)).button_held = false;
					pointerP2 = -1;
					touchedP2 = false;
				}

//				if (pointer == pointerP2 && touchedP2 && Intersector.intersectRayBoundsFast(collisionRay, touchAreaP2)) {
//					((FactoryProduction) factoryMap.get(1)).button_held = false;
//					pointerP2 = -1;
//					touchedP2 = false;
//				}

				if (button == Input.Buttons.MIDDLE)
					shieldUp(1, false);
			}

			if (m3 && !m1 && !m2 && !m4 && factoryMap.get(2) != null) {

				if (pointer == pointerP3 && button == Input.Buttons.LEFT) {
					((FactoryProduction) factoryMap.get(2)).button_held = false;
					pointerP3 = -1;
					touchedP2 = false;
				}

//				if (pointer == pointerP3 && touchedP3 && Intersector.intersectRayBoundsFast(collisionRay, touchAreaP3)) {
//					((FactoryProduction) factoryMap.get(2)).button_held = false;
//					pointerP3 = -1;
//					touchedP3 = false;
//				}

				if (button == Input.Buttons.MIDDLE)
					shieldUp(2, false);

			}

			if (m4 && !m1 && !m2 && !m3 && factoryMap.get(3) != null) {

				if (pointer == pointerP4 && button != Input.Buttons.LEFT) {
					((FactoryProduction) factoryMap.get(3)).button_held = false;
					pointerP4 = -1;
					touchedP4 = false;
				}

//				if (pointer == pointerP4 && touchedP4 && Intersector.intersectRayBoundsFast(collisionRay, touchAreaP4)) {
//					((FactoryProduction) factoryMap.get(3)).button_held = false;
//					pointerP4 = -1;
//					touchedP3 = false;
//				}

				if (button == Input.Buttons.MIDDLE)
					shieldUp(3, false);
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

}
