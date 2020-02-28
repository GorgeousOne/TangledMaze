package me.gorgeousone.tangledmaze.generation.pathmap;

import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.Vec2;

/**
 * A class that compresses the whole area array of a {@link TerrainMap} into an array where each "cell" represents
 * a segment of wall or path. After the {@link PathMapFactory} plotted all information about available and blocked segments,
 * the {@link PathGenerator} can easily generate the paths of the maze without needing to check itself if a segment in the maze is actually free or not.<br>
 * In the end the {@link PathGenerator} will plot all generated paths back into the {@link TerrainMap}.
 */
public class PathMap {
	
	private PathAreaType[][] mazePathsMap;
	private Vec2 pathGridOffset;
	private Vec2 pathMapOffset;
	
	private Vec2 pathStartGridPoint;
	private int pathWidth;
	private int wallWidth;
	private int meshSize;
	
	public PathMap(Vec2 clipMin, Vec2 clipMax, Vec2 pathStart, int pathWidth, int wallWidth) {
		
		if (pathWidth < 1 || wallWidth < 1)
			throw new IllegalArgumentException("Path and wall width must be greater than 0.");
		
		this.pathWidth = pathWidth;
		this.wallWidth = wallWidth;
		this.meshSize = pathWidth + wallWidth;
		
		calculateOffsets(clipMin, pathStart);
		createMazePathsMap(clipMax);
		
		this.pathStartGridPoint = getGridCoordinates(pathStart);
	}
	
	public Vec2 getGridCoordinates(Vec2 point) {
		
		Vec2 relativePoint = point.clone();
		relativePoint.sub(pathMapOffset);
		
		Vec2 gridPoint = new Vec2(
				relativePoint.getX() / meshSize * 2,
				relativePoint.getZ() / meshSize * 2);
		
		gridPoint.add(
				relativePoint.getX() % meshSize / pathWidth,
				relativePoint.getZ() % meshSize / pathWidth);
		
		return gridPoint;
	}
	
	//TODO add comments. super useful. too tired.
	private void calculateOffsets(Vec2 clipMin, Vec2 pathStart) {
		
		pathGridOffset = new Vec2(
				pathStart.getX() % meshSize,
				pathStart.getZ() % meshSize);
		
		pathMapOffset = clipMin.clone();
		pathMapOffset.sub(pathGridOffset);
		
		pathMapOffset.set(
				(pathMapOffset.getX() / meshSize) * meshSize,
				(pathMapOffset.getZ() / meshSize) * meshSize);
		
		pathMapOffset.add(pathGridOffset);
	}
	
	private void createMazePathsMap(Vec2 clipMax) {
		
		int gridWidth = 2 * (int) Math.ceil(1f * (clipMax.getX() - pathMapOffset.getX()) / meshSize);
		int gridHeight = 2 * (int) Math.ceil(1f * (clipMax.getZ() - pathMapOffset.getZ()) / meshSize);
		
		mazePathsMap = new PathAreaType[gridWidth][gridHeight];
		
		for (int gridX = 0; gridX < getGridWidth(); gridX++) {
			for (int gridZ = 0; gridZ < getGridHeight(); gridZ++) {
				
				if (gridX % 2 != 0 && gridZ % 2 != 0)
					setGridCellType(gridX, gridZ, PathAreaType.BLOCKED);
				else
					setGridCellType(gridX, gridZ, PathAreaType.AVAILABLE);
			}
		}
	}
	
	public int getGridWidth() {
		return mazePathsMap.length;
	}
	
	public int getGridHeight() {
		return mazePathsMap[0].length;
	}
	
	public void setGridCellType(int gridX, int gridZ, PathAreaType pathAreaType) {
		mazePathsMap[gridX][gridZ] = pathAreaType;
	}
	
	public Vec2 getPathMapOffset() {
		return pathMapOffset;
	}
	
	public Vec2 getPathGridOffset() {
		return pathGridOffset;
	}
	
	public Vec2 getPathStartGridPoint() {
		return pathStartGridPoint;
	}
	
	public int getPathGridMeshSize() {
		return meshSize;
	}
	
	public void setGridCellType(Vec2 gridPoint, PathAreaType pathAreaType) {
		setGridCellType(gridPoint.getX(), gridPoint.getZ(), pathAreaType);
	}
	
	public boolean isPathCellFree(Vec2 gridPoint) {
		return getGridCellType(gridPoint.getX(), gridPoint.getZ()) == PathAreaType.AVAILABLE;
	}
	
	public PathAreaType getGridCellType(int gridX, int gridZ) {
		
		if (gridX < 0 || gridX >= getGridWidth() ||
		    gridZ < 0 || gridZ >= getGridHeight())
			return PathAreaType.BLOCKED;
		
		return mazePathsMap[gridX][gridZ];
	}
	
	public PathSegment getGridCell(int gridX, int gridZ) {
		
		Vec2 segmentStart = pathMapOffset.clone();
		
		segmentStart.add(
				(gridX / 2) * meshSize,
				(gridZ / 2) * meshSize);
		
		segmentStart.add(
				(gridX % 2) * pathWidth,
				(gridZ % 2) * pathWidth);
		
		Vec2 segmentSize = new Vec2(
				gridX % 2 == 0 ? pathWidth : wallWidth,
				gridZ % 2 == 0 ? pathWidth : wallWidth);
		
		return new PathSegment(segmentStart, segmentSize);
	}
}