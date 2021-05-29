package com.mygdx.dungeongame.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.dungeongame.math.Tile.TYPE;

public class LevelGenerator {

	public Tile[][] tiles;

	public ArrayList<Room> rooms;
	ArrayList<Tile> waterSources;
	public ArrayList<Rectangle> floorRectangles, waterRectangles;
	public ArrayList<TextureRegion> wallTextures, floorTextures, waterTextures;
	
	private HashMap<Integer, Integer> map;

	private int roomTries = 20;
	public int tileWidth = 24;

	public int dWidth = 50;
	public int dHeight = 50;

	int minRoomWidth = 5;
	int maxRoomWidth = 9;
	int minRoomHeight = 5;
	int maxRoomHeight = 9;

	int separation = 2;

	int waterTries = 5;
	int waterSpread = 3;

	private Texture wall, floor, waterSource, water;

	Random rand;

	private int[] remove = new int[] { 14, 16, 18, 20, 24, 26, 27, 29, 31, 33, 35, 36, 38, 40, 42, 44, 47, 50, 53, 56,
			59, 62, 65, 68, 71 };

	private int[] remap = new int[] {208, 248, 104, 214, 255, 107, 22, 31, 11, 80, 24, 72, 66, 0, 18, 10, 64, 16, 90,
			8, 2, 88, 82, 74, 26, 95, 123, 222, 250, 127, 223, 251, 254, 86, 75, 210, 106, 120, 216, 27, 30, 218, 122,
			94, 91, 126, 219};

	public LevelGenerator() {
		rand = new Random();
		generate();
	}

	public void generate() {
		wall = new Texture("test/wall.png");
		floor = new Texture("test/floor.png");
		waterSource = new Texture("test/water.png");
		water = new Texture("test/water.png");

		generateTextures();
		generateTiles();

		while (true) {
			generateRooms();
			removeRooms();

			if (rooms.size() > 6) {
				break;
			}
		}

		createLakes();
		createRivers();

		connectRooms();
		createCorridors();

		assignType();

		
		wallOff();
		convertFloors();
		assignTextures();
		
		
		setBitmasks(TYPE.WALL);
		setFloorBitmasks(TYPE.FLOOR, TYPE.WATER);
		setBitmasks(TYPE.WATER);
		
		setWallTextures();
		setFloorTextures();
		setWaterTextures();
		
		generateMap();
	}

	private void generateTextures() {
		wallTextures = new ArrayList<TextureRegion>();
		Texture sheet = new Texture("tileSheets/1.png");
		int x = 84;
		int y = 163;

		for (int i = 0; i < 24; i++) {
			x = 84;
			for (int j = 0; j < 3; j++) {
				TextureRegion region = new TextureRegion(sheet, x, y, tileWidth, tileWidth);
				wallTextures.add(region);
				x += tileWidth + 1;
			}
			y += tileWidth + 1;
		}

		for (int i = wallTextures.size() - 1; i >= 0; i--) {
			for (int j = 0; j < remove.length; j++) {
				if (i == remove[j]) {
					wallTextures.remove(i);
				}
			}
		}
		
		/////////////////////////////////////
		
		floorTextures = new ArrayList<TextureRegion>();
		x = 309;
		y = 163;
		
		for (int i = 0; i < 24; i++) {
			x = 309;
			for (int j = 0; j < 3; j++) {
				TextureRegion region = new TextureRegion(sheet, x, y, tileWidth, tileWidth);
				floorTextures.add(region);
				x += tileWidth + 1;
			}
			y += tileWidth + 1;
		}
		
		for (int i = floorTextures.size() - 1; i >= 0; i--) {
			for (int j = 0; j < remove.length; j++) {
				if (i == remove[j]) {
					floorTextures.remove(i);
				}
			}
		}
		
		//////////////////////////
		
		waterTextures = new ArrayList<TextureRegion>();
		x = 609;
		y = 163;
		
		for (int i = 0; i < 24; i++) {
			x = 609;
			for (int j = 0; j < 3; j++) {
				TextureRegion region = new TextureRegion(sheet, x, y, tileWidth, tileWidth);
				waterTextures.add(region);
				x += tileWidth + 1;
			}
			y += tileWidth + 1;
		}
		
		for (int i = waterTextures.size() - 1; i >= 0; i--) {
			for (int j = 0; j < remove.length; j++) {
				if (i == remove[j]) {
					waterTextures.remove(i);
				}
			}
		}
		
		//////////////////////////
		
		map = new HashMap<Integer, Integer>();
		for (int i = 0; i < remap.length; i++) {
			map.put(remap[i], i);
		}
		
	}

