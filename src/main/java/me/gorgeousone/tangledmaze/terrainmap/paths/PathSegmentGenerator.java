package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;

public final class PathSegmentGenerator {
	
	private PathSegmentGenerator() {}
	
	public static void mapEntrance(TerrainMap terrainMap) {
		
		Maze maze = terrainMap.getMaze();
		Vec2 entrancePoint = maze.getEntrance();
		
		NewPathSegment entrance = createEntranceSegment(
				entrancePoint,
				getExitFacing(entrancePoint, terrainMap),
				maze.getDimension(MazeDimension.PATH_WIDTH),
				maze.getDimension(MazeDimension.WALL_WIDTH));
		
		//TODO reactivate new entrance mapping
		//terrainMap.mapSegment(entrance, MazeAreaType.PATH);
	}
	
	private static Direction getExitFacing(Vec2 exit, TerrainMap terrainMap) {
		
		for (Direction dir : Direction.fourCardinals()) {
			Vec2 neighbor = exit.clone().add(dir.getVec2());
			
			if (!terrainMap.contains(neighbor))
				continue;
			
			if (terrainMap.getAreaType(neighbor) == MazeAreaType.UNDEFINED)
				return dir;
		}
		
		throw new IllegalArgumentException("This exit does not seem to touch the maze.");
	}
	
	private static NewPathSegment createEntranceSegment(
			Vec2 entrancePoint,
			Direction facing,
			int pathWidth,
			int wallWidth) {
		
		Vec2 segmentMin = entrancePoint.clone();
		Vec2 segmentSize;
		
		if (facing.isXAligned()) {
			
			segmentSize = new Vec2(wallWidth + pathWidth, pathWidth);
			
			if (!facing.isPositive())
				segmentMin.add(-segmentSize.getX() + 1, -segmentSize.getZ() + 1);
			
		} else {
			
			segmentSize = new Vec2(pathWidth, wallWidth + pathWidth);
			
			if (facing.isPositive())
				segmentMin.add(-segmentSize.getX() + 1, 0);
			else
				segmentMin.add(0, -segmentSize.getZ() + 1);
		}
		
		return new NewPathSegment(segmentMin, segmentSize);
	}
}
