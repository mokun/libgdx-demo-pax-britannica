package de.swagner.paxbritannica;

import java.util.Map;

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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

//import com.badlogic.gdx.controllers.Controller;
//import com.badlogic.gdx.Controllers;

import de.swagner.paxbritannica.background.BackgroundFXRenderer;
import de.swagner.paxbritannica.factory.EasyEnemyProduction;
import de.swagner.paxbritannica.factory.FactoryProduction;
import de.swagner.paxbritannica.factory.HardEnemyProduction;
import de.swagner.paxbritannica.factory.MediumEnemyProduction;
import de.swagner.paxbritannica.factory.PlayerProduction;
import de.swagner.paxbritannica.mainmenu.MainMenu;

public class GameScreen extends DefaultScreen implements InputProcessor {

    private final static int FPS_HEIGHT = 15; //90
    private final static int FPS_WIDTH = 85; //45

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

		if (playerList.contains(1, true)) {
			Gdx.app.log("GameScreen", "m1 is true");
			m1 = true;
		}
		else if (playerList.contains(2, true)) {
			Gdx.app.log("GameScreen", "m2 is true");
			m2 = true;
		}
		else if (playerList.contains(3, true)) {
			Gdx.app.log("GameScreen", "m3 is true");
			m3 = true;
		}
		else if (playerList.contains(4, true)) {
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
			Gdx.app.log("[GameScreen]", "Player " + i + " initialized.");
		for (int i : cpuList)
			Gdx.app.log("[GameScreen]", "CPU " + i + " initialized.");

		teamMap = GameInstance.getInstance().teamMap;

		// Set up targetingMap
		targetingMap = GameInstance.getInstance().targetingMap;
		targetingMap.put(1, 0);
		targetingMap.put(2, 0);
		targetingMap.put(3, 0);
		targetingMap.put(4, 0);


		//font = new BitmapFont();

        // Set up generator for ttf creation
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/BLKCHCRY.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 16;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:";

        font = generator.generateFont(parameter);
        generator.dispose();

		//font = new BitmapFont(Gdx.files.internal("data/default.fnt"),Gdx.files.internal("data/default_00.png"),false);
		//font.set.setScale(.2f);

		Gdx.input.setCatchBackKey( true );
		Gdx.input.setInputProcessor(this);

		cam = new OrthographicCamera(width, height);
		
		cam.position.x = 400;
		cam.position.y = 240;
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
		
		if(playerList.size>0 && playerList.get(0)==1) {
			p1 = Resources.getInstance().factoryP1Small;
		} else if(playerList.size>0 && playerList.get(0)==2) {
			p1 = Resources.getInstance().factoryP2Small;
		} else if(playerList.size>0 && playerList.get(0)==3) {
			p1 = Resources.getInstance().factoryP3Small;
		} else if(playerList.size>0 && playerList.get(0)==4) {
			p1 = Resources.getInstance().factoryP4Small;		
		}
		
		if(playerList.size>1 && playerList.get(1)==1) {
			p2 = Resources.getInstance().factoryP1Small;
		} else if(playerList.size>1 && playerList.get(1)==2) {
			p2 = Resources.getInstance().factoryP2Small;
		} else if(playerList.size>1 && playerList.get(1)==3) {
			p2 = Resources.getInstance().factoryP3Small;
		} else if(playerList.size>1 && playerList.get(1)==4) {
			p2 = Resources.getInstance().factoryP4Small;		
		}
		
		if(playerList.size>2 && playerList.get(2)==1) {
			p3 = Resources.getInstance().factoryP1Small;
		} else if(playerList.size>2 && playerList.get(2)==2) {
			p3 = Resources.getInstance().factoryP2Small;
		} else if(playerList.size>2 && playerList.get(2)==3) {
			p3 = Resources.getInstance().factoryP3Small;
		} else if(playerList.size>2 && playerList.get(2)==4) {
			p3 = Resources.getInstance().factoryP4Small;		
		}
		
		if(playerList.size>3 && playerList.get(3)==1) {
			p4 = Resources.getInstance().factoryP1Small;
		} else if(playerList.size>3 && playerList.get(3)==2) {
			p4 = Resources.getInstance().factoryP2Small;
		} else if(playerList.size>3 && playerList.get(3)==3) {
			p4 = Resources.getInstance().factoryP3Small;
		} else if(playerList.size>3 && playerList.get(3)==4) {
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

		for(int i=0;i<playerList.size;++i) {
			Vector2 temp1 = new Vector2(POSITIONS.get(currentPos).x, POSITIONS.get(currentPos).y);
			Vector2 temp2 = new Vector2(POSITIONS.get(currentPos).x, POSITIONS.get(currentPos).y);
			Vector2 facing = new Vector2(-temp1.sub(CENTER).y, temp2.sub(CENTER).x).nor();

			// Set up teamID, Team 1 consists of player 1 & 2; team 2 consists of player 3 & 4.
			if (playerList.get(i) == 1 || playerList.get(i) == 2)
				teamID = 1;
			else if (playerList.get(i) == 3 || playerList.get(i) == 4)
				teamID = 2;

			teamMap.put(i, teamID);

			playerProduction = new PlayerProduction(playerList.get(i), teamID, POSITIONS.get(currentPos), facing);

            factorys.add(playerProduction);
			factoryMap.put(playerList.get(i), playerProduction);
			++currentPos;
		}
		
		for(int i=0;i<cpuList.size;++i) {
			Vector2 temp1 = new Vector2(POSITIONS.get(currentPos).x, POSITIONS.get(currentPos).y);
			Vector2 temp2 = new Vector2(POSITIONS.get(currentPos).x, POSITIONS.get(currentPos).y);
			Vector2 facing = new Vector2(-temp1.sub(CENTER).y, temp2.sub(CENTER).x).nor();

			// Set up teamID, Team 1 consists of player 1 & 2; team 2 consists of player 3 & 4.
			if (cpuList.get(i) == 1 || cpuList.get(i) == 2)
				teamID = 1;
			else if (cpuList.get(i) == 3 || cpuList.get(i) == 4)
				teamID = 2;

			teamMap.put(i, teamID);

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
	/*
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
		}  else if (width == 1366 && height == 768) {
			cam = new OrthographicCamera(1280, 720);
			this.width = 1280;
			this.height = 720;
		} else if (width == 1366 && height == 720) {
			cam = new OrthographicCamera(1280, 675);
			this.width = 1280;
			this.height = 675;
		} else if (width == 1536 && height == 1152) {
			cam = new OrthographicCamera(1366, 1024);
			this.width = 1366;
			this.height = 1024;
        } else if (width == 1920 && height == 1080) {
            cam = new OrthographicCamera(1920, 1080);
            this.width = 1920;
            this.height = 1080;
		} else if (width == 1920 && height == 1152) {
			cam = new OrthographicCamera(1366, 854);
			this.width = 1366;
			this.height = 854;
		} else if (width == 1920 && height == 1200) {
			cam = new OrthographicCamera(1366, 800);
			this.width = 1280;
			this.height = 800;
		} else if (width > 1280) {
			cam = new OrthographicCamera(1280, 768);
			this.width = 1280;
			this.height = 768;
		} else if (width < 800) {
			cam = new OrthographicCamera(800, 480);
			this.width = 800;
			this.height = 480;
		} else {
*/
			cam = new OrthographicCamera(width, height);
		//}
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
					GameInstance.getInstance().playerList.removeValue(ship.getID(), true);
				}
				else if (cpuList.contains(ship.getID(), true)) {
					cpuList.removeValue(ship.getID(), true);
					GameInstance.getInstance().cpuList.removeValue(ship.getID(), true);
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

        int[][] counts = GameInstance.getInstance().getCounts();

        for (int i=1; i< 5; i++) {

            if (i == 1)
                font.setColor(Color.CYAN);
            else if (i == 2)
                font.setColor(Color.RED);
            else if (i == 3)
                font.setColor(Color.GREEN);
            else if (i == 4)
                font.setColor(Color.YELLOW);

            if (factoryMap.containsKey(i)) {
                //Gdx.app.log("[GS] ", "factoryMap.get(i-1) : " + factoryMap.get(i-1));
                //Gdx.app.log("[GS] ", "factoryMap.size() : " + factoryMap.size());
                //Gdx.app.log("[GS] ", "factorys.size : " + factorys.size);

                double health = factoryMap.get(i).health()* 100.0;

                font.draw(gameBatch,
		        				Math.round(health * 10.0) / 10.0 + " %",
                              -FPS_WIDTH+5, FPS_HEIGHT * 2 - i * 20);

                font.draw(gameBatch,
                                counts[i - 1][0]
                                + "   " + counts[i - 1][1]
                                + "   " + counts[i - 1][2]
                                //+ " " + counts[3]
                                + "]",
                              0, FPS_HEIGHT * 2 - 5 - i * 20);

                font.draw(gameBatch,
	        					" " + counts[i - 1][3],
                        FPS_WIDTH+5, FPS_HEIGHT * 2 - i * 20);

				int j = targetingMap.get(i);

				String target = null;

				if (j == 0) {
					font.setColor(Color.WHITE);
					target = "Any";
				}
				else if (j == 1) {
					font.setColor(Color.CYAN);
					target = "Cyan";
				}
				else if (j == 2) {
					font.setColor(Color.RED);
					target = "Red";
				}
				else if (j == 3) {
					font.setColor(Color.GREEN);
					target = "Green";
				}
				else if (j == 4) {
					font.setColor(Color.YELLOW);
					target = "Yellow";
				}

				font.draw(gameBatch,
						" " + target,
						FPS_WIDTH *2+5, FPS_HEIGHT * 2 - i * 20);


			}
        }

		gameBatch.end();
				
		//show touch area notification
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
		else if (cpuList.contains(target_id, true))
			return true;
		else
			return false;
	}

	// Change the target color to the next
	public void rotateTarget(int player_id) {
		// target_id is 0, 1, 2, 3, or 4 (Note that 0 means all ships)
		int target_id = targetingMap.get(player_id);
		target_id++;
		if (target_id > 4)
			// reset it back to zero
			target_id = 0;
		if (target_id != 0)
			// go to the next color
			target_id = nextTarget(player_id, target_id);

		// update the target_id
		targetingMap.put(player_id, target_id);
	}

	public int nextTarget(int player_id, int target_id) {
		// repeat this process again
		if (targetExisted(target_id)) {
			// if this is player's own color
			if (target_id != player_id) {
				// if this color is on the same team
				if (GameInstance.getInstance().isFinalEpicBattle()
						|| teamMap.get(player_id) != teamMap.get(target_id)){
					return target_id;
				}
				else {
					target_id++;
					if (target_id > 4)
						// reset it back to zero
						target_id = 0;
					if (target_id != 0)
						// call recursively to go to the next color
						target_id = nextTarget(player_id, target_id);
					else
						return target_id;
				}
			}
			else {
				target_id++;
				if (target_id > 4)
					// reset it back to zero
					target_id = 0;
				if (target_id != 0)
					// call recursively to go to the next color
					target_id = nextTarget(player_id, target_id);
				else
					return target_id;
			}

		}
		else {
			target_id++;
			if (target_id > 4)
				// reset it back to zero
				target_id = 0;
			if (target_id != 0)
				// call recursively to go to the next color
				target_id = nextTarget(player_id, target_id);
			else
				return target_id;
		}

		return target_id;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.BACK) {
			gameOver = true;
			gameOverTimer=0;
		}
		
		if(keycode == Input.Keys.ESCAPE) {
			gameOver = true;
			gameOverTimer=0;
		}


		if (factorys.size > 0) {
			// Player 1
			if (keycode == Input.Keys.S && factoryMap.get(1) != null) {
				((FactoryProduction) factoryMap.get(1)).button_held = true;
				touchedP1 = true;
			}
			if (keycode == Input.Keys.D && factoryMap.get(1) != null) {
				rotateTarget(1);
			}

			// Player 2
			if (keycode == Input.Keys.K && factoryMap.get(2) != null) {
				((FactoryProduction) factoryMap.get(2)).button_held = true;
				touchedP2 = true;
			}
			if (keycode == Input.Keys.L && factoryMap.get(2) != null) {
				rotateTarget(2);
			}

			// Player 3
			if (keycode == Input.Keys.DOWN && factoryMap.get(3) != null) {
				((FactoryProduction) factoryMap.get(3)).button_held = true;
				touchedP3 = true;
			}
			if (keycode == Input.Keys.RIGHT && factoryMap.get(3) != null) {
				rotateTarget(3);
			}

			// Player 4
			if (keycode == Input.Keys.NUMPAD_2 && factoryMap.get(4) != null) {
				((FactoryProduction) factoryMap.get(4)).button_held = true;
				touchedP4 = true;
			}
			if (keycode == Input.Keys.NUMPAD_6 && factoryMap.get(4) != null) {
				rotateTarget(4);
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (factorys.size > 0) {
			if (keycode == Input.Keys.S && factoryMap.get(1) != null) {
				((FactoryProduction) factoryMap.get(1)).button_held = false;
			}
			if (keycode == Input.Keys.K && factoryMap.get(2) != null) {
				((FactoryProduction) factoryMap.get(2)).button_held = false;
			}
			if (keycode == Input.Keys.DOWN && factoryMap.get(3) != null) {
				((FactoryProduction) factoryMap.get(3)).button_held = false;
			}
			if (keycode == Input.Keys.NUMPAD_2 && factoryMap.get(4) != null) {
				((FactoryProduction) factoryMap.get(4)).button_held = false;
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
			if (factoryMap.get(1) != null) {
				if (touchAreaP1 != null && Intersector.intersectRayBoundsFast(collisionRay, touchAreaP1)
					|| (m1 && button == Input.Buttons.LEFT)) {
					((FactoryProduction) factoryMap.get(1)).button_held = true;
					pointerP1 = pointer;
					touchedP1 = true;
				}

				if (m1 && button == Input.Buttons.RIGHT) {
					//Gdx.app.log("GameScreen", "rotateTarget(1)");
					rotateTarget(1);
				}
			}

			// Player 2
			if (factoryMap.get(2) != null) {
				if (touchAreaP2 != null && Intersector.intersectRayBoundsFast(collisionRay, touchAreaP2)
					|| (m2 && button == Input.Buttons.LEFT)) {
					((FactoryProduction) factoryMap.get(2)).button_held = true;
					pointerP2 = pointer;
					touchedP2 = true;
				}

				if (m2 && button == Input.Buttons.RIGHT) {
					//Gdx.app.log("GameScreen", "rotateTarget(2)");
					rotateTarget(2);
				}
			}

			// Player 3
			if (factoryMap.get(3) != null) {
				if (touchAreaP3 != null && Intersector.intersectRayBoundsFast(collisionRay, touchAreaP3)
					|| (m3 && button == Input.Buttons.LEFT)) {
					((FactoryProduction) factoryMap.get(3)).button_held = true;
					pointerP3 = pointer;
					touchedP3 = true;
				}

				if (m3 && button == Input.Buttons.RIGHT) {
					//Gdx.app.log("GameScreen", "rotateTarget(3)");
					rotateTarget(3);
				}
			}

			// Player 4
			if (factoryMap.get(4) != null) {
				if (touchAreaP4 != null && Intersector.intersectRayBoundsFast(collisionRay, touchAreaP4)
					|| (m4 && button == Input.Buttons.LEFT)) {
					((FactoryProduction) factoryMap.get(4)).button_held = true;
					pointerP4 = pointer;
					touchedP4 = true;
				}

				if (m4 && button == Input.Buttons.RIGHT) {
					//Gdx.app.log("GameScreen", "rotateTarget(4)");
					rotateTarget(4);
				}
			}

		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		collisionRay = cam.getPickRay(x, y);

		if (factorys.size > 0) {
			if (pointer == pointerP1 && factoryMap.get(1) != null) {
				((FactoryProduction) factoryMap.get(1)).button_held = false;
				pointerP1 = -1;
			}
			if (pointer == pointerP2 && factoryMap.get(2) != null) {
				((FactoryProduction) factoryMap.get(2)).button_held = false;
				pointerP2 = -1;
			}
			if (pointer == pointerP3 && factoryMap.get(3) != null) {
				((FactoryProduction) factoryMap.get(3)).button_held = false;
				pointerP3 = -1;
			}
			if (pointer == pointerP4 && factoryMap.get(4) != null) {
				((FactoryProduction) factoryMap.get(4)).button_held = false;
				pointerP4 = -1;
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
