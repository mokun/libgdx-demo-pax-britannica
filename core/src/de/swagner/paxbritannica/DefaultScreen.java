package de.swagner.paxbritannica;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public abstract class DefaultScreen implements Screen {
	protected Game game;

	//public SpriteBatch gameBatch;

	public DefaultScreen(Game game) {

		this.game = game;

		//gameBatch = new SpriteBatch();
		//gameBatch.getProjectionMatrix().set(cam.combined);


	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
//		dispose();
	}

	@Override
	public void resume() { 
//		Resources.getInstance().reInit();
	}

	@Override
	public void dispose() {
//		Resources.getInstance().dispose();
	}
	
}
