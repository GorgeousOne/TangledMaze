package me.gorgeousone.tangledmaze.generation;

import java.util.Map.Entry;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.generation.path.PathSegment;
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
	
	public MazeFillType getType(int x, int z) {
		return shapeMap[x-getMinX()][z-getMinZ()];
	}
	
	public MazeFillType getType(Vec2 point) {
		return getType(point.getX(), point.getZ());
	}

	public int getGroundHeight(int x, int z) {
		return groundHeightMap[x-getMinX()][z-getMinZ()];
	}
	
	public int getGroundHeight(Vec2 point) {
		return getGroundHeight(point.getX(), point.getZ());
	}
	
	public int getMazeHeight(int x, int z) {
		return mazeHeightMap[x-getMinX()][z-getMinZ()];
	}
	
	public int getMazeHeight(Vec2 point) {
		return getMazeHeight(point.getX(), point.getZ());
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
		setType(point.getX(), point.getZ(), type);
	}
	
	public void setGroundHeight(int x, int z, int newY) {
		groundHeightMap[x-getMinX()][z-getMinZ()] = newY;
	}
	
	public void setGroundHeight(Vec2 point, int newY) {
		setGroundHeight(point.getX(), point.getZ(), newY);
	}

	public void setMazeHeight(int x, int z, int newY) {
		mazeHeightMap[x-getMinX()][z-getMinZ()] = newY;
	}

	public void setMazeHeight(Vec2 point, int newY) {
		setMazeHeight(point.getX(), point.getZ(), newY);
	}
	
	public void setStart(Vec2 pathStart) {
		this.pathStart = pathStart;
	}
	
	public void mapSegment(PathSegment segment, MazeFillType type) {
		
		for(Vec2 point : segment.getFill()) {
			
			if(contains(point))
				setType(point.getX(), point.getZ(), type);
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
	
	//TODO overthink map size calculation
	private void calculateMapSize() {
		
		minimum = getMinLoc();
		maximum = getMaxLoc();
		
		shapeMap = new MazeFillType
			[maximum.getX() - minimum.getX()]
			[maximum.getZ() - minimum.getZ()];
		
		groundHeightMap = new int
			[maximum.getX() - minimum.getX()]
			[maximum.getZ() - minimum.getZ()];
		
		mazeHeightMap = new int
			[maximum.getX() - minimum.getX()]
			[maximum.getZ() - minimum.getZ()];
	}
	
	private void copyMazeOntoMap() {
		
		for(int x = getMinX(); x < getMaxX(); x++) {
			for(int z = getMinZ(); z < getMaxZ(); z++) {
				setType(x, z, MazeFillType.NOT_MAZE);
			}
		}
		
		int wallHeight = maze.getWallHeight();
		Clip clip = maze.getClip();
		
		//mark the maze's area in mazeMap as undefined area (open for paths and walls)
		for(Entry<Vec2, Integer> loc : clip.getFillSet()) {
			
			setType(loc.getKey(), MazeFillType.UNDEFINED);
			setGroundHeight(loc.getKey(), loc.getValue());
			setMazeHeight(loc.getKey(), loc.getValue() + wallHeight);
		}
		
		//mark the border in mazeMap as walls
		for(Vec2 loc : maze.getClip().getBorder())
			setType(loc, MazeFillType.WALL);
	}
	
	private Vec2 getMinLoc() {
		
		Vec2 minimum = null;
//		Vec2 maximum = null;

		for(Vec2 loc : maze.getClip().getFill()) {
			
			if(minimum == null) {
				
				minimum = loc.clone();
//				maximum = loc.clone();
				continue;
			}
			
			if(loc.getX() < minimum.getX())
				minimum.setX(loc.getX());
				
			if(loc.getZ() < minimum.getZ())
				minimum.setZ(loc.getZ());
		}
		
		return minimum;
	}
	
	private Vec2 getMaxLoc() {
		
		Vec2 maximum = null;
		
		for(Vec2 chunk : maze.getClip().getFill()) {
			
			if(maximum == null) {
				maximum = chunk.clone();
				continue;
			}
			
			if(chunk.getX() > maximum.getX())
				maximum.setX(chunk.getX());
				
			if(chunk.getZ() > maximum.getZ())
				maximum.setZ(chunk.getZ());
		}
		
		return maximum.add(1, 1);
	}
}