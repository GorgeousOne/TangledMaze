package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

/**
 * A class that compresses the whole area array of a {@link me.gorgeousone.tangledmaze.terrainmap.TerrainMap} into an array where each "cell" represents
 * a segment of wall or path. After the {@link PathMapFactory} plotted all information about available and blocked segments,
 * the {@link NewPathGenerator} can easily generate the paths of the maze without needing to check itself if a segment in the maze is actually free or not.<br>
 * In the end the {@link NewPathGenerator} will plot all generated paths back into the {@link me.gorgeousone.tangledmaze.terrainmap.TerrainMap}.
 */
public class PathMap {
	
	private PathAreaType[][] mazePathGrid;
	private Vec2 gridMapOffset;
	
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
		
		compressMazeInfos(clipMin, clipMax, pathStart);
	}
	
	public Vec2 getGridMapOffset() {
		return gridMapOffset;
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
		
		if (gridX < 0 || gridX >= getGridWidth() ||
		    gridZ < 0 || gridZ >= getGridHeight())
			return PathAreaType.BLOCKED;
		
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
	
	private void compressMazeInfos(Vec2 clipMin, Vec2 clipMax, Vec2 pathStart) {
		
		calculateGridMapOffset(clipMin, pathStart);
		
		int gridWidth = 2 * (int) Math.ceil(1f * (clipMax.getX() - gridMapOffset.getX()) / meshSize);
		int gridHeight = 2 * (int) Math.ceil(1f * (clipMax.getZ() - gridMapOffset.getZ()) / meshSize);
		
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.GRAY + "clip width: " + clipMax.getX() + " - " + gridMapOffset.getX() + " = " + (clipMax.getX() - gridMapOffset.getX()));
		Bukkit.broadcastMessage(ChatColor.GRAY + "clip height: " + clipMax.getZ() + " - " + gridMapOffset.getZ() + " = " + (clipMax.getZ() - gridMapOffset.getZ()));
		Bukkit.broadcastMessage(ChatColor.GRAY + "Grid size: " + ChatColor.RESET + gridWidth + " x " + gridHeight);
		
		createMazePathGrid(gridWidth, gridHeight);
		pathStartAsGridPoint = getGridCoordinates(pathStart);
	}
	
	//TODO add comments. super useful. too tired.
	private void calculateGridMapOffset(Vec2 clipMin, Vec2 pathStart) {
		
		Bukkit.broadcastMessage(ChatColor.GRAY + "Mesh size: " + ChatColor.RESET + meshSize);
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.GRAY + "Path start: " + ChatColor.RESET + pathStart.toString());
		
		Vec2 gridOffset = new Vec2(
				pathStart.getX() % meshSize,
				pathStart.getZ() % meshSize);
		
		Bukkit.broadcastMessage(ChatColor.GRAY + "Grid offset: " + ChatColor.RESET + gridOffset.toString());
		Bukkit.broadcastMessage("");
		
		gridMapOffset = clipMin.clone();
		gridMapOffset.sub(gridOffset);
		gridMapOffset.set(
				(gridMapOffset.getX() / meshSize) * meshSize,
				(gridMapOffset.getZ() / meshSize) * meshSize);
		gridMapOffset.add(gridOffset);
	}
	
	private void createMazePathGrid(int width, int height) {
		
		mazePathGrid = new PathAreaType[width][height];
		
		for (int gridX = 0; gridX < getGridWidth(); gridX++) {
			for (int gridZ = 0; gridZ < getGridHeight(); gridZ++) {
				
				if (gridX % 2 != 0 && gridZ % 2 != 0)
					setPathAreaType(gridX, gridZ, PathAreaType.BLOCKED);
				else
					setPathAreaType(gridX, gridZ, PathAreaType.AVAILABLE);
			}
		}
	}
	
	private Vec2 getGridCoordinates(Vec2 point) {
		
		Vec2 relativePoint = point.clone();
		relativePoint.sub(gridMapOffset);
		
		Vec2 gridPoint = new Vec2(
				relativePoint.getX() / meshSize * 2,
				relativePoint.getZ() / meshSize * 2);
		
		gridPoint.add(
				relativePoint.getX() % meshSize / pathWidth,
				relativePoint.getZ() % meshSize / pathWidth);
		
		return gridPoint;
	}
	
//	public Set<NewPathSegment> getPossiblePaths() {
//
//		Set<NewPathSegment> possiblePaths = new HashSet<>();
//
//		for (int gridX = 0; gridX < getGridWidth(); gridX++) {
//			for (int gridZ = 0; gridZ < getGridHeight(); gridZ++) {
//
//				if (gridX % 2 == 0 || gridZ % 2 == 0)
//					possiblePaths.add(getSegment(gridX, gridZ));
//			}
//		}
//
//		return possiblePaths;
//	}
	
	public NewPathSegment getSegment(int gridX, int gridZ) {
		
		Vec2 segmentStart = gridMapOffset.clone();
		
		segmentStart.add(
				(gridX / 2) * meshSize,
				(gridZ / 2) * meshSize);
		
		segmentStart.add(
				(gridX % 2) * pathWidth,
				(gridZ % 2) * pathWidth);
		
		Vec2 segmentSize = new Vec2(
				gridX % 2 == 0 ? pathWidth : wallWidth,
				gridZ % 2 == 0 ? pathWidth : wallWidth);
		
		return new NewPathSegment(segmentStart, segmentSize);
	}
}