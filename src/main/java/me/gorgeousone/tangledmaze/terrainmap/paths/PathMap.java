package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.utils.Vec2;

public class PathMap {
	
	private PathAreaType[][] mazePathGrid;
	private Vec2 mapOffset;
	
	private Vec2 pathStartAsGridPoint;
	private int pathWidth;
	private int wallWidth;
	private int meshSize;
	
	public PathMap(Vec2 clipMin, Vec2 clipMax, Vec2 pathStart, int pathWidth, int wallWidth) {
		
		if (pathWidth < 1 || wallWidth < 1)
			throw new IllegalArgumentException("Path and wall width must be greater than 0.");
		
		this.pathWidth = pathWidth;
		this.wallWidth = wallWidth;
		this.meshSize = pathWidth + wallWidth;
		
		setUp(clipMin, clipMax, pathStart);
	}
	
	public int getGridWidth() {
		return mazePathGrid.length;
	}
	
	public int getGridHeight() {
		return mazePathGrid[0].length;
	}
	
	public boolean isFree(Vec2 gridPoint) {
		return getPathAreaType(gridPoint.getX(), gridPoint.getZ()) == PathAreaType.AVAILABLE;
	}
	
	public PathAreaType getPathAreaType(int gridX, int gridZ) {
		return mazePathGrid[gridX][gridZ];
	}
	
	public Vec2 getPathStartInGrid() {
		return pathStartAsGridPoint;
	}
	
	public void setPathAreaType(Vec2 gridPoint, PathAreaType pathAreaType) {
		setPathAreaType(gridPoint.getX(), gridPoint.getZ(), pathAreaType);
	}
	
	public void setPathAreaType(int gridX, int gridZ, PathAreaType pathAreaType) {
		mazePathGrid[gridX][gridZ] = pathAreaType;
	}
	
	//TODO remodel segments size + start into one PathSegment after cleaning up mentioned class.
	public Vec2 getSegmentStart(int gridX, int gridZ) {
		
		Vec2 segmentStart = mapOffset.clone();
		
		segmentStart.add(
				(gridX / meshSize) * meshSize,
				(gridZ / meshSize) * meshSize);
		
		segmentStart.add(
				(gridX % 2) * pathWidth,
				(gridZ % 2) * pathWidth);
		
		return segmentStart;
	}
	
	public Vec2 getSegmentSize(int gridX, int gridZ) {
		
		return new Vec2(
				gridX % 2 == 0 ? pathWidth : wallWidth,
				gridZ % 2 == 0 ? pathWidth : wallWidth);
	}
	
	private void setUp(Vec2 clipMin, Vec2 clipMax, Vec2 pathStart) {
		
		Vec2 gridOffSet = new Vec2(
				pathStart.getX() % meshSize,
				pathStart.getZ() % meshSize);
		
		mapOffset = clipMin.clone();
		mapOffset.sub(gridOffSet);
		mapOffset.set(
				(mapOffset.getX() / meshSize) * meshSize,
				(mapOffset.getZ() / meshSize) * meshSize);
		mapOffset.add(gridOffSet);
		
		int gridWidth = (int) Math.ceil(1f * (clipMax.getX() - mapOffset.getX()) / meshSize);
		int gridHeight = (int) Math.ceil(1f * (clipMax.getZ() - mapOffset.getZ()) / meshSize);
		
		createMazePathGrid(gridWidth, gridHeight);
		pathStartAsGridPoint = getGridCoordinates(pathStart);
	}
	
	private void createMazePathGrid(int width, int height) {
		
		mazePathGrid = new PathAreaType[width][height];
		
		for (int gridX = 0; gridX < getGridWidth(); gridX++) {
			for (int gridZ = 0; gridZ < getGridHeight(); gridZ++) {
				
				if (gridX % 2 == 0 && gridZ % 2 == 0)
					setPathAreaType(gridX, gridZ, PathAreaType.BLOCKED);
				else
					setPathAreaType(gridX, gridZ, PathAreaType.AVAILABLE);
			}
		}
	}
	
	private Vec2 getGridCoordinates(Vec2 point) {
		
		Vec2 relativePoint = point.clone();
		relativePoint.sub(mapOffset);
		
		Vec2 gridPoint = new Vec2(
				relativePoint.getX() / meshSize * 2,
				relativePoint.getZ() / meshSize * 2);
		
		gridPoint.add(
				relativePoint.getX() % meshSize / pathWidth,
				relativePoint.getZ() % meshSize / pathWidth);
		
		return gridPoint;
	}
}