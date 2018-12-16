package me.gorgeousone.tangledmaze.mazes.generation;

import org.bukkit.Location;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.Vec2;
public class ExitGenerator {
	
	private static int
		pathGridOffsetX = 0,
		pathGridOffsetZ = 0;
	
	public static void generateExits(BuildMap map) {
		
		Maze maze = map.getMaze();
		
		pathGridOffsetX = 0;
		pathGridOffsetZ = 0;
		
		Location mainExit = maze.getMainExit().clone();
		mainExit.subtract(map.getMinX(), 0, map.getMinZ());
		
		generateMainExit(maze.getMainExit(), map, maze.getPathWidth(), maze.getWallWidth());
		
		for(int i = 0; i < maze.getExits().size() - 1; i++) {
			Location exit = maze.getExits().get(i).clone();
			exit.subtract(map.getMinX(), 0, map.getMinZ());
			
			generateExit(maze.getExits().get(i), map, maze.getPathWidth(), maze.getWallWidth());
		}
	}
	
//	private static void mark(MazeSegment segment, MazeMap map) {
//		
//		World world = Bukkit.getWorld("world");
//		
//		Location
//			start = segment.getStart().add(map.getMinX(), map.getMinZ()).toVec3().setY(16).toLocation(world),
//			end   = segment.getEnd().add(map.getMinX(), map.getMinZ()).toVec3().setY(16).toLocation(world);
//		
//		start.getBlock().setType(Material.GOLD_BLOCK);
//		end.getBlock().setType(Material.LAPIS_BLOCK);
//	}
	
	private static void generateMainExit(Location mainExit, BuildMap map, int pathWidth, int wallWidth) {
		
		mainExit.subtract(map.getMinX(), 0, map.getMinZ());
		
		PathSegment exit = new PathSegment(
			new Vec2(mainExit.toVector()),
			getExitsFacing(mainExit, map),
			2*pathWidth,
			pathWidth,
			true);
		
		Vec2 pathStart = exit.getEnd();
		map.setStart(pathStart);
		
		pathGridOffsetX = pathStart.getIntX() % (pathWidth + wallWidth);
		pathGridOffsetZ = pathStart.getIntZ() % (pathWidth + wallWidth);
		
		//make the main exit out of path so the path generator won't connect any path to it anymore
		drawSegmentOnMap(exit, map, MazeFillType.PATH);
	}
	
	private static void generateExit(Location exit, BuildMap map, int pathWidth, int wallWidth) {
		
		exit.subtract(map.getMinX(), 0, map.getMinZ());
		Vec2 facing = getExitsFacing(exit, map);
		
		PathSegment exitSegment = new PathSegment(
				new Vec2(exit.toVector()),
				facing,
				pathWidth,
				pathWidth,
				true);
			
		int exitOffset;
		
		//calculate the extra length for the exit in relation to the grid / the grids's offset
		if(facing.getIntX() != 0)
			exitOffset = exitSegment.getStart().getIntX() - pathGridOffsetX;
		else
			exitOffset = exitSegment.getStart().getIntZ() - pathGridOffsetZ;
		
		//get rid off negative values...
		if(exitOffset <= 0)
			exitOffset += pathWidth + wallWidth;
		
		//... and offsets bigger than one path+wall segment
		else if(exitOffset > pathWidth + wallWidth)
			exitOffset %= pathWidth + wallWidth;
		
		//reverse offset if facing is not pointing to the offsets of the grid
		if(facing.getIntX() == 1 || facing.getIntZ() == 1) {
			exitOffset = (pathWidth + wallWidth) - exitOffset;
			
			if(exitOffset <= 0)
				exitOffset += pathWidth + wallWidth;
		}
		
		exitSegment.expand(exitOffset);
		drawSegmentOnMap(exitSegment, map, MazeFillType.EXIT);
	}
		
	private static Vec2 getExitsFacing(Location exit, BuildMap map) {
		
		for(Directions dir : Directions.cardinalValues()) {
			
			Vec2 nextToExit = new Vec2(exit.toVector());
			nextToExit.add(dir.facing());
			
			if(nextToExit.getIntX() < 0 || nextToExit.getIntX() >= map.getDimX() ||
			   nextToExit.getIntZ() < 0 || nextToExit.getIntZ() >= map.getDimZ())
				continue;
			
			if(map.getType(nextToExit) == MazeFillType.UNDEFINED)
				return dir.facing();
		}
		
		return null;
	}
	
	private static void drawSegmentOnMap(PathSegment segment, BuildMap map, MazeFillType type) {
		
		for(Vec2 point : segment.getFill()) {
			if(point.getIntX() >= 0 && point.getIntX() < map.getDimX() &&
			   point.getIntZ() >= 0 && point.getIntZ() < map.getDimZ())
				map.setType(point, type);
		}
	}
}