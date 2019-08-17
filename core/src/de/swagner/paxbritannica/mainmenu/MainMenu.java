package de.swagner.paxbritannica.mainmenu;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import java.util.Map;

import de.swagner.paxbritannica.DefaultScreen;
import de.swagner.paxbritannica.GameInstance;
import de.swagner.paxbritannica.GameScreen;
import de.swagner.paxbritannica.Resources;
import de.swagner.paxbritannica.background.BackgroundFXRenderer;
import de.swagner.paxbritannica.help.Help;
import de.swagner.paxbritannica.settings.Settings;

public class MainMenu extends DefaultScreen implements InputProcessor {

	private final static int INITIAL_WIDTH = 80;
	private final static int TEAM_HEIGHT = 150;
	private final static int TEAM_WIDTH = 127;
	private final static int OFFSET = 20;
	private final static int OFFSET1 = 14;
	private final static String NONE = "None";

	private Sprite title;
	private Sprite credits;
	private Sprite settings;

	private FactorySelector p1;
	private FactorySelector p2;
	private FactorySelector p3;
	private FactorySelector p4;

	private Countdown countdown;

	private OrthographicCamera cam;

	private Sprite help;
	private Sprite musicOnOff;

	private BoundingBox collisionHelp = new BoundingBox();
	private BoundingBox collisionMusic = new BoundingBox();
	private BoundingBox collisionSettings = new BoundingBox();

	private BackgroundFXRenderer backgroundFX = new BackgroundFXRenderer();
	private Sprite blackFade;

	private SpriteBatch titleBatch;
	private SpriteBatch fadeBatch;
	private SpriteBatch gameBatch;

	private Ray collisionRay;

	private BitmapFont font;

	private Map<Integer, Integer> teamMap;

	private float time = 0;
	private float fade = 1.0f;

	private int idP1 = -1;
	private int idP2 = -1;
	private int cnt = 0;
	private int oldCnt = 0;
	private int changeToScreen = -1;

	private int width = 800;
	private int height = 480;

	private int offset2;

	private String team;


	public MainMenu(Game game) {
		super(game);
		Gdx.input.setInputProcessor(this);

		gameBatch = new SpriteBatch();

		// Set up generator for ttf creation
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/BLKCHCRY.TTF"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 22;
		parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:";

		font = generator.generateFont(parameter);
		generator.dispose();

        // read teamMap
		teamMap = GameInstance.getInstance().teamMap;

	}
	
	@Override
	public void show() {	
		Resources.getInstance().reInit();

		GameInstance.getInstance().resetGame();
		
		changeToScreen = -1;
		
		backgroundFX = new BackgroundFXRenderer();

		title = Resources.getInstance().title;
		credits = Resources.getInstance().credits;
		settings = Resources.getInstance().settings;
		blackFade = Resources.getInstance().blackFade;
		
		musicOnOff = Resources.getInstance().musicOnOff;
		musicOnOff.setPosition(20, 10);
		musicOnOff.setColor(1,1,1,0.5f);
		collisionMusic.set(new Vector3(musicOnOff.getVertices()[0], musicOnOff.getVertices()[1], -10),new Vector3(musicOnOff.getVertices()[10], musicOnOff.getVertices()[11], 10));
		
		help = Resources.getInstance().help;
		help.setPosition(75, 10);
		help.setColor(1,1,1,0.5f);
		collisionHelp.set(new Vector3(help.getVertices()[0], help.getVertices()[1], -10),new Vector3(help.getVertices()[10], help.getVertices()[11], 10));
		
		settings = Resources.getInstance().settings;
		settings.setPosition(135, 8);
		settings.setColor(1,1,1,0.5f);
		collisionSettings.set(new Vector3(settings.getVertices()[0], settings.getVertices()[1], -10),new Vector3(settings.getVertices()[10], settings.getVertices()[11], 10));
		
		p1 = new FactorySelector(new Vector2(055f, 150f), 1);
		p2 = new FactorySelector(new Vector2(180f, 150f), 2);
		p3 = new FactorySelector(new Vector2(305f, 150f), 3);
		p4 = new FactorySelector(new Vector2(430f, 150f), 4);

		countdown = new Countdown(new Vector2(380f, -10f));

		titleBatch = new SpriteBatch();
		titleBatch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 2, 2);
		
		Preferences prefs = Gdx.app.getPreferences("paxbritannica");
		if (prefs.getBoolean("music")) {
			if(Resources.getInstance().music == null) Resources.getInstance().reInit();
			if(!Resources.getInstance().music.isPlaying()) { 
				Resources.getInstance().music.play();
				Resources.getInstance().music.setLooping(true);
			}
			musicOnOff.setColor(1,1,1,0.5f);
		} else {
			Resources.getInstance().music.stop();			
			musicOnOff.setColor(1,1,1,0.1f);
		}
		
	}
	
	
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
		}  else if (width == 1366 && height == 768) {
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
		titleBatch.getProjectionMatrix().set(cam.combined);
		
		musicOnOff.setPosition(20 - ((this.width-800)/2), 10- ((this.height-480)/2));
		help.setPosition(75- ((this.width-800)/2), 10- ((this.height-480)/2));
		settings.setPosition(135 - ((this.width-800)/2), 8- ((this.height-480)/2));
		
		collisionMusic.set(new Vector3(musicOnOff.getVertices()[0], musicOnOff.getVertices()[1], -10),new Vector3(musicOnOff.getVertices()[10], musicOnOff.getVertices()[11], 10));
		collisionHelp.set(new Vector3(help.getVertices()[0], help.getVertices()[1], -10),new Vector3(help.getVertices()[10], help.getVertices()[11], 10));
		collisionSettings.set(new Vector3(settings.getVertices()[0], settings.getVertices()[1], -10),new Vector3(settings.getVertices()[10], settings.getVertices()[11], 10));
	
	}

	@Override
	public void render(float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		time += delta;

		if (time < 1f)
			return;

		backgroundFX.render();

		titleBatch.begin();
		
		musicOnOff.draw(titleBatch);
		help.draw(titleBatch);
		settings.draw(titleBatch);
		
		titleBatch.draw(title, 85f, 320f, 0, 0, 512, 64f, 1.24f, 1.24f, 0);
		titleBatch.draw(credits, 595f, 50f);
		p1.draw(titleBatch);
		p2.draw(titleBatch);
		p3.draw(titleBatch);
		p4.draw(titleBatch);

		cnt = 0;
		if (p1.playerSelect || p1.cpuSelect)
			cnt++;
		if (p2.playerSelect || p2.cpuSelect)
			cnt++;
		if (p3.playerSelect || p3.cpuSelect)
			cnt++;
		if (p4.playerSelect || p4.cpuSelect)
			cnt++;
		if (cnt > 1) {
			countdown.draw(titleBatch);
		}
		if( cnt != oldCnt) {
			countdown.reset();
			oldCnt = cnt;
		}
		if ((p1.picked && !(p1.playerSelect || p1.cpuSelect)) || (p2.picked && !(p2.playerSelect || p2.cpuSelect)) || (p3.picked
				&& !(p3.playerSelect || p3.cpuSelect)) || (p4.picked && !(p4.playerSelect || p4.cpuSelect))) {
			countdown.reset();
		}

		titleBatch.end();

		if (!countdown.finished && fade > 0) {
			fade = Math.max(fade - delta / 2.f, 0);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g, blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
		}

		if (countdown.finished) {
			fade = Math.min(fade + delta / 2.f, 1);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g, blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();

			if (fade >= 1 && cnt>=2) {
				Array<Integer> playerList = new Array<Integer>();
				if (p1.playerSelect) {
					playerList.add(1);
				}
				if (p2.playerSelect) {
					playerList.add(2);
				}
				if (p3.playerSelect) {
					playerList.add(3);
				}
				if (p4.playerSelect) {
					playerList.add(4);
				}
				Array<Integer> cpuList = new Array<Integer>();
				if (p1.cpuSelect) {
					cpuList.add(1);
				}
				if (p2.cpuSelect) {
					cpuList.add(2);
				}
				if (p3.cpuSelect) {
					cpuList.add(3);
				}
				if (p4.cpuSelect) {
					cpuList.add(4);
				}
				game.setScreen(new GameScreen(game, playerList, cpuList));

			} else if(fade >= 1 && cnt<1)   {
				if(changeToScreen==1) {
					game.setScreen(new Settings(game));
				} else {
					game.setScreen(new Help(game));
				}
			}
		}

		gameBatch.begin();

		font.setColor(Color.GOLDENROD);
		font.draw(gameBatch, "Ship Color :", INITIAL_WIDTH - 10, TEAM_HEIGHT + 40);

		font.setColor(Color.TEAL);
		font.draw(gameBatch, "Blue", INITIAL_WIDTH + OFFSET1 * 2 + TEAM_WIDTH - offset2 - 2, TEAM_HEIGHT + 40);
		font.setColor(Color.RED);
		font.draw(gameBatch, "Red", INITIAL_WIDTH + OFFSET1 * 2 + TEAM_WIDTH * 2 - offset2 - 4, TEAM_HEIGHT + 40);
		font.setColor(Color.GREEN);
		font.draw(gameBatch, "Green", INITIAL_WIDTH + OFFSET1 * 2 + TEAM_WIDTH * 3 - offset2 - 7, TEAM_HEIGHT + 40);
		font.setColor(Color.YELLOW);
		font.draw(gameBatch, "Yellow", INITIAL_WIDTH + OFFSET1 * 2 + TEAM_WIDTH * 4 - offset2 - 11, TEAM_HEIGHT + 40);

		font.setColor(Color.GOLDENROD);
		font.draw(gameBatch, "Select Team :", INITIAL_WIDTH - OFFSET1 - 10, TEAM_HEIGHT);

		// Team 1
		int id = teamMap.get(1);
		if (id != 0) {
			team = id + "";
			offset2 = 0;
		}
		else {
			team = NONE;
			offset2 = OFFSET;
		}
		font.setColor(Color.TEAL);
		font.draw(gameBatch, team, INITIAL_WIDTH + OFFSET1 * 3 + TEAM_WIDTH - offset2, TEAM_HEIGHT);

		// Team 2
		id = teamMap.get(2);
		if (id != 0) {
			team = id + "";
			offset2 = 0;
		}
		else {
			team = NONE;
			offset2 = OFFSET;
		}
		font.setColor(Color.RED);
		font.draw(gameBatch, team, INITIAL_WIDTH + OFFSET1 * 3 + TEAM_WIDTH * 2 - offset2, TEAM_HEIGHT);

		// Team 3
		id = teamMap.get(3);
		if (id != 0) {
			team = id + "";
			offset2 = 0;
		}
		else {
			team = NONE;
			offset2 = OFFSET;
		}
		font.setColor(Color.GREEN);
		font.draw(gameBatch, team, INITIAL_WIDTH + OFFSET1 * 3 + TEAM_WIDTH * 3 - offset2, TEAM_HEIGHT);

		// Team 4
		id = teamMap.get(4);
		if (id != 0) {
			team = id + "";
			offset2 = 0;
		}
		else {
			team = NONE;
			offset2 = OFFSET;
		}
		font.setColor(Color.YELLOW);
		font.draw(gameBatch, team, INITIAL_WIDTH + OFFSET1 * 3 + TEAM_WIDTH * 4 - offset2, TEAM_HEIGHT);

		gameBatch.end();
	}

	@Override
	public void hide() {
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
			boolean exit = true;
			if(p1.picked) {
				p1.reset();
				exit = false;
			}
			if(p2.picked) {
				p2.reset();
				exit = false;
			}			
			if(p3.picked) {
				p3.reset();
				exit = false;
			}			
			if(p4.picked) {
				p4.reset();
				exit = false;
			}
			
			if(exit) {
				if(!(Gdx.app.getType() == ApplicationType.Applet)) {
					Gdx.app.exit();
				}
			}
		}
		
		if(keycode == Input.Keys.A) {
			if (!p1.picked) {
				p1.picked = true;
			} else {
				p1.playerSelect = true;
				p1.cpuSelect = false;
			}			
		}		
		if(keycode == Input.Keys.F) {
			if (!p2.picked) {
				p2.picked = true;
			}  else {
				p2.playerSelect = true;
				p2.cpuSelect = false;
			}	
		}		
		if(keycode == Input.Keys.H) {
			if (!p3.picked) {
				p3.picked = true;
			}  else {
				p3.playerSelect = true;
				p3.cpuSelect = false;
			}	
		}
		if(keycode == Input.Keys.L) {
			if (!p4.picked) {
				p4.picked = true;
			}  else {
				p4.playerSelect = true;
				p4.cpuSelect = false;
			}	
		}
		
		if(keycode == Input.Keys.M) {
			if (cnt >= 1)
				return false;
			Preferences prefs = Gdx.app.getPreferences("paxbritannica");
			prefs.putBoolean("music", !prefs.getBoolean("music"));
			prefs.flush();
			if(prefs.getBoolean("music")) {
				if(Resources.getInstance().music == null) Resources.getInstance().reInit();
				if(!Resources.getInstance().music.isPlaying()) { 
					Resources.getInstance().music.play();
					Resources.getInstance().music.setLooping(true);
				}
				musicOnOff.setColor(1,1,1,0.5f);				
			} else {
				Resources.getInstance().music.stop();
				musicOnOff.setColor(1,1,1,0.1f);
			}			
		}
		
		if(keycode == Input.Keys.F1) {
			if (cnt >= 1)
				return false;
			countdown.finished = true;
			changeToScreen = 0;
		}
		
		if(keycode == Input.Keys.S) {
			if (cnt >= 1)
				return false;
			countdown.finished = true;
			changeToScreen = 1;
		}

		if (keycode == Input.Keys.NUM_1 || keycode == Input.Keys.NUMPAD_1) {
			int i = GameInstance.getInstance().teamMap.get(1);
			i++;
			if (i > 2) i = 0;
			GameInstance.getInstance().teamMap.put(1, i);
		}

		if (keycode == Input.Keys.NUM_2 || keycode == Input.Keys.NUMPAD_2) {
			int i = GameInstance.getInstance().teamMap.get(2);
			i++;
			if (i > 2) i = 0;
			GameInstance.getInstance().teamMap.put(2, i);
		}

		if (keycode == Input.Keys.NUM_3 || keycode == Input.Keys.NUMPAD_3) {
			int i = GameInstance.getInstance().teamMap.get(3);
			i++;
			if (i > 2) i = 0;
			GameInstance.getInstance().teamMap.put(3, i);
		}

		if (keycode == Input.Keys.NUM_4 || keycode == Input.Keys.NUMPAD_4) {
			int i = GameInstance.getInstance().teamMap.get(4);
			i++;
			if (i > 2) i = 0;
			GameInstance.getInstance().teamMap.put(4, i);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
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
		
		if (cnt > 4 || countdown.finished)
			return false;	
		
		// check if ship is activated
		if (Intersector.intersectRayBoundsFast(collisionRay, p1.collision) && !p1.picked) {
			p1.picked = true;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p2.collision) && !p2.picked) {
			p2.picked = true;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p3.collision) && !p3.picked) {
			p3.picked = true;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p4.collision) && !p4.picked) {
			p4.picked = true;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p1.collisionPlayerSelect) && p1.picked && !p1.cpuSelect) {
			p1.playerSelect = true;
			p1.cpuSelect = false;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p2.collisionPlayerSelect) && p2.picked && !p2.cpuSelect) {
			p2.playerSelect = true;
			p2.cpuSelect = false;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p3.collisionPlayerSelect) && p3.picked && !p3.cpuSelect) {
			p3.playerSelect = true;
			p3.cpuSelect = false;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p4.collisionPlayerSelect) && p4.picked && !p4.cpuSelect) {
			p4.playerSelect = true;
			p4.cpuSelect = false;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p1.collisionCPUSelect) && p1.picked && !p1.playerSelect) {
			p1.cpuSelect = true;
			p1.playerSelect = false;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p2.collisionCPUSelect) && p2.picked && !p2.playerSelect) {
			p2.cpuSelect = true;
			p2.playerSelect = false;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p3.collisionCPUSelect) && p3.picked && !p3.playerSelect) {
			p3.cpuSelect = true;
			p3.playerSelect = false;
		} else if (Intersector.intersectRayBoundsFast(collisionRay, p4.collisionCPUSelect) && p4.picked && !p4.playerSelect) {
			p4.cpuSelect = true;
			p4.playerSelect = false;
		}
		
		if (Intersector.intersectRayBoundsFast(collisionRay, collisionMusic)) {
			if (cnt >= 1)
				return false;
			Preferences prefs = Gdx.app.getPreferences("paxbritannica");
			prefs.putBoolean("music", !prefs.getBoolean("music"));
			prefs.flush();
			if(prefs.getBoolean("music")) {
				if(Resources.getInstance().music == null) Resources.getInstance().reInit();
				if(!Resources.getInstance().music.isPlaying()) { 
					Resources.getInstance().music.play();
					Resources.getInstance().music.setLooping(true);
				}
				musicOnOff.setColor(1,1,1,0.5f);				
			} else {
				Resources.getInstance().music.stop();
				musicOnOff.setColor(1,1,1,0.1f);
			}			
		}
		
		if (Intersector.intersectRayBoundsFast(collisionRay, collisionHelp)) {
			if (cnt >= 1)
				return false;
			countdown.finished = true;
			changeToScreen = 0;
		}
		
		if (Intersector.intersectRayBoundsFast(collisionRay, collisionSettings)) {
			if (cnt >= 1)
				return false;
			countdown.finished = true;
			changeToScreen = 1;
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
}
