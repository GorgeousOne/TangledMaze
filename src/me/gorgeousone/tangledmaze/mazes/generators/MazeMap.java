package me.gorgeousone.tangledmaze.mazes.generators;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.utils.Vec2;
public class MazeMap {
	
	private Maze maze;
	private int minX, minZ;
	private int[][] shapeMap, heightMap;
	
	private Vec2 pathStart;
	
	public MazeMap(Maze maze) {
		
		this.maze = maze;
		
		calculateMapSize();
		drawBlankMazeAreaOnMap();
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
	
	public int getDimX() {
		return shapeMap.length;
	}
	
	public int getDimZ() {
		return shapeMap[0].length;
	}
	
	public int getType(Vec2 point) {
		return shapeMap[point.getX()]
					   [point.getZ()];
	}
	
	public int getHeight(Vec2 point) {
		return heightMap[point.getX()]
						[point.getZ()];
	}
	
	public Vec2 getStart() {
		return pathStart;
	}
	
	public void setStart(Vec2 pathStart) {
		this.pathStart = pathStart;
	}
	
	public void setType(Vec2 point, int type) {
		shapeMap[point.getX()]
				[point.getZ()] = type;
	}
	
	private void calculateMapSize() {
		//select a probably random chunk from the maze's chunk list
		ArrayList<Chunk> chunks = maze.getChunks();
		Chunk randomChunk = chunks.get(0);
		
		//initialize the maze's minimal and maximal coordinates with the chunk
		minX = randomChunk.getX();
		minZ = randomChunk.getZ();
		
		int
			maxX = minX,
			maxZ = minZ;
		
		//look for chunks with greater/smaller coordinates
		for(Chunk c : chunks) {
			if(c.getX() < minX)
				minX = c.getX();
			else if(c.getX() > maxX)
				maxX = c.getX();

			if(c.getZ() < minZ)
				minZ = c.getZ();
			else if(c.getZ() > maxZ)
				maxZ = c.getZ();
		}
		
		//multiply with a chunk's length to get to normal location coordinates
		minX *= 16;
		minZ *= 16;
		maxX *= 16;
		maxZ *= 16;
		
		//create two 2D-arrays, one for locations occupied by the maze, another one for the y-coordinates there
		shapeMap  = new int[maxX - minX + 16][maxZ - minZ + 16];
		heightMap = new int[maxX - minX + 16][maxZ - minZ + 16];
	}
	
	private void drawBlankMazeAreaOnMap() {
		//mark the maze's area in mazeMap as undefined area (open for paths or walls), the rest will stay untouched
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
}