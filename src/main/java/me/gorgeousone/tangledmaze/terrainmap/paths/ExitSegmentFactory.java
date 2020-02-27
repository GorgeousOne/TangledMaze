package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;

public final class ExitSegmentFactory {
	
	private ExitSegmentFactory() {}
	
	public static ExitSegment createEntranceSegment(TerrainMap terrainMap, Vec2 entrancePoint, int pathWidth, int wallWidth) {
		
		Direction facing = getExitFacing(entrancePoint, terrainMap);
		Vec2 exitStart = calculateExitStart(entrancePoint, facing, pathWidth);
		
		ExitSegment entrance = new ExitSegment(exitStart, facing, pathWidth);
		entrance.expandLength(wallWidth);
		return entrance;
	}
	
	public static ExitSegment createExitSegment(
			Vec2 exitPoint,
			TerrainMap terrainMap,
			Vec2 pathGridOffset,
			int pathWidth,
			int pathGridMeshSize) {
		
		Direction facing = getExitFacing(exitPoint, terrainMap);
		Vec2 exitStart = calculateExitStart(exitPoint, facing, pathWidth);
		
		ExitSegment exit = new ExitSegment(exitStart, facing, pathWidth);
		exit.expandLength(facing.isXAligned() ?
				                   getExitOffsetToPathGrid(exit.getStartPoint().getX(), facing, pathGridOffset.getX(), pathGridMeshSize) :
				                   getExitOffsetToPathGrid(exit.getStartPoint().getZ(), facing, pathGridOffset.getZ(), pathGridMeshSize));
		return exit;
	}
	
	private static Direction getExitFacing(Vec2 exit, TerrainMap terrainMap) {
		
		for (Direction dir : Direction.fourCardinals()) {
			
			Vec2 neighbor = exit.clone().add(dir.getVec2());
			
			if (terrainMap.getAreaType(neighbor) == MazeAreaType.UNDEFINED)
				return dir;
		}
		
		throw new IllegalArgumentException("This exit does not seem to touch the maze.");
	}
	
	private static Vec2 calculateExitStart(Vec2 exitPoint, Direction facing, int exitWidth) {
		
		Vec2 exitStart = exitPoint.clone();
		
		if (!facing.isXAligned()) {
			
			if (facing.isPositive())
				exitStart.add(-exitWidth + 1, 0);
			else
				exitStart.add(0, -exitWidth + 1);
			
		} else if (!facing.isPositive())
			exitStart.add(-exitWidth + 1, -exitWidth + 1);
		
		return exitStart;
	}
	
	private static int getExitOffsetToPathGrid(
			int exitStart,
			Direction exitFacing,
			int pathGridOffset,
			int pathGridMeshSize) {
		
		//start with getting the exit's position relative to the path grid
		int exitOffset = exitStart - pathGridOffset;
		
		//reduce the relative position to actual possible offset (0 to mesh size)
		exitOffset %= pathGridMeshSize;
		
		//invert the offset if the path is facing the next grid cell, not the last one
		if (exitFacing.isPositive())
			exitOffset = (int) Math.signum(exitOffset) * pathGridMeshSize - exitOffset;
		
		//increase offset if it's lower than possible minimum of 1 block
		if (exitOffset < 1)
			exitOffset += pathGridMeshSize;
		
		return exitOffset;
	}
}
