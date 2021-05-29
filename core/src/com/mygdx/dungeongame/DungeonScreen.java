package com.mygdx.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.dungeongame.math.LevelGenerator;
import com.mygdx.dungeongame.math.Room;

public class DungeonScreen implements Screen, InputProcessor {
	private DungeonGame game;

	private Viewport viewport;
	private OrthographicCamera camera;

	private LevelGenerator levelGenerator;

	private boolean camUp, camLeft, camDown, camRight, zoomIn, zoomOut;
	
	BitmapFont font;

	public DungeonScreen(DungeonGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Gdx.input.setInputProcessor(this);

		camera = new OrthographicCamera(1920, 1080);
		camera.setToOrtho(false);

		viewport = new FitViewport(1920, 1080, camera);
		viewport.apply();

		camera.zoom = 3;
		camera.update();

		levelGenerator = new LevelGenerator();
		
		font = new BitmapFont();
		font.getData().setScale(0.5f);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		ScreenUtils.clear(0, 0, 0, 1);
		Gdx.gl.glBlendEquation(GL20.GL_BLEND);
		
		moveCamera();
		camera.update();

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();

		for (int i = 0; i < levelGenerator.dWidth; i++) {
			for (int j = 0; j < levelGenerator.dHeight; j++) {
				game.batch.draw(levelGenerator.tiles[i][j].getTexture(), levelGenerator.tiles[i][j].rect.x, levelGenerator.tiles[i][j].rect.y, levelGenerator.tileWidth, levelGenerator.tileWidth);
				//font.draw(game.batch, levelGenerator.tiles[i][j].type + "", levelGenerator.tiles[i][j].rect.x, levelGenerator.tiles[i][j].rect.y + 10);
			}
		}
		
		for (int i = 0; i < levelGenerator.rooms.size(); i++) {
			//font.draw(game.batch, levelGenerator.rooms.get(i).getWidth() + ", " + levelGenerator.rooms.get(i).getHeight(),levelGenerator.rooms.get(i).getX() * 24, levelGenerator.rooms.get(i).getY() * 24 );
			
		}

		for (int i = 0; i < levelGenerator.dWidth; i++) {
			for (int j = 0; j < levelGenerator.dHeight; j++) {
				//font.draw(game.batch, levelGenerator.tiles[i][j].x + ", " + levelGenerator.tiles[i][j].y, i * 24, j * 24);
				if ( levelGenerator.tiles[i][j].getBitmask() == 249) {
					font.setColor(Color.RED);
				} else {
					font.setColor(Color.WHITE);
				}
				//font.draw(game.batch, levelGenerator.tiles[i][j].getBitmask() + "", i * 24, j * 24 + 12);
			}
		}
		
		
		
		
		game.batch.end();

		game.sr.setProjectionMatrix(camera.combined);
		game.sr.setAutoShapeType(true);
		game.sr.begin();
		game.sr.setColor(Color.WHITE);

		for (int i = 0; i < levelGenerator.dWidth; i++) {
			for (int j = 0; j < levelGenerator.dHeight; j++) {
				//game.sr.rect(levelGenerator.tiles[i][j].rect.x, levelGenerator.tiles[i][j].rect.y, 24, 24);
			}
		}

		for (int i = 0; i < levelGenerator.rooms.size(); i++) {
			if (levelGenerator.rooms.get(i).isPrimary()) {
				game.sr.setColor(Color.MAGENTA);
			} else if (levelGenerator.rooms.get(i).isMarked()) {
				game.sr.setColor(Color.RED);
			} else {
				game.sr.setColor(Color.BLUE);
			}

			//drawRect(levelGenerator.rooms.get(i).getRect(), levelGenerator.tileWidth);
		}

		game.sr.setColor(Color.LIME);
		for (int i = 0; i < levelGenerator.rooms.size(); i++) {
			//game.sr.rect(
			//		levelGenerator.rooms.get(i).getX() * levelGenerator.tileWidth
			//				+ levelGenerator.rooms.get(i).getConnectorPos().x * levelGenerator.tileWidth,
			//		levelGenerator.rooms.get(i).getY() * levelGenerator.tileWidth
			//				+ levelGenerator.rooms.get(i).getConnectorPos().y * levelGenerator.tileWidth,
			//		levelGenerator.tileWidth, levelGenerator.tileWidth);
		}

		game.sr.setColor(Color.WHITE);
		for (int i = 0; i < levelGenerator.rooms.size(); i++) {
			if (levelGenerator.rooms.get(i).childrenRooms != null) {
				Vector2 pos1 = toReal(levelGenerator.rooms.get(i).getConnectorPos(), levelGenerator.rooms.get(i));
				
				for (int j = 0; j < levelGenerator.rooms.get(i).childrenRooms.size(); j++) {
					Vector2 pos2 = toReal(levelGenerator.rooms.get((levelGenerator.rooms.get(i).childrenRooms.get(j))).getConnectorPos(), levelGenerator.rooms.get((levelGenerator.rooms.get(i).childrenRooms.get(j))));
					//game.sr.line(pos1, pos2);
				}
				
				
			}
		}
		
		for (int i = 0; i < levelGenerator.floorRectangles.size(); i++) {
			game.sr.rect(levelGenerator.floorRectangles.get(i).x, levelGenerator.floorRectangles.get(i).y, levelGenerator.tileWidth, levelGenerator.tileWidth);
		}
		

		game.sr.setColor(Color.BLUE);
		for (int i = 0; i < levelGenerator.waterRectangles.size(); i++) {
			game.sr.rect(levelGenerator.waterRectangles.get(i).x, levelGenerator.waterRectangles.get(i).y, levelGenerator.tileWidth, levelGenerator.tileWidth);
		}
		
		game.sr.end();

	}

	private Vector2 toReal(Vector2 v, Room room) {
		return new Vector2((room.getX() + v.x) * levelGenerator.tileWidth, (room.getY() + v.y) * levelGenerator.tileWidth);
	}
	
	private void drawRect(Rectangle rect, int tileSize) {
		game.sr.rect(rect.x * tileSize, rect.y * tileSize, rect.width * tileSize, rect.height * tileSize);
	}

	private void moveCamera() {
		if (camUp)
			camera.translate(0, 5);
		if (camLeft)
			camera.translate(-5, 0);
		if (camDown)
			camera.translate(0, -5);
		if (camRight)
			camera.translate(5, 0);
		if (zoomIn)
			camera.zoom -= 0.1f;
		if (zoomOut)
			camera.zoom += 0.1f;

		if (camera.zoom <= 0.3f)
			camera.zoom = 0.3f;
		
		//System.out.println(camera.position);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

		viewport.update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		font.dispose();
		levelGenerator.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == Input.Keys.W)
			camUp = true;
		if (keycode == Input.Keys.A)
			camLeft = true;
		if (keycode == Input.Keys.S)
			camDown = true;
		if (keycode == Input.Keys.D)
			camRight = true;
		if (keycode == Input.Keys.Q)
			zoomIn = true;
		if (keycode == Input.Keys.E)
			zoomOut = true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == Input.Keys.W)
			camUp = false;
		if (keycode == Input.Keys.A)
			camLeft = false;
		if (keycode == Input.Keys.S)
			camDown = false;
		if (keycode == Input.Keys.D)
			camRight = false;
		if (keycode == Input.Keys.Q)
			zoomIn = false;
		if (keycode == Input.Keys.E)
			zoomOut = false;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		levelGenerator.generate();
		// System.out.println(new Vector3(screenX, screenY, 0));
		// System.out.println(camera.unproject(new Vector3(screenX, screenY, 0)));
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}
