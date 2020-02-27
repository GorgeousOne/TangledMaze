package me.gorgeousone.tangledmaze.terrainmap;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.terrainmap.paths.RectSegment;
import me.gorgeousone.tangledmaze.utils.Vec2;

/**
 * A terrain map contains different information about a maze that the generators access.
 * There are different 2d-arrays for accessing and changing:
 * - the determined y-coordinates at each position of the underlying floor (can be changed if trees are leveled off)
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
			for (int z = getMinZ(); z < getMaxZ(); z++)
				setAreaType(x, z, MazeAreaType.NOT_MAZE);
		}
	}
	
	//it's a bit of cheating to pass this reference for info about path width etc. Maybe there is a better way?
	public Maze getMaze() {
		return maze;
	}
	
	public Vec2 getMinimum() {
		return minimum.clone();
	}
	
	public Vec2 getMaximum() {
		return maximum.clone();
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
	
	public void setAreaType(Vec2 point, MazeAreaType type) {
		setAreaType(point.getX(), point.getZ(), type);
	}
	
	public void setAreaType(int x, int z, MazeAreaType type) {
		shapeMap[x - getMinX()][z - getMinZ()] = type;
	}
	
	public int getFloorHeight(Vec2 point) {
		return getFloorHeight(point.getX(), point.getZ());
	}
	
	public int getFloorHeight(int x, int z) {
		return floorHeightMap[x - getMinX()][z - getMinZ()];
	}
	
	public void setFloorHeight(Vec2 point, int newY) {
		setFloorHeight(point.getX(), point.getZ(), newY);
	}
	
	public void setFloorHeight(int x, int z, int newY) {
		floorHeightMap[x - getMinX()][z - getMinZ()] = newY;
	}
	
	public int getWallHeight(Vec2 point) {
		return getWallHeight(point.getX(), point.getZ());
	}
	
	public void setWallHeight(Vec2 point, int newHeight) {
		setWallHeight(point.getX(), point.getZ(), newHeight);
	}
	
	public void setWallHeight(int x, int z, int newHeight) {
		wallHeightMap[x - getMinX()][z - getMinZ()] = newHeight;
	}
	
	public int getWallHeight(int x, int z) {
		return wallHeightMap[x - getMinX()][z - getMinZ()];
	}
	
	public int getRoofHeight(Vec2 point) {
		return getRoofHeight(point.getX(), point.getZ());
	}
	
	public int getRoofHeight(int x, int z) {
		return getFloorHeight(x, z) + getWallHeight(x, z);
	}
	
	public void mapSegment(RectSegment segment, MazeAreaType type) {
		
		for (Vec2 point : segment.getFill()) {
			if (contains(point)) setAreaType(point.getX(), point.getZ(), type);
		}
	}
}