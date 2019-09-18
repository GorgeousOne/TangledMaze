package me.gorgeousone.tangledmaze.generation;

import java.util.Map.Entry;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.util.Vec2;

public class TerrainMap {
	
	private Maze maze;
	private MazeAreaType[][] shapeMap;
	private int[][] floorHeightMap, wallHeightMap;
	
	private Vec2 minimum, maximum;
	private Vec2 pathStart;
	
	public TerrainMap(Maze maze) {
		
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
	
	public MazeAreaType getAreaType(int x, int z) {
		return shapeMap[x-getMinX()][z-getMinZ()];
	}
	
	public MazeAreaType getAreaType(Vec2 point) {
		return getAreaType(point.getX(), point.getZ());
	}

	public int getFloorHeight(Vec2 point) {
		return getFloorHeight(point.getX(), point.getZ());
	}
	
	public int getFloorHeight(int x, int z) {
		return floorHeightMap[x-getMinX()][z-getMinZ()];
	}
	
	public int getWallHeight(Vec2 point) {
		return getWallHeight(point.getX(), point.getZ());
		
	}
	public int getWallHeight(int x, int z) {
		return wallHeightMap[x-getMinX()][z-getMinZ()];
	}
	
	public int getCeilHeight(int x, int z) {
		return getFloorHeight(x, z) + getWallHeight(x, z);
	}
	
	public int getCeilHeight(Vec2 point) {
		return getFloorHeight(point) + getWallHeight(point);
	}
	
	public Vec2 getStart() {
		return pathStart;
	}
	
	public void setType(Vec2 point, MazeAreaType type) {
		setType(point.getX(), point.getZ(), type);
	}
	
	public void setType(int x, int z, MazeAreaType type) {
		shapeMap[x-getMinX()][z-getMinZ()] = type;
	}

	public void setFloorHeight(Vec2 point, int newY) {
		setFloorHeight(point.getX(), point.getZ(), newY);
	}

	public void setFloorHeight(int x, int z, int newY) {
		floorHeightMap[x-getMinX()][z-getMinZ()] = newY;
	}
	
	public void setWallHeight(Vec2 point, int newHeight) {
		setWallHeight(point.getX(), point.getZ(), newHeight);
	}
	
	public void setWallHeight(int x, int z, int newHeight) {
		wallHeightMap[x-getMinX()][z-getMinZ()] = newHeight;
	}
	
	public void setPathStart(Vec2 pathStart) {
		this.pathStart = pathStart;
	}
	
	public void mapSegment(PathSegment segment, MazeAreaType type) {
		
		for(Vec2 point : segment.getFill()) {
			
			if(contains(point))
				setType(point.getX(), point.getZ(), type);
		}
	}
	
	public void flipMap() {
		
		for(int x = getMinX(); x < getMaxX(); x++) {
			for(int z = getMinZ(); z < getMaxZ(); z++) {
				
				MazeAreaType fillType = getAreaType(x, z);
				
				if(fillType == MazeAreaType.UNDEFINED)
					setType(x, z, MazeAreaType.WALL);
					
				else if(fillType == MazeAreaType.EXIT)
					setType(x, z, MazeAreaType.PATH);
			}
		}
	}
	
	private void calculateMapSize() {
		
		minimum = getMinLoc();
		maximum = getMaxLoc();
		
		shapeMap = new MazeAreaType
			[maximum.getX() - minimum.getX()]
			[maximum.getZ() - minimum.getZ()];
		
		floorHeightMap = new int
			[maximum.getX() - minimum.getX()]
			[maximum.getZ() - minimum.getZ()];
		
		wallHeightMap = new int
			[maximum.getX() - minimum.getX()]
			[maximum.getZ() - minimum.getZ()];
	}
	
	private void copyMazeOntoMap() {
		
		for(int x = getMinX(); x < getMaxX(); x++) {
			for(int z = getMinZ(); z < getMaxZ(); z++) {
				setType(x, z, MazeAreaType.NOT_MAZE);
			}
		}
		
		int wallHeight = maze.getWallHeight();
		Clip clip = maze.getClip();
		
		//mark the maze's area in mazeMap as undefined area (open to become paths and walls)
		for(Entry<Vec2, Integer> loc : clip.getFillSet()) {
			
			setType(loc.getKey(), MazeAreaType.UNDEFINED);
			setFloorHeight(loc.getKey(), loc.getValue());
			setWallHeight(loc.getKey(), wallHeight);
		}
		
		//mark the border in mazeMap as walls
		for(Vec2 loc : maze.getClip().getBorder())
			setType(loc, MazeAreaType.WALL);
	}
	
	private Vec2 getMinLoc() {
		
		Vec2 minimum = null;

		for(Vec2 loc : maze.getClip().getFill()) {
			
			if(minimum == null) {
				
				minimum = loc.clone();
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
		
		for(Vec2 loc : maze.getClip().getFill()) {
			
			if(maximum == null) {
				maximum = loc.clone();
				continue;
			}
			
			if(loc.getX() > maximum.getX())
				maximum.setX(loc.getX());
				
			if(loc.getZ() > maximum.getZ())
				maximum.setZ(loc.getZ());
		}
		
		return maximum.add(1, 1);
	}
}