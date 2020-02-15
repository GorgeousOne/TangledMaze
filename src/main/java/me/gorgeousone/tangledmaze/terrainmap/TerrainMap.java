package me.gorgeousone.tangledmaze.terrainmap;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.Map.Entry;

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

	public TerrainMap(Maze maze) {

		this.maze = maze;

		calculateMapSize();
		copyMazeOntoMap();

		new PathGenerator().generatePaths(this);
		new TerrainEditor().editTerrain(this);
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
		return
				point.getX() >= getMinX() && point.getX() < getMaxX() &&
						point.getZ() >= getMinZ() && point.getZ() < getMaxZ();
	}

	public MazeAreaType getAreaType(int x, int z) {
		return shapeMap[x - getMinX()][z - getMinZ()];
	}

	public MazeAreaType getAreaType(Vec2 point) {
		return getAreaType(point.getX(), point.getZ());
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

	public Vec2 getStart() {
		return pathStart;
	}

	public void setType(Vec2 point, MazeAreaType type) {
		setType(point.getX(), point.getZ(), type);
	}

	public void setType(int x, int z, MazeAreaType type) {
		shapeMap[x - getMinX()][z - getMinZ()] = type;
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

	public void setPathStart(Vec2 pathStart) {
		this.pathStart = pathStart;
	}

	public void mapSegment(PathSegment segment, MazeAreaType type) {

		for (Vec2 point : segment.getFill()) {

			if (contains(point))
				setType(point.getX(), point.getZ(), type);
		}
	}

	public void flipMap() {

		for (int x = getMinX(); x < getMaxX(); x++) {
			for (int z = getMinZ(); z < getMaxZ(); z++) {

				MazeAreaType fillType = getAreaType(x, z);

				if (fillType == MazeAreaType.UNDEFINED)
					setType(x, z, MazeAreaType.WALL);

				else if (fillType == MazeAreaType.EXIT)
					setType(x, z, MazeAreaType.PATH);
			}
		}
	}

	private void calculateMapSize() {

		minimum = getMinLoc();
		maximum = getMaxLoc();

		shapeMap = new MazeAreaType
				[maximum.getX() - minimum.getX()]
				[maximum.getZ() - minimum.getZ()];

		floorHeightMap = new int
				[maximum.getX() - minimum.getX()]
				[maximum.getZ() - minimum.getZ()];

		wallHeightMap = new int
				[maximum.getX() - minimum.getX()]
				[maximum.getZ() - minimum.getZ()];
	}

	private void copyMazeOntoMap() {

		for (int x = getMinX(); x < getMaxX(); x++) {
			for (int z = getMinZ(); z < getMaxZ(); z++) {
				setType(x, z, MazeAreaType.NOT_MAZE);
			}
		}

		int wallHeight = maze.getDimension(MazeDimension.WALL_HEIGHT);
		Clip clip = maze.getClip();

		//mark the maze's area in mazeMap as undefined area (open to become paths and walls)
		for (Entry<Vec2, Integer> point : clip.getFillEntries()) {

			setType(point.getKey(), MazeAreaType.UNDEFINED);
			setFloorHeight(point.getKey(), point.getValue());
			setWallHeight(point.getKey(), wallHeight);
		}

		//mark the border in mazeMap as walls
		for (Vec2 point : maze.getClip().getBorder()) {
			setType(point, MazeAreaType.WALL);
		}
	}

	private Vec2 getMinLoc() {

		Vec2 minimum = null;

		for (Vec2 point : maze.getClip().getFill()) {

			if (minimum == null) {

				minimum = point.clone();
				continue;
			}

			if (point.getX() < minimum.getX())
				minimum.setX(point.getX());

			if (point.getZ() < minimum.getZ())
				minimum.setZ(point.getZ());
		}

		return minimum;
	}

	private Vec2 getMaxLoc() {

		Vec2 maximum = null;

		for (Vec2 point : maze.getClip().getFill()) {

			if (maximum == null) {
				maximum = point.clone();
				continue;
			}

			if (point.getX() > maximum.getX())
				maximum.setX(point.getX());

			if (point.getZ() > maximum.getZ())
				maximum.setZ(point.getZ());
		}

		return maximum.add(1, 1);
	}
}