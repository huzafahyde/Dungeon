package com.mygdx.dungeongame.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Entity {
	
	private Sprite sprite;
	private int x, y;
	

	public Entity(Sprite sprite, int x, int y) {
		// TODO Auto-generated constructor stub
		this.sprite = sprite;
		this.x = x;
		this.y = y;
	}

	
	public void move(int x, int y) {
		setPos(getX() + x, getY() + y); 
	}

	public Sprite getSprite() {
		return sprite;
	}


	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}
	
	public Vector2 getPos() {
		return new Vector2(x,y);
	}
	
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

}