	private void generateTiles() {
		tiles = new Tile[dWidth][dHeight];

		int x = 0;
		int y = 0;
		for (int i = 0; i < dWidth; i++) {

			y = 0;
			for (int j = 0; j < dHeight; j++) {
				tiles[i][j] = new Tile(x, y, tileWidth);
				tiles[i][j].setTexture(wall);
				y += tileWidth;
			}
			x += tileWidth;
		}
	}

	private void generateRooms() {
		Random rand = new Random();
		rooms = new ArrayList<Room>();
		for (int i = 0; i < roomTries; i++) {
			int width = rand.nextInt(maxRoomWidth - minRoomWidth) + minRoomWidth;
			int height = rand.nextInt(maxRoomHeight - minRoomHeight) + minRoomHeight;

			Room room = new Room(width, height, dWidth, dHeight);

			rooms.add(room);
		}

	}

	private void removeRooms() {
		for (int i = 0; i < rooms.size(); i++) {
			if (rooms.get(i).getX() + rooms.get(i).getWidth() > dWidth - 1
					|| rooms.get(i).getY() + rooms.get(i).getHeight() > dHeight - 1) {
				rooms.get(i).setMarked(true);
			}
		}

		int count = 1;
		for (int i = 0; i < rooms.size(); i++) {
			for (int j = count; j < rooms.size(); j++) {

				Rectangle rect2 = new Rectangle(rooms.get(j).getRect().x - separation,
						rooms.get(j).getRect().y - separation, rooms.get(j).getRect().width + 2 * separation,
						rooms.get(j).getRect().height + 2 * separation);
				if (rooms.get(i).intersects(rect2)) {
					rooms.get(j).setMarked(true);
				}
			}
			count++;
		}

		for (int i = rooms.size() - 1; i >= 0; i--) {
			if (rooms.get(i).isMarked())
				rooms.remove(i);
		}
	}

	private void connectRooms() {
		Random rand = new Random();

		int primary = rand.nextInt(rooms.size());

		ArrayList<Integer> roomsLeft = new ArrayList<Integer>();
		for (int i = 0; i < rooms.size(); i++) {
			roomsLeft.add(i);
		}
		roomsLeft.remove(primary);

		rooms.get(primary).setPrimary(true);
		rooms.get(primary).setConnected(true);

		for (int i = 0; i < 3; i++) {
			int secondaryChoice = rand.nextInt(roomsLeft.size());
			int secondary = roomsLeft.get(secondaryChoice);
			roomsLeft.remove(secondaryChoice);
			rooms.get(primary).childrenRooms.add(secondary);
		}

		int leftForEach = (int) (roomsLeft.size() / 3);

		for (int i = 0; i < 3; i++) {

			for (int j = 0; j < leftForEach; j++) {
				int tertiaryChoice = rand.nextInt(roomsLeft.size());
				int tertiary = roomsLeft.get(tertiaryChoice);
				roomsLeft.remove(tertiaryChoice);
				rooms.get(rooms.get(primary).childrenRooms.get(i)).childrenRooms.add(tertiary);
			}
		}

		for (int i = 0; i < roomsLeft.size(); i++) {
			int remainingRoom = roomsLeft.get(i);
			rooms.get(primary).childrenRooms.add(remainingRoom);

			rooms.get(rand.nextInt(rooms.size())).childrenRooms.add(remainingRoom);
		}

	}

	private void assignType() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				Rectangle rect = new Rectangle(tiles[i][j].rect);

				for (int k = 0; k < rooms.size(); k++) {
					if (rect.overlaps(tileToRect(rooms.get(k).getRect()))) {
						// System.out.println(i + ", " + j);
						tiles[i][j].type = TYPE.ROOM;
					}
				}

			}
		}
	}

	private void assignTextures() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (tiles[i][j].type == TYPE.ROOM || tiles[i][j].type == TYPE.CORRIDOR) {
					tiles[i][j].setTexture(floor);
				} else if (tiles[i][j].type == TYPE.WATERSOURCE) {
					tiles[i][j].setTexture(waterSource);
				} else if (tiles[i][j].type == TYPE.WATER) {
					tiles[i][j].setTexture(water);
				}
			}
		}
	}

	private Rectangle tileToRect(Rectangle rect) {
		Rectangle rectangle = new Rectangle(rect.x * tileWidth, rect.y * tileWidth, rect.width * tileWidth,
				rect.height * tileWidth);
		// System.out.println(rectangle);
		return rectangle;
	}

	private void createCorridors() {
		for (int i = 0; i < rooms.size(); i++) {
			for (int j = 0; j < rooms.get(i).childrenRooms.size(); j++) {

				Vector2 currentPos = new Vector2(rooms.get(i).getConnectorPos().x + rooms.get(i).getX(),
						rooms.get(i).getConnectorPos().y + rooms.get(i).getY());

				Room destinationRoom = rooms.get(rooms.get(i).childrenRooms.get(j));
				Vector2 destinationPos = new Vector2(destinationRoom.getConnectorPos().x + destinationRoom.getX(),
						destinationRoom.getConnectorPos().y + destinationRoom.getY());

				Vector2 direction = new Vector2(destinationPos.x - currentPos.x, destinationPos.y - currentPos.y);

				while (direction.x != 0 || direction.y != 0) {
					double rand = Math.random();
					if (rand >= 0.5) { // x
						if (direction.x > 0) {
							currentPos.x += 1;
							direction.x -= 1;
						} else if (direction.x < 0) {
							currentPos.x -= 1;
							direction.x += 1;
						}
					} else { // y
						if (direction.y > 0) {
							currentPos.y += 1;
							direction.y -= 1;
						} else if (direction.y < 0) {
							currentPos.y -= 1;
							direction.y += 1;
						}
					}
					// System.out.println(currentPos.x + ", " + currentPos.y);
					tiles[(int) currentPos.x][(int) currentPos.y].type = TYPE.CORRIDOR;
				}

			}
		}
	}

	private void createLakes() {
		waterSources = new ArrayList<Tile>();
		for (int i = 0; i < waterTries; i++) {
			int x = rand.nextInt(tiles.length);
			int y = rand.nextInt(tiles[i].length);

			if (tiles[x][y].type == TYPE.WALL) {
				tiles[x][y].type = TYPE.WATERSOURCE;
				waterSources.add(tiles[x][y]);
			}
		}

		for (int i = 0; i < dWidth; i++) {
			for (int j = 0; j < dHeight; j++) {

				for (int k = 0; k < waterSources.size(); k++) {

					double distance = distance(waterSources.get(k).getPosition(), new Vector2(i, j));
					double probability = Math.exp(-(distance - waterSpread));
					// double probability = 1 / Math.pow(
					// (distance(waterSources.get(k).getPosition(), new Vector2(i, j)) - 4), 4);

					double num = rand.nextDouble();
					if (num <= probability) {
						tiles[i][j].type = TYPE.WATER;
					}

				}

			}
		}

//		for (int i = 0; i < waterSources.size(); i++) {
//			tiles[waterSources.get(i).x][waterSources.get(i).y].type = TYPE.WATERSOURCE;
//		}

		for (int i = 0; i < dWidth; i++) {
			for (int j = 0; j < dHeight; j++) {
				if (tiles[i][j].type == TYPE.WATER) {
					int surround = 0;
					if (i + 1 < dWidth && tiles[i + 1][j].type == TYPE.WATER)
						surround += 1;
					if (i - 1 >= 0 && tiles[i - 1][j].type == TYPE.WATER)
						surround += 1;
					if (j + 1 < dHeight && tiles[i][j + 1].type == TYPE.WATER)
						surround += 1;
					if (j - 1 >= 0 && tiles[i][j - 1].type == TYPE.WATER)
						surround += 1;

					if (surround == 0) {
						tiles[i][j].type = TYPE.WALL;
					}
				}
			}
		}
	}

	private void createRivers() {
		for (int i = 0; i < waterSources.size(); i++) {

			Vector2 currentPos = new Vector2(waterSources.get(i).getPosition());
			Vector2 destinationPos = new Vector2(rand.nextInt(tiles.length), rand.nextInt(tiles[0].length));

			if (i + 1 < waterSources.size()) {
				destinationPos = new Vector2(waterSources.get(i + 1).getPosition());
			}

			Vector2 direction = new Vector2(destinationPos.x - currentPos.x, destinationPos.y - currentPos.y);

			while (direction.x != 0 || direction.y != 0) {
				double rand = Math.random();
				if (rand >= 0.5) { // x
					if (direction.x > 0) {
						currentPos.x += 1;
						direction.x -= 1;
					} else if (direction.x < 0) {
						currentPos.x -= 1;
						direction.x += 1;
					}
				} else { // y
					if (direction.y > 0) {
						currentPos.y += 1;
						direction.y -= 1;
					} else if (direction.y < 0) {
						currentPos.y -= 1;
						direction.y += 1;
					}
				}
				// System.out.println(currentPos.x + ", " + currentPos.y);
				tiles[(int) currentPos.x][(int) currentPos.y].type = TYPE.WATER;
			}

		}
	}

	private double distance(Vector2 v1, Vector2 v2) {
		return Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2));
	}

	private void setWallTextures() {
		for (int i = 0; i < dWidth; i++) {
			for (int j = 0; j < dHeight; j++) {
				if (tiles[i][j].type == TYPE.WALL) {
					//System.out.println(tiles[i][j].getBitmask());
					tiles[i][j].setTexture(wallTextures.get(map.get(tiles[i][j].getBitmask())));
				}
				
				
			}
		}
	}
	
	private void setFloorTextures() {
		for (int i = 0; i < dWidth; i++) {
			for (int j = 0; j < dHeight; j++) {
				if (tiles[i][j].type == TYPE.FLOOR) {
					// System.out.println(tiles[i][j].getBitmask());
					tiles[i][j].setTexture(floorTextures.get(map.get(tiles[i][j].getBitmask())));
				}
				
				
			}
		}
	}
	
	private void setWaterTextures() {
		for (int i = 0; i < dWidth; i++) {
			for (int j = 0; j < dHeight; j++) {
				if (tiles[i][j].type == TYPE.WATER) {
					// System.out.println(tiles[i][j].getBitmask());
					tiles[i][j].setTexture(waterTextures.get(map.get(tiles[i][j].getBitmask())));
				}
				
				
			}
		}
	}

	private void setBitmasks(TYPE type) {
		for (int i = 0; i < dWidth; i++) {
			for (int j = 0; j < dHeight; j++) {
				if (tiles[i][j].type == type) {
					boolean up = false;
					boolean down = false;
					boolean left = false;
					boolean right = false;

					if (i - 1 < 0) {
						left = true;
						tiles[i][j].addBitmask(3);
					} else if (tiles[i - 1][j].type == type) {
						left = true;
						tiles[i][j].addBitmask(3);
					}

					if (i + 1 >= dWidth) {
						right = true;
						tiles[i][j].addBitmask(4);
					} else if (tiles[i + 1][j].type == type) {
						right = true;
						tiles[i][j].addBitmask(4);
					}

					if (j - 1 < 0) {
						down = true;
						tiles[i][j].addBitmask(6);
					} else if (tiles[i][j - 1].type == type) {
						down = true;
						tiles[i][j].addBitmask(6);
					}

					if (j + 1 >= dHeight) {
						up = true;
						tiles[i][j].addBitmask(1);
					} else if (tiles[i][j + 1].type == type) {
						up = true;
						tiles[i][j].addBitmask(1);
					}

					if (i - 1 < 0 || j + 1 >= dHeight) {
						tiles[i][j].addBitmask(0);
					} else if (tiles[i - 1][j + 1].type == type && up && left) {
						tiles[i][j].addBitmask(0);
					}

					if (i + 1 >= dWidth || j + 1 >= dHeight) {
						tiles[i][j].addBitmask(2);
					} else if (tiles[i + 1][j + 1].type == type && up && right) {
						tiles[i][j].addBitmask(2);
					}

					if (i - 1 < 0 || j - 1 < 0) {
						tiles[i][j].addBitmask(5);
					} else if (tiles[i - 1][j - 1].type == type && down && left) {
						tiles[i][j].addBitmask(5);
					}

					if (i + 1 >= dWidth || j - 1 < 0) {
						tiles[i][j].addBitmask(7);
					} else if (tiles[i + 1][j - 1].type == type && down && right) {
						tiles[i][j].addBitmask(7);
					}
				}
			}
		}

	}
	
	private void setFloorBitmasks(TYPE type1, TYPE type2 ) {
		for (int i = 0; i < dWidth; i++) {
			for (int j = 0; j < dHeight; j++) {
				if (tiles[i][j].type == type1) {
					boolean up = false;
					boolean down = false;
					boolean left = false;
					boolean right = false;

					if (i - 1 < 0) {
						left = true;
						tiles[i][j].addBitmask(3);
					} else if (tiles[i - 1][j].type == type1 || tiles[i - 1][j].type == type2) {
						left = true;
						tiles[i][j].addBitmask(3);
					}

					if (i + 1 >= dWidth) {
						right = true;
						tiles[i][j].addBitmask(4);
					} else if (tiles[i + 1][j].type == type1 || tiles[i + 1][j].type == type2) {
						right = true;
						tiles[i][j].addBitmask(4);
					}

					if (j - 1 < 0) {
						down = true;
						tiles[i][j].addBitmask(6);
					} else if (tiles[i][j - 1].type == type1 || tiles[i][j - 1].type == type2) {
						down = true;
						tiles[i][j].addBitmask(6);
					}

					if (j + 1 >= dHeight) {
						up = true;
						tiles[i][j].addBitmask(1);
					} else if (tiles[i][j + 1].type == type1 || tiles[i][j + 1].type == type2) {
						up = true;
						tiles[i][j].addBitmask(1);
					}

					if (i - 1 < 0 || j + 1 >= dHeight) {
						tiles[i][j].addBitmask(0);
					} else if ((tiles[i - 1][j + 1].type == type1 || tiles[i - 1][j + 1].type == type2) && up && left) {
						tiles[i][j].addBitmask(0);
					}

					if (i + 1 >= dWidth || j + 1 >= dHeight) {
						tiles[i][j].addBitmask(2);
					} else if ((tiles[i + 1][j + 1].type == type1 || tiles[i + 1][j + 1].type == type2) && up && right) {
						tiles[i][j].addBitmask(2);
					}

					if (i - 1 < 0 || j - 1 < 0) {
						tiles[i][j].addBitmask(5);
					} else if ((tiles[i - 1][j - 1].type == type1 || tiles[i - 1][j - 1].type == type2) && down && left) {
						tiles[i][j].addBitmask(5);
					}

					if (i + 1 >= dWidth || j - 1 < 0) {
						tiles[i][j].addBitmask(7);
					} else if ((tiles[i + 1][j - 1].type == type1 || tiles[i + 1][j - 1].type == type2) && down && right) {
						tiles[i][j].addBitmask(7);
					}
				}
			}
		}

	}
	
	private void wallOff() {
		for (int i  = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length;j++) {
				if (i == 0 || i == dWidth - 1 || j == 0 || j == dHeight - 1) {
					tiles[i][j].type = TYPE.WALL;
				}
			}
		}
	}

	private void convertFloors() {
		for (int i  = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length;j++) {
				if (tiles[i][j].type == TYPE.ROOM || tiles[i][j].type == TYPE.CORRIDOR) {
					tiles[i][j].type = TYPE.FLOOR;
				}
			}
		}
	}
	
	private void generateMap() {

		floorRectangles = new ArrayList<Rectangle>();
		
		for (int i = 0; i < dWidth ; i++) {
			for (int j = 0; j < dHeight; j++) {
				if (tiles[i][j].type == TYPE.FLOOR) {
					floorRectangles.add(new Rectangle(tiles[i][j].rect));
				}
			}
		}
		
		waterRectangles = new ArrayList<Rectangle>();
		
		for (int i = 0; i < dWidth ; i++) {
			for (int j = 0; j < dHeight; j++) {
				if (tiles[i][j].type == TYPE.WATER) {
					waterRectangles.add(new Rectangle(tiles[i][j].rect));
				}
			}
		}
		
		
		
	}

	
	public void dispose() {
		wall.dispose();
		floor.dispose();
	}

}
