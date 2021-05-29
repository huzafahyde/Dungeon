package com.mygdx.dungeongame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class DungeonGame extends Game {
	public SpriteBatch batch;
	public ShapeRenderer sr;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		setScreen(new DungeonScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		sr.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
