package me.tangledmaze.gorgeousone.mazes.generators;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.mazes.Maze;

public class MazeMap {
	
	private Maze maze;
	private int minX, minZ;
	private int[][] shapeMap, heightMap;
	
	private Vector pathStart;
	
	public MazeMap(Maze maze) {
		
		this.maze = maze;
		ArrayList<Chunk> chunks = maze.getChunks();
		
		//select a probably random chunk from the maze's chunk list
		Chunk randomChunk = chunks.get(0);
		
		//initialize the maze's minimal and maximal coordinates with the chunk
		minX = randomChunk.getX();
		minZ = randomChunk.getZ();
		
		int
			mazeMaxX = minX,
			mazeMaxZ = minZ;
		
		//look for chunks with greater/smaller coordinates
		for(Chunk c : chunks) {
			if(c.getX() < minX)
				minX = c.getX();
			else if(c.getX() > mazeMaxX)
				mazeMaxX = c.getX();

			if(c.getZ() < minZ)
				minZ = c.getZ();
			else if(c.getZ() > mazeMaxZ)
				mazeMaxZ = c.getZ();
		}
		
		//multiply with a chunk's length to get to normal location coordinates
		minX *= 16;
		minZ *= 16;
		mazeMaxX *= 16;
		mazeMaxZ *= 16;
		
		//create two 2D-arrays, one for locations occupied by the maze, another one for the y-coordinates there
		shapeMap   = new int[mazeMaxX - minX + 16][mazeMaxZ - minZ + 16];
		heightMap = new int[mazeMaxX - minX + 16][mazeMaxZ - minZ + 16];
		
		//mark the maze's area in mazeMap as undefined area (open for path or walls), the will stay untouched
		for(ArrayList<Location> chunk : maze.getFill().values())
			for(Location point : chunk) {
				shapeMap [point.getBlockX() - minX][point.getBlockZ() - minZ] = MazeSegment.UNDEFINED;
				heightMap[point.getBlockX() - minX][point.getBlockZ() - minZ] = point.getBlockY();
			}
		
		//mark the border in mazeMap as reserved for walls
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				shapeMap[point.getBlockX() - minX][point.getBlockZ() - minZ] = MazeSegment.WALL;
	}
	
	public Maze getMaze() {
		return maze;
	}
	
	public int getMinX() {
		return minX;
	}

	public int getMinZ() {
		return minZ;
	}

	public int[][] getShapeMap() {
		return shapeMap;
	}

	public int[][] getHeightMap() {
		return heightMap;
	}
	
	public Vector getStart() {
		return pathStart;
	}
	
	public void setStart(Vector pathStart) {
		this.pathStart = pathStart;
	}
}