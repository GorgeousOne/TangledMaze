package me.gorgeousone.tangledmaze.terrainmap;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.utils.Vec2;

/**
 * A terrain map contains different information about a maze that the generators access.
 * There are different 2d-arrays for accessing and changing:
 * - the determined y-coordinates at each position of the underlying floor (can be changed if trees are leveld off)
 * - the type of maze at each block (in the beginning mostly "undefined", later rather "path" or "wall")
 * - the height of the soon constructed wall at each block (also changed related to trees and slop of terrain)
 */
public class TerrainMap {
	
	private Maze maze;
	private MazeAreaType[][] shapeMap;
	private int[][] floorHeightMap;
	private int[][] wallHeightMap;
	
	private Vec2 minimum;
	private Vec2 maximum;
	private Vec2 pathStart;
	
	public TerrainMap(Vec2 minimum, Vec2 maximum, Maze maze) {
		
		this.minimum = minimum;
		this.maximum = maximum;
		this.maze = maze;
		
		int width = maximum.getX() - minimum.getX() + 1;
		int height = maximum.getZ() - minimum.getZ() + 1;
		
		shapeMap = new MazeAreaType[width][height];
		floorHeightMap = new int[width][height];
		wallHeightMap = new int[width][height];
		
		for (int x = getMinX(); x < getMaxX(); x++) {
			for (int z = getMinZ(); z < getMaxZ(); z++) {
				setType(x, z, MazeAreaType.NOT_MAZE);
			}
		}
	}
	
	public Maze getMaze() {
		return maze;
	}
	
	public int getMinX() {
		return minimum.getX();
	}
	
	public int getMinZ() {
		return minimum.getZ();
	}
	
	public int getMaxX() {
		return maximum.getX();
	}
	
	public int getMaxZ() {
		return maximum.getZ();
	}
	
	public boolean contains(Vec2 point) {
		return contains(point.getX(), point.getZ());
	}
	
	public boolean contains(int x, int z) {
		return
				x >= getMinX() && x <= getMaxX() &&
				z >= getMinZ() && z <= getMaxZ();
	}
	
	public MazeAreaType getAreaType(Vec2 point) {
		return getAreaType(point.getX(), point.getZ());
	}
	
	public MazeAreaType getAreaType(int x, int z) {
		return contains(x, z) ? shapeMap[x - getMinX()][z - getMinZ()] : null;
	}
	
	public int getFloorHeight(Vec2 point) {
		return getFloorHeight(point.getX(), point.getZ());
	}
	
	public int getFloorHeight(int x, int z) {
		return floorHeightMap[x - getMinX()][z - getMinZ()];
	}
	
	public int getWallHeight(Vec2 point) {
		return getWallHeight(point.getX(), point.getZ());
	}
	
	public int getWallHeight(int x, int z) {
		return wallHeightMap[x - getMinX()][z - getMinZ()];
	}
	
	public int getRoofHeight(int x, int z) {
		return getFloorHeight(x, z) + getWallHeight(x, z);
	}
	
	public int getRoofHeight(Vec2 point) {
		return getFloorHeight(point) + getWallHeight(point);
	}
	
	public Vec2 getPathStart() {
		return pathStart;
	}
	
	public void setPathStart(Vec2 pathStart) {
		this.pathStart = pathStart;
	}
	
	public void setType(Vec2 point, MazeAreaType type) {
		setType(point.getX(), point.getZ(), type);
	}
	
	public void setType(int x, int z, MazeAreaType type) {
		if (contains(x, z)) shapeMap[x - getMinX()][z - getMinZ()] = type;
	}
	
	public void setFloorHeight(Vec2 point, int newY) {
		setFloorHeight(point.getX(), point.getZ(), newY);
	}
	
	public void setFloorHeight(int x, int z, int newY) {
		floorHeightMap[x - getMinX()][z - getMinZ()] = newY;
	}
	
	public void setWallHeight(Vec2 point, int newHeight) {
		setWallHeight(point.getX(), point.getZ(), newHeight);
	}
	
	public void setWallHeight(int x, int z, int newHeight) {
		wallHeightMap[x - getMinX()][z - getMinZ()] = newHeight;
	}
	
	public void mapSegment(PathSegment segment, MazeAreaType type) {
		
		for (Vec2 point : segment.getFill()) {
			if (contains(point)) setType(point.getX(), point.getZ(), type);
		}
	}
}