package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.maze.Maze;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MazeBackup {
	
	private Maze maze;
	private Map<MazePart, Set<LocatedBlockData>> mazePartBlocks;
	private TerrainMap terrainMap;
	
	public MazeBackup(Maze maze) {
		
		this.maze = maze;
		this.mazePartBlocks = new HashMap<>();
	}
	
	public Maze getMaze() {
		return maze;
	}
	
	public TerrainMap getTerrainMap() {
		return terrainMap;
	}
	
	public void setTerrainMap(TerrainMap terrainMap) {
		this.terrainMap = terrainMap;
	}
	
	public Set<LocatedBlockData> getBlocks(MazePart part) {
		return mazePartBlocks.get(part);
	}
	
	public void setBlocks(MazePart part, Set<LocatedBlockData> blocks) {
		mazePartBlocks.put(part, blocks);
	}
	
	public void deleteMazePart(MazePart mazePart) {
		mazePartBlocks.remove(mazePart);
	}
	
	public boolean isEmpty() {
		return mazePartBlocks.isEmpty();
	}
	
	public boolean hasBlocksFor(MazePart part) {
		return mazePartBlocks.containsKey(part);
	}
}