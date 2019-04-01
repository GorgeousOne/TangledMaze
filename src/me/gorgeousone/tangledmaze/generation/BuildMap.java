package me.gorgeousone.tangledmaze.generation;

import java.util.HashSet;

import org.bukkit.Chunk;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Vec2;

public class BuildMap {
	
	private Maze maze;
	private MazeFillType[][] shapeMap;
	private int[][] groundHeightMap, mazeHeightMap;
	
	private Vec2 minimum;
	private Vec2 pathStart;
	
	public BuildMap(Maze maze) {
		
		this.maze = maze;
		
		calculateMapSize();
		drawBlankMazeOnMap();
	}
	
	public Maze getMaze() {
		return maze;
	}
	
	public int getMinX() {
		return minimum.getIntX();
	}

	public int getMinZ() {
		return minimum.getIntZ();
	}
	
	public int getDimX() {
		return shapeMap.length;
	}
	
	public int getDimZ() {
		return shapeMap[0].length;
	}

	public MazeFillType getType(int x, int z) {
		return shapeMap[x][z];
	}

	public int getGroundHeight(int x, int z) {
		return groundHeightMap[x][z];
	}
	
	public int getGroundHeight(Vec2 point) {
		return groundHeightMap[point.getIntX()][point.getIntZ()];
	}
	
	public int getMazeHeight(int x, int z) {
		return mazeHeightMap[x][z];
	}
	
	public int getMazeHeight(Vec2 point) {
		return mazeHeightMap[point.getIntX()][point.getIntZ()];
	}
	
	public int getWallHeight(Vec2 point) {
		return getMazeHeight(point) - getGroundHeight(point);
	}
	
	public MazeFillType getType(Vec2 point) {
		return shapeMap[point.getIntX()][point.getIntZ()];
	}

	public Vec2 getStart() {
		return pathStart;
	}
	
	public void setGroundHeight(int x, int z, int newY) {
		groundHeightMap[x][z] = newY;
	}
	
	public void setGroundHeight(Vec2 point, int newY) {
		setGroundHeight(point.getIntX(), point.getIntZ(), newY);
	}

	public void setMazeHeight(int x, int z, int newY) {
		mazeHeightMap[x][z] = newY;
	}

	public void setMazeHeight(Vec2 point, int newY) {
		setMazeHeight(point.getIntX(), point.getIntZ(), newY);
	}

	public void setType(int x, int z, MazeFillType type) {
		shapeMap[x][z] = type;
	}

	public void setType(Vec2 point, MazeFillType type) {
		shapeMap[point.getIntX()]
				[point.getIntZ()] = type;
	}

	public void setStart(Vec2 pathStart) {
		this.pathStart = pathStart;
	}
	
	public void mapSegment(PathSegment segment, MazeFillType type) {
		
		for(Vec2 point : segment.getFill()) {
			
			if(point.getIntX() >= 0 && point.getIntX() < getDimX() &&
			   point.getIntZ() >= 0 && point.getIntZ() < getDimZ()) {
				
				setType(point, type);
			}
		}
	}
	
	private void calculateMapSize() {
		
		HashSet<Chunk> chunks = maze.getClip().getChunks();

		minimum = getMinPoint(chunks);
		Vec2 max = getMaxPoint(chunks);

		shapeMap  = new MazeFillType
			[max.getIntX() - minimum.getIntX()]
			[max.getIntZ() - minimum.getIntZ()];
		
		groundHeightMap = new int
			[max.getIntX() - minimum.getIntX()]
			[max.getIntZ() - minimum.getIntZ()];
		
		mazeHeightMap = new int
			[max.getIntX() - minimum.getIntX()]
			[max.getIntZ() - minimum.getIntZ()];
	}
	
	private void drawBlankMazeOnMap() {
		
		int wallHeight = maze.getWallHeight();
		
		for(int x = 0; x < getDimX(); x++) {
			for(int z = 0; z < getDimZ(); z++) {
				shapeMap[x][z] = MazeFillType.NOT_MAZE;
			}
		}
		
		//mark the maze's area in mazeMap as undefined area (open for paths and walls)
		for(MazePoint point : maze.getClip().getFilling()) {
			
			shapeMap       [point.getBlockX() - getMinX()][point.getBlockZ() - getMinZ()] = MazeFillType.UNDEFINED;
			groundHeightMap[point.getBlockX() - getMinX()][point.getBlockZ() - getMinZ()] = point.getBlockY();
			mazeHeightMap  [point.getBlockX() - getMinX()][point.getBlockZ() - getMinZ()] = point.getBlockY() + wallHeight;
		}
		
		//mark the border in mazeMap as walls
		for(MazePoint point : maze.getClip().getBorder()) {
			shapeMap[point.getBlockX() - getMinX()][point.getBlockZ() - getMinZ()] = MazeFillType.WALL;
		}
	}

	private Vec2 getMinPoint(HashSet<Chunk> chunks) {
		
		Vec2 minimum = null;

		for(Chunk chunk : chunks) {
			
			if(minimum == null) {
				minimum = new Vec2(chunk.getX(), chunk.getZ());
				continue;
			}
			
			if(chunk.getX() < minimum.getX()) {
				minimum.setX(chunk.getX());
			}
				
			if(chunk.getZ() < minimum.getZ()) {
				minimum.setZ(chunk.getZ());
			}
		}
		
		return minimum.mult(16);
	}
	
	private Vec2 getMaxPoint(HashSet<Chunk> chunks) {
		
		Vec2 maximum = null;
		
		for(Chunk chunk : chunks) {
			
			if(maximum == null) {
				maximum = new Vec2(chunk.getX(), chunk.getZ());
				continue;
			}
			
			if(chunk.getX() > maximum.getX()) {
				maximum.setX(chunk.getX());
			}
				
			if(chunk.getZ() > maximum.getZ()) {
				maximum.setZ(chunk.getZ());
			}
		}
		
		return maximum.add(1, 1).mult(16);
	}
}