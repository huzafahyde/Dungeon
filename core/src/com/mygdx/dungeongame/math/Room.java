package com.mygdx.dungeongame.math;

import java.util.ArrayList;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Room {

	private Vector2 connectorPos;
	private Rectangle rect;
	
	private boolean marked = false;
	private boolean primary = false;
	private boolean connected = false;
	
	public ArrayList<Integer> childrenRooms;
	
	private int x,y,width,height;
	
	public Room(int width, int height, int dWidth, int dHeight) {

		int connectorX = ((int) (Math.random() * width));
		int connectorY = ((int) (Math.random() * height));
		
		x = ((int) (Math.random() * dWidth) + 1);
		y = ((int) (Math.random() * dHeight) + 1);
		
		this.width = width;
		this.height = height;
		
		connectorPos = new Vector2(connectorX, connectorY);
		
		rect = new Rectangle(x, y, width, height);
		
		childrenRooms = new ArrayList<Integer>();
		
	}
	
	public boolean intersects(Rectangle rect2) {
		if (Intersector.intersectRectangles(this.rect, rect2, new Rectangle())) {
			return true;
		} else {
			return false;
		}
	}

	public Vector2 getConnectorPos() {
		return connectorPos;
	}
	
	public void setConnector(int x, int y) {
		connectorPos = new Vector2(x, y);
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
}
