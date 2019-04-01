package me.gorgeousone.tangledmaze.generation;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Vec2;

public class ExitGenerator {
	
	public void generateExits(BuildMap map) {
		
		Maze maze = map.getMaze();
		
		int pathWidth = maze.getPathWidth(),
			wallWidth = maze.getWallWidth();
		
		PathSegment entrance = generateEntrance(
				map,
				maze.getMainExit().clone(),
				pathWidth,
				wallWidth);
		
		Vec2 pathStart = entrance.getEnd();
		
		map.setStart(pathStart);
		map.mapSegment(entrance, MazeFillType.PATH);
		
		if(maze.getExits().size() == 1)
			return;

		int pathGridOffsetX = pathStart.getIntX() % (pathWidth + wallWidth),
			pathGridOffsetZ = pathStart.getIntZ() % (pathWidth + wallWidth);
		
		for(int i = 0; i < maze.getExits().size() - 1; i++) {
			
			generateExit(
					map,
					maze.getExits().get(i).clone(),
					pathWidth,
					wallWidth,
					pathGridOffsetX,
					pathGridOffsetZ);
		}
	}
	
	protected PathSegment generateEntrance(
			BuildMap map,
			MazePoint entrance,
			int pathWidth,
			int wallWidth) {
		
		entrance.subtract(map.getMinX(), 0, map.getMinZ());
		
		PathSegment entranceSegment = new PathSegment(
			new Vec2(entrance.toVector()),
			getExitFacing(entrance, map).toVec2(),
			wallWidth + pathWidth,
			pathWidth,
			true);
		
		return entranceSegment;
	}
	
	protected void generateExit(
			BuildMap map,
			MazePoint exit,
			int pathWidth,
			int wallWidth,
			int pathGridOffsetX,
			int pathGridOffsetZ) {
		
		MazePoint relExit = exit.clone().add(-map.getMinX(), 0, -map.getMinZ());
//		relExit.subtract(map.getMinX(), 0, map.getMinZ());

		Vec2 exitFacing = getExitFacing(relExit, map).toVec2();
		
		PathSegment exitSegment = new PathSegment(
				new Vec2(relExit.toVector()),
				exitFacing,
				pathWidth,
				pathWidth,
				true);
			
		//calculate how long the exit has to be to reach the grid of paths
		int exitOffset;

		//start with getting the exit's position relative to the path grid
		if(exitFacing.getIntX() != 0)
			exitOffset = exitSegment.getStart().getIntX() - pathGridOffsetX;
		else
			exitOffset = exitSegment.getStart().getIntZ() - pathGridOffsetZ;
		
		//reduce the relative position to the actual possible offset
		if(Math.abs(exitOffset) > pathWidth + wallWidth)
			exitOffset %= pathWidth + wallWidth;
		
		//invert offset if it is calculated to opposing path in the grid
		if(exitFacing.getIntX() == 1 || exitFacing.getIntZ() == 1)
			exitOffset = (pathWidth + wallWidth) - exitOffset;
		
		Bukkit.broadcastMessage("exit offset maybe negative: " + exitOffset);
		//increase offset if it's under possible minimum of 1 block
		if(exitOffset < 1)
			exitOffset += pathWidth + wallWidth;
		
		exitSegment.expand(exitOffset);
		map.mapSegment(exitSegment, MazeFillType.EXIT);
	}
		
	public static Directions getExitFacing(Location exit, BuildMap map) {
		
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