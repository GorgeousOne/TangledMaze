package me.gorgeousone.tangledmaze.generation;

import org.bukkit.Location;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Vec2;

public final class ExitGenerator {
	
	private ExitGenerator() {}
	
	public static void generateExits(BuildMap map) {
		
		Maze maze = map.getMaze();
		
		int pathWidth = maze.getPathWidth(),
			wallWidth = maze.getWallWidth();
		
		PathSegment mainExit = generateMainExit(
				map,
				maze.getMainExit().clone(),
				pathWidth,
				wallWidth);
		
		Vec2 pathStart = mainExit.getEnd();
		map.setStart(pathStart);
		map.drawSegment(mainExit, MazeFillType.PATH);
		
		int pathGridOffsetX = pathStart.getIntX() % (pathWidth + wallWidth),
			pathGridOffsetZ = pathStart.getIntZ() % (pathWidth + wallWidth);
		
		if(maze.getExits().size() < 2) {
			return;
		}
		
		for(int i = 0; i < maze.getExits().size() - 2; i++) {
			
			generateExit(
					map,
					maze.getExits().get(i).clone(),
					pathWidth,
					wallWidth,
					pathGridOffsetX,
					pathGridOffsetZ);
		}
	}
	
	private static PathSegment generateMainExit(
			BuildMap map,
			MazePoint mainExit,
			int pathWidth,
			int wallWidth) {
		
		mainExit.subtract(map.getMinX(), 0, map.getMinZ());
		
		PathSegment exit = new PathSegment(
			new Vec2(mainExit.toVector()),
			getExitsFacing(mainExit, map).toVec2(),
			2*pathWidth,
			pathWidth,
			true);
		
		return exit;
	}
	
	private static void generateExit(
			BuildMap map,
			MazePoint exit,
			int pathWidth,
			int wallWidth,
			int pathGridOffsetX,
			int pathGridOffsetZ) {
		
		exit.subtract(map.getMinX(), 0, map.getMinZ());
		Vec2 facing = getExitsFacing(exit, map).toVec2();
		
		PathSegment exitSegment = new PathSegment(
				new Vec2(exit.toVector()),
				facing,
				pathWidth,
				pathWidth,
				true);
			
		//TODO normal - check if maze exits are placed correctly, maybe there is a bug
		
		//calculate calculate how long the exit has to be to reach the path grid
		int exitOffset;

		//start with getting the exits position relative to the path grid
		if(facing.getIntX() != 0) {
			exitOffset = exitSegment.getStart().getIntX() - pathGridOffsetX;
		}else {
			exitOffset = exitSegment.getStart().getIntZ() - pathGridOffsetZ;
		}
		
		//reduce the relative position to the actual possible offset
		if(Math.abs(exitOffset) > pathWidth + wallWidth) {
			exitOffset %= pathWidth + wallWidth;
		}
		
		//invert offset if the exit's facing is not along with the grid's facing (simply -x or -z)
		if(facing.getIntX() == 1 || facing.getIntZ() == 1) {
			exitOffset = (pathWidth + wallWidth) - exitOffset;
		}
		
		//get rid off negative values
		if(exitOffset <= 0) {
			exitOffset += pathWidth + wallWidth;
		}
		
		exitSegment.expand(exitOffset);
		map.drawSegment(exitSegment, MazeFillType.EXIT);
	}
		
	private static Directions getExitsFacing(Location exit, BuildMap map) {
		
		for(Directions dir : Directions.cardinalValues()) {
			
			Vec2 nextToExit = new Vec2(exit.toVector());
			nextToExit.add(dir.toVec2());
			
			if(nextToExit.getIntX() < 0 || nextToExit.getIntX() >= map.getDimX() ||
			   nextToExit.getIntZ() < 0 || nextToExit.getIntZ() >= map.getDimZ())
				continue;
			
			if(map.getType(nextToExit) == MazeFillType.UNDEFINED)
				return dir;
		}
		
		return null;
	}
}