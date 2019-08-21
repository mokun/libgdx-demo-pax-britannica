package de.swagner.paxbritannica.help;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import de.swagner.paxbritannica.DefaultScreen;
import de.swagner.paxbritannica.GameInstance;
import de.swagner.paxbritannica.Resources;
import de.swagner.paxbritannica.background.BackgroundFXRenderer;
import de.swagner.paxbritannica.mainmenu.MainMenu;

public class Help extends DefaultScreen implements InputProcessor {
	
	Sprite back;
	
	BoundingBox collisionBack = new BoundingBox();
	BoundingBox collisionMusic = new BoundingBox();

	Sprite fighter;
	Sprite bomber;
	Sprite frigate;
	Sprite upgrade;
	
	BackgroundFXRenderer backgroundFX = new BackgroundFXRenderer();
	Sprite blackFade;

	OrthographicCamera cam;
	
	BitmapFont font;
	
	SpriteBatch titleBatch;
	SpriteBatch fadeBatch;
	
	Ray collisionRay;

	boolean finished = false;
	
	float time = 0;
	float fade = 1.0f;

	private int width = 800;
	private int height = 480;

	public Help(Game game) {
		super(game);
		Gdx.input.setCatchBackKey( true );
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void show() {		
		GameInstance.getInstance().resetGame();
		
		blackFade = Resources.getInstance().blackFade;
		
		back = Resources.getInstance().back;
		back.setPosition(10, 10);
		back.setColor(1,1,1,0.5f);
		collisionBack.set(new Vector3(back.getVertices()[0], back.getVertices()[1], -10),new Vector3(back.getVertices()[10], back.getVertices()[11], 10));
		
		fighter = Resources.getInstance().fighterOutline;
		fighter.setRotation(0);
		bomber = Resources.getInstance().bomberOutline;
		bomber.setRotation(0);
		frigate = Resources.getInstance().frigateOutline;
		frigate.setRotation(0);
		upgrade = Resources.getInstance().upgradeOutline;
		upgrade.setRotation(0);
		
		titleBatch = new SpriteBatch();
		titleBatch.getProjectionMatrix().setToOrtho2D(-120, 0, 800, 480);
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 2, 2);

//		font = new BitmapFont();
//		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// Set up generator for ttf creation
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/BLKCHCRY.TTF"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 20;
		parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:";

		font = generator.generateFont(parameter);
//		font.setColor(Color.GOLDENROD);
		generator.dispose();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		time += delta;

		if (time < 1f)
			return;

		backgroundFX.render();

		titleBatch.begin();
		
		back.draw(titleBatch);

		int x = 0;

		font.draw(titleBatch, "Pax Britannica is a real-time action strategy game. " +
				"The battle takes place around an inter-dimensional vortex.", x, 480);
		font.draw(titleBatch, "Features : ", 100, 440);
		font.draw(titleBatch, " > Up to 4 players", 110, 420);
		font.draw(titleBatch, " > Against other Player(s) and/or CPU(s) hotseat", 110, 400);
		font.draw(titleBatch, " > Mouse or keyboard control", 110, 380);
		font.draw(titleBatch, "Button          Function          Player 1    Player 2    Player 3    Player 4", x + 30, 320);
		font.draw(titleBatch, "1         shield up             a             j             left          4 (numpad) ", x + 70, 280);
		font.draw(titleBatch, "2         produce ships           s             k             down            2 (numpad)", x + 70, 300);
		font.draw(titleBatch, "3        target factory           d             l              right             6 (numpad)", x + 70, 280);

		font.draw(titleBatch, "Quad 1 : Fighter - Small and agile. Shoot laser. Chase down Bombers", x + 40, 230);
		fighter.setPosition(x - 20, 180);
		fighter.draw(titleBatch);
		font.draw(titleBatch, "Quad 2 : Bomber - Shoots slow projectiles. Do massive damage to Frigates or Factory ships", x + 40, 200);
		bomber.setPosition(x - 20, 150);
		bomber.draw(titleBatch);
        font.draw(titleBatch, "Quad 3 : Frigate - Powerful but slow. Fires volleys of heat-seeking missiles. Effective against Fighters", x + 40, 170);
		frigate.setPosition(x - 20, 120);
		frigate.draw(titleBatch);
		font.draw(titleBatch, "Quad 4 : Upgrade - Regenerate Factory ship's health points. Accumulate resources more quickly", x + 40, 140);
		upgrade.setPosition(x - 20, 90);
		upgrade.draw(titleBatch);
		font.draw(titleBatch, "Spawn ships to fight automatically on the opposite team using the latest in artificial aquatelligence technology.", x, 90);
		font.draw(titleBatch, "The last player who keeps one's factory ship alive wins!", x + 150, 70);
	
		
		titleBatch.end();

		if (!finished && fade > 0) {
			fade = Math.max(fade - Gdx.graphics.getDeltaTime() / 2.f, 0);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g, blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
		}

		if (finished) {
			fade = Math.min(fade + Gdx.graphics.getDeltaTime() / 2.f, 1);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g, blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
			if (fade >= 1) {
				game.setScreen(new MainMenu(game));
			}
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
		
		back.setPosition(20 - ((this.width-800)/2), 10- ((this.height-480)/2));
		collisionBack.set(new Vector3(back.getVertices()[0], back.getVertices()[1], -10),new Vector3(back.getVertices()[10], back.getVertices()[11], 10));
	
	}

	@Override
	public void hide() {
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.BACK) {
			finished = true;
		}
		
		if(keycode == Input.Keys.ESCAPE) {
			finished = true;
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
		
		if (Intersector.intersectRayBoundsFast(collisionRay, collisionBack)) {
			finished = true;
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