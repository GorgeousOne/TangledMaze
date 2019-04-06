package me.gorgeousone.tangledmaze.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Vec2;

public class PathGenerator {
	
	public ArrayList<Directions> shuffledCardinals;
	public Random rnd;
	
	public PathGenerator() {
		
		shuffledCardinals = new ArrayList<>(Arrays.asList(Directions.cardinalValues()));
		rnd = new Random();
	}
	
	public void generatePaths(BuildMap buildMap) {
		
		generateExitSegments(buildMap);
		generatePathSegments(buildMap);
	}
	
	public void generateExitSegments(BuildMap buildMap) {
	
		Maze maze = buildMap.getMaze();
		
		int pathWidth = maze.getPathWidth(),
			wallWidth = maze.getWallWidth();
		
		PathSegment entrance = createEntranceSegment(
				maze.getMainExit().clone(),
				buildMap,
				pathWidth,
				wallWidth);
		
		Vec2 pathStart = entrance.getEnd();
		
		buildMap.setStart(pathStart);
		buildMap.mapSegment(entrance, MazeFillType.PATH);
		
		if(maze.getExits().size() == 1)
			return;

		int pathGridOffsetX = pathStart.getIntX() % (pathWidth + wallWidth),
			pathGridOffsetZ = pathStart.getIntZ() % (pathWidth + wallWidth);
		
		for(int i = 0; i < maze.getExits().size() - 1; i++) {
			
			createExitSegment(
					maze.getExits().get(i).clone(),
					buildMap,
					pathWidth,
					wallWidth,
					pathGridOffsetX,
					pathGridOffsetZ);
		}
	}
	
	public void generatePathSegments(BuildMap buildMap) {
		
		Maze maze = buildMap.getMaze();
		
		ArrayList<Vec2> openEnds = new ArrayList<>();
		openEnds.add(buildMap.getStart());
		
		int	pathWidth  = maze.getPathWidth(),
			wallWidth  = maze.getWallWidth(),
			linkedPathsLength = 0;
					
		Vec2 currentEnd;

		while(!openEnds.isEmpty()) {
			
			if(linkedPathsLength < 3) {
				currentEnd = openEnds.get(openEnds.size()-1);
			
			}else {
				currentEnd = openEnds.get(rnd.nextInt(openEnds.size()));
				linkedPathsLength = 0;
			}
			
			PathSegment path = createPathSegment(buildMap, currentEnd, pathWidth, wallWidth);
			
			if(path == null) {
				
				openEnds.remove(currentEnd);
				linkedPathsLength = 0;
			
			}else {
				
				buildMap.mapSegment(path, MazeFillType.PATH);
				openEnds.add(path.getEnd());
				linkedPathsLength++;
			}
		}
	}

	public PathSegment createEntranceSegment(
			MazePoint entrance,
			BuildMap buildMap,
			int pathWidth,
			int wallWidth) {
		
		MazePoint relEntrance = entrance.clone();
		relEntrance.subtract(buildMap.getMinX(), 0, buildMap.getMinZ());
		
		PathSegment entranceSegment = new PathSegment(
			new Vec2(relEntrance),
			wallWidth + pathWidth,
			pathWidth,
			getExitFacing(relEntrance, buildMap),
			true);
		
		return entranceSegment;
	}
	
	public PathSegment createExitSegment(
			MazePoint exit,
			BuildMap buildMap,
			int pathGridOffsetX,
			int pathGridOffsetZ,
			int pathWidth,
			int wallWidth) {

		MazePoint relExit = exit.clone().add(-buildMap.getMinX(), 0, -buildMap.getMinZ());
		Directions exitFacing = getExitFacing(relExit, buildMap);
		
		PathSegment exitSegment = new PathSegment(
				new Vec2(relExit),
				pathWidth,
				pathWidth,
				exitFacing,
				true);
		
		exitSegment.expand(exitFacing.isXAligned() ?
				getExitDistanceToPathGrid(exitSegment.getStart().getIntX(), exitFacing, pathGridOffsetX, pathWidth, wallWidth) :
				getExitDistanceToPathGrid(exitSegment.getStart().getIntZ(), exitFacing, pathGridOffsetZ, pathWidth, wallWidth));
		
		return exitSegment;
	}
	
	public PathSegment createPathSegment(
			BuildMap map,
			Vec2 currentEnd,
			int pathWidth,
			int wallWidth) {
		
		Collections.shuffle(shuffledCardinals);
		
		for(Directions dir : shuffledCardinals) {

			Vec2 facing = dir.toVec2();
			Vec2 start  = new Vec2(currentEnd.getIntX() + facing.getIntX() * pathWidth,
								  currentEnd.getIntZ() + facing.getIntZ() * pathWidth);
		
			PathSegment path = new PathSegment(
					start,
					pathWidth + wallWidth,
					pathWidth,
					dir,
					false);
				
			if(segmentIsFree(map, path)) {
				return path;
			}
		}
		
		return null;
	}
	
	public static Directions getExitFacing(MazePoint relExit, BuildMap buildMap) {
		
		for(Directions dir : Directions.cardinalValues()) {
			
			Vec2 neighbor = new Vec2(relExit).add(dir.toVec2());
			
			if(!buildMap.contains(neighbor)) {
				System.out.println("DEBUG: " + dir + " out of maze");
				continue;
			}
			
			//check if location next to exit is inside of the maze
			if(buildMap.getType(neighbor) == MazeFillType.UNDEFINED)
				return dir;
			else
				System.out.println("DEBUG: " + dir + buildMap.getType(neighbor));
		}
		
		throw new IllegalStateException("The parsed Location cannot be an exit of this maze.");
	}
	
	public int getExitDistanceToPathGrid(
			int exitSegmentStart,
			Directions exitFacing,
			int pathGridOffset,
			int pathWidth,
			int wallWidth) {
		
		//calculate how long the exit has to be to reach the grid of paths
		//start with getting the exit's position relative to the path grid
		int exitOffset = exitSegmentStart - pathGridOffset;
		
		//reduce the relative position to the actual possible offset
		if(Math.abs(exitOffset) > pathWidth + wallWidth)
			exitOffset %= pathWidth + wallWidth;
		
		//invert offset if it is calculated to opposing path in the grid
		if(exitFacing.getSign() == 1)
			exitOffset = (pathWidth + wallWidth) - exitOffset;
		
		//increase offset if it's under possible minimum of 1 block
		if(exitOffset < 1)
			exitOffset += pathWidth + wallWidth;
		
		return exitOffset;
	}
	
	public boolean segmentIsFree(BuildMap buildMap, PathSegment segment) {
		
		for(Vec2 point : segment.getFill()) {
			
			if(!buildMap.contains(point))
				return false;
			
			if(buildMap.getType(point) != MazeFillType.UNDEFINED &&
			   buildMap.getType(point) != MazeFillType.EXIT) {
				return false;
			}
		}
		
		return true;
	}
}