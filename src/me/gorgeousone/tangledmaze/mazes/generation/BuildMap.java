package me.gorgeousone.tangledmaze.mazes.generation;

import java.util.HashSet;

import org.bukkit.Chunk;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.utils.MazePoint;
import me.gorgeousone.tangledmaze.utils.Vec2;

public class BuildMap {
	
	private Maze maze;
	private MazeFillType[][] shapeMap;
	private int[][] heightMap;
	
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
	
	public MazeFillType getType(Vec2 point) {
		return shapeMap[point.getIntX()]
					   [point.getIntZ()];
	}
	
	public int getHeight(Vec2 point) {
		return heightMap[point.getIntX()]
						[point.getIntZ()];
	}
	
	public Vec2 getStart() {
		return pathStart;
	}
	
	public void setStart(Vec2 pathStart) {
		this.pathStart = pathStart;
	}
	
	public void setType(Vec2 point, MazeFillType type) {
		shapeMap[point.getIntX()]
				[point.getIntZ()] = type;
	}
	
	public void drawSegment(PathSegment segment, MazeFillType type) {
		
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
		Vec2 max = getMaxPoin(chunks);
		
		shapeMap  = new MazeFillType
			[max.getIntX() - minimum.getIntX()]
			[max.getIntZ() - minimum.getIntZ()];
		
		heightMap = new int
			[max.getIntX() - minimum.getIntX()]
			[max.getIntZ() - minimum.getIntZ()];
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
	
	private Vec2 getMaxPoin(HashSet<Chunk> chunks) {
		
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
	
	private void drawBlankMazeOnMap() {
		//mark the maze's area in mazeMap as undefined area (open for paths and walls)
		for(MazePoint point : maze.getClip().getFill()) {
				shapeMap [point.getBlockX() - getMinX()][point.getBlockZ() - getMinZ()] = MazeFillType.UNDEFINED;
				heightMap[point.getBlockX() - getMinX()][point.getBlockZ() - getMinZ()] = point.getBlockY();
		}
		
		//mark the border in mazeMap as walls
		for(MazePoint point : maze.getClip().getBorder()) {
				shapeMap[point.getBlockX() - getMinX()][point.getBlockZ() - getMinZ()] = MazeFillType.WALL;
		}
	}
}