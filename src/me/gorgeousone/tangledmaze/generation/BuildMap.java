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
	
	private Vec2 minimum, maximum;
	private Vec2 pathStart;
	
	public BuildMap(Maze maze) {
		
		this.maze = maze;
		
		calculateMapSize();
		copyMazeOntoMap();
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
	
	public int getMaxX() {
		return maximum.getIntX();
	}

	public int getMaxZ() {
		return maximum.getIntZ();
	}
	
	public boolean contains(Vec2 point) {
	
		return
			point.getIntX() >= getMinX() && point.getIntX() < getMaxX() &&
			point.getIntZ() >= getMinZ() && point.getIntZ() < getMaxZ();
	}
	
	public MazeFillType getType(int x, int z) {
		return shapeMap[x-getMinX()][z-getMinZ()];
	}
	
	public MazeFillType getType(Vec2 point) {
		return getType(point.getIntX(), point.getIntZ());
	}

	public int getGroundHeight(int x, int z) {
		return groundHeightMap[x-getMinX()][z-getMinZ()];
	}
	
	public int getGroundHeight(Vec2 point) {
		return getGroundHeight(point.getIntX(), point.getIntZ());
	}
	
	public int getMazeHeight(int x, int z) {
		return mazeHeightMap[x-getMinX()][z-getMinZ()];
	}
	
	public int getMazeHeight(Vec2 point) {
		return getMazeHeight(point.getIntX(), point.getIntZ());
	}
	
	public int getWallHeight(Vec2 point) {
		return getMazeHeight(point) - getGroundHeight(point);
	}
	
	public Vec2 getStart() {
		return pathStart;
	}
	
	public void setType(int x, int z, MazeFillType type) {
		shapeMap[x-getMinX()][z-getMinZ()] = type;
	}

	public void setType(Vec2 point, MazeFillType type) {
		setType(point.getIntX(), point.getIntZ(), type);
	}
	
	public void setGroundHeight(int x, int z, int newY) {
		groundHeightMap[x-getMinX()][z-getMinZ()] = newY;
	}
	
	public void setGroundHeight(Vec2 point, int newY) {
		setGroundHeight(point.getIntX(), point.getIntZ(), newY);
	}

	public void setMazeHeight(int x, int z, int newY) {
		mazeHeightMap[x-getMinX()][z-getMinZ()] = newY;
	}

	public void setMazeHeight(Vec2 point, int newY) {
		setMazeHeight(point.getIntX(), point.getIntZ(), newY);
	}
	
	public void setStart(Vec2 pathStart) {
		this.pathStart = pathStart;
	}
	
	public void mapSegment(PathSegment segment, MazeFillType type) {
		
		for(Vec2 point : segment.getFill()) {
			
			if(contains(point))
				setType(point.getIntX(), point.getIntZ(), type);
		}
	}
	
	public void flip() {
		
		for(int x = getMinX(); x < getMaxX(); x++) {
			for(int z = getMinZ(); z < getMaxZ(); z++) {
				
				MazeFillType fillType = getType(x, z);
				
				if(fillType == MazeFillType.UNDEFINED)
					setType(x, z, MazeFillType.WALL);
					
				else if(fillType == MazeFillType.EXIT)
					setType(x, z, MazeFillType.PATH);
			}
		}
	}
	
	private void calculateMapSize() {
		
		HashSet<Chunk> chunks = maze.getClip().getChunks();

		minimum = getMinPoint(chunks);
		maximum = getMaxPoint(chunks);

		shapeMap  = new MazeFillType
			[maximum.getIntX() - minimum.getIntX()]
			[maximum.getIntZ() - minimum.getIntZ()];
		
		groundHeightMap = new int
			[maximum.getIntX() - minimum.getIntX()]
			[maximum.getIntZ() - minimum.getIntZ()];
		
		mazeHeightMap = new int
			[maximum.getIntX() - minimum.getIntX()]
			[maximum.getIntZ() - minimum.getIntZ()];
	}
	
	private void copyMazeOntoMap() {
		
		for(int x = getMinX(); x < getMaxX(); x++) {
			for(int z = getMinZ(); z < getMaxZ(); z++) {
				setType(x, z, MazeFillType.NOT_MAZE);
			}
		}
		
		int wallHeight = maze.getWallHeight();

		//mark the maze's area in mazeMap as undefined area (open for paths and walls)
		for(MazePoint point : maze.getClip().getFilling()) {
			
			Vec2 pointVec = new Vec2(point);
			
			setType(pointVec, MazeFillType.UNDEFINED);
			setGroundHeight(pointVec, point.getBlockY());
			setMazeHeight(pointVec, point.getBlockY() + wallHeight);
		}
		
		//mark the border in mazeMap as walls
		for(MazePoint point : maze.getClip().getBorder())
			//TODO
			setType(point.getBlockX(), point.getBlockZ(), MazeFillType.WALL);
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
			
			if(chunk.getX() > maximum.getX())
				maximum.setX(chunk.getX());
				
			if(chunk.getZ() > maximum.getZ())
				maximum.setZ(chunk.getZ());
		}
		
		return maximum.add(1, 1).mult(16);
	}
}