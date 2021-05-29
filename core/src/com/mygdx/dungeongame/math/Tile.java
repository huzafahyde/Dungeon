package com.mygdx.dungeongame.math;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Tile {

	public Rectangle rect;
	
	private TextureRegion texture;
	
	private int x, y;
	
	private int bitmask = 0;
	
	public enum TYPE{
		WALL, ROOM, CORRIDOR, WATERSOURCE, WATER, FLOOR
	}
	
	public TYPE type = TYPE.WALL;
	
	public Tile(int x, int y, int width) {
		rect = new Rectangle(x, y, width, width);
		this.x = (x / 24);
		this.y = (y / 24);
	}

	public TextureRegion getTexture() {
		return texture;
	}

	
	public void setTexture(TextureRegion texture) {
		this.texture = texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = new TextureRegion(texture);
	}
	
	public Vector2 getPosition() {
		return new Vector2(x, y);
	}

	public int getBitmask() {
		return bitmask;
	}

	public void setBitmask(int bitmask) {
		this.bitmask = bitmask;
	}
	
	public void addBitmask(int num) {
		this.bitmask += Math.pow(2, num);
	}
	
//	public void update() {
//		switch (type) {
//		case WALL:
//			texture = new Texture("test/wall.png");
//			break;
//		case ROOM:
//			
//			break;
//		case CORRIDOR:
//			
//			break;
//		default:
//			break;
//		}
//	}
	
}
