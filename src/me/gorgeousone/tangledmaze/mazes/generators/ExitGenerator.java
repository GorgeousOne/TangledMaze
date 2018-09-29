package me.gorgeousone.tangledmaze.mazes.generators;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.utils.Utils;
import me.gorgeousone.tangledmaze.utils.Vec2;

public class ExitGenerator {
	
	private static int
		pathGridOffsetX = 0,
		pathGridOffsetZ = 0;
	
	public static void generateExits(MazeMap map) {
		
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
	
	private static void generateMainExit(Location mainExit, MazeMap map, int pathWidth, int wallWidth) {
		
		mainExit.subtract(map.getMinX(), 0, map.getMinZ());
		
		MazeSegment exitSegment = new MazeSegment(
			new Vec2(mainExit.toVector()),
			new Vec2(getExitsFacing(mainExit, map)),
			2*pathWidth,
			pathWidth,
			true);
		
		Vec2 pathStart = exitSegment.getEnd();
		map.setStart(pathStart);
		
		pathGridOffsetX = pathStart.getX() % (pathWidth + wallWidth);
		pathGridOffsetZ = pathStart.getZ() % (pathWidth + wallWidth);
		
		//make the main exit out of path so the path generator won't connect any path to it anymore
		drawSegmentOnMap(exitSegment, map, MazeSegment.PATH);
	}
	
	private static void generateExit(Location exit, MazeMap map, int pathWidth, int wallWidth) {
		
		exit.subtract(map.getMinX(), 0, map.getMinZ());
		Vec2 facing = new Vec2(getExitsFacing(exit, map));
		
		MazeSegment exitSegment = new MazeSegment(
				new Vec2(exit.toVector()),
				facing,
				pathWidth,
				pathWidth,
				true);
			
		int exitOffset;
		
		//calculate the extra length for the exit in relation to the grid / the grids's offset
		if(facing.getX() != 0)
			exitOffset = exitSegment.getStart().getX() - pathGridOffsetX;
		else
			exitOffset = exitSegment.getStart().getZ() - pathGridOffsetZ;
		
		//get rid off negative values...
		if(exitOffset <= 0)
			exitOffset += pathWidth + wallWidth;
		
		//... and offsets bigger than one path+wall segment
		else if(exitOffset > pathWidth + wallWidth)
			exitOffset %= pathWidth + wallWidth;
		
		//reverse offset if facing is not pointing to the offsets of the grid
		if(facing.getX() == 1 || facing.getZ() == 1) {
			exitOffset = (pathWidth + wallWidth) - exitOffset;
			
			if(exitOffset <= 0)
				exitOffset += pathWidth + wallWidth;
		}
		
		exitSegment.expand(exitOffset);
		drawSegmentOnMap(exitSegment, map, MazeSegment.EXIT);
	}
		
	private static Vector getExitsFacing(Location exit, MazeMap map) {
		
		for(Vector dir : Utils.CARDINAL_DIRS) {
			
			Vec2 nextToExit = new Vec2(exit.toVector());
			nextToExit.add(new Vec2(dir));
			
			if(nextToExit.getX() < 0 || nextToExit.getX() >= map.getDimX() ||
			   nextToExit.getZ() < 0 || nextToExit.getZ() >= map.getDimZ())
				continue;
			
			if(map.getType(nextToExit) == MazeSegment.UNDEFINED)
				return dir.clone();
		}
		
		return null;
	}
	
	private static void drawSegmentOnMap(MazeSegment segment, MazeMap map, int segmentType) {
		
		for(Vec2 point : segment.getFill()) {
			if(point.getX() >= 0 && point.getX() < map.getDimX() &&
			   point.getZ() >= 0 && point.getZ() < map.getDimZ())
				map.setType(point, segmentType);
		}
	}
}