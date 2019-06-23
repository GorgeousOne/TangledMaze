package me.gorgeousone.tangledmaze.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.generation.path.PathSegment;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Vec2;

public class PathGenerator {
	
	private ArrayList<Directions> shuffledCardinalDirs;
	private Random rnd;
	
	public PathGenerator() {
		
		shuffledCardinalDirs = new ArrayList<>(Arrays.asList(Directions.cardinalValues()));
		rnd = new Random();
	}
	
	public void generatePaths(BuildMap buildMap) {
		
		generateExits(buildMap);
		generatePathMap(buildMap);
		buildMap.flip();
	}
	
	private void generateExits(BuildMap buildMap) {
	
		Maze maze = buildMap.getMaze();
		
		int pathWidth = maze.getPathWidth(),
			wallWidth = maze.getWallWidth();
		
		PathSegment entrance = createEntranceSegment(
				maze.getMainExit(),
				getExitFacing(maze.getMainExit(), buildMap),
				pathWidth,
				wallWidth);
		
		Vec2 pathStart = entrance.getEnd();
		
		buildMap.setStart(pathStart);
		buildMap.mapSegment(entrance, MazeFillType.PATH);
		
		if(maze.getExits().size() < 2)
			return;

		int pathGridOffsetX = pathStart.getX() % (pathWidth + wallWidth),
			pathGridOffsetZ = pathStart.getZ() % (pathWidth + wallWidth);
		
		for(Vec2 exit : maze.getExits()) {
			
			if(exit.equals(maze.getMainExit()))
				continue;
			
			createExitSegment(
					exit,
					buildMap,
					pathGridOffsetX,
					pathGridOffsetZ,
					pathWidth,
					wallWidth);
		}
	}
	
	private PathSegment createEntranceSegment(
			Vec2 entrance,
			Directions facing,
			int pathWidth,
			int wallWidth) {
		
		PathSegment entranceSegment = new PathSegment(
			entrance,
			wallWidth + pathWidth,
			pathWidth,
			facing,
			true);
		
		return entranceSegment;
	}
	
	private PathSegment createExitSegment(
			Vec2 exit,
			BuildMap buildMap,
			int pathGridOffsetX,
			int pathGridOffsetZ,
			int pathWidth,
			int wallWidth) {
		
		Directions facing = getExitFacing(exit, buildMap);
		
		PathSegment exitSegment = new PathSegment(
				exit,
				pathWidth,
				pathWidth,
				facing,
				true);
		
		exitSegment.expand(facing.isXAligned() ?
				getExitOffsetToPathGrid(exitSegment.getStart().getX(), facing, pathGridOffsetX, pathWidth, wallWidth) :
				getExitOffsetToPathGrid(exitSegment.getStart().getZ(), facing, pathGridOffsetZ, pathWidth, wallWidth));
		
		
//		if(!segmentIsFree(buildMap, exitSegment))
//			return null;

//		buildMap.mapSegment(exitSegment, MazeFillType.EXIT);
//		
//		Vec2 verticalOffset = facing.isXAligned() ?
//				getVerticalOffsetToPathGrid(exitSegment.getEnd().getZ(), facing, pathGridOffsetZ, pathWidth, wallWidth) :
//				getVerticalOffsetToPathGrid(exitSegment.getEnd().getX(), facing, pathGridOffsetX, pathWidth, wallWidth);
//		
//		System.out.println(facing + " " + facing.toVec2().toString());
//		System.out.println(Directions.cardinalValueOf(verticalOffset) + " " + verticalOffset.toString());
//		
//		if(!verticalOffset.isZero()) {
//			
//			exitSegment = new PathSegment(
//					exitSegment.getEnd(),
//					pathWidth,
//					pathWidth,
//					Directions.cardinalValueOf(verticalOffset),
//					false);
//			
//			exitSegment.expand(verticalOffset.length());
//			
//			if(!segmentIsFree(buildMap, exitSegment))
//				return null;
//			
//		}
		
		buildMap.mapSegment(exitSegment, MazeFillType.EXIT);
		return exitSegment;
	}
	
	//calculate how long the exit has to be to reach the grid of paths
	private int getExitOffsetToPathGrid(
			int exitSegmentStart,
			Directions exitFacing,
			int pathGridOffset,
			int pathWidth,
			int wallWidth) {
		
		//start with getting the exit's position relative to the path grid
		int exitOffset = exitSegmentStart - pathGridOffset;
		
		//reduce the relative position to the actual possible offset
		exitOffset %= pathWidth + wallWidth;
		
		//invert offset if it is calculated to opposing path in the grid
		if(exitFacing.isPositive())
			exitOffset = (int) Math.signum(exitOffset) * (pathWidth + wallWidth) - exitOffset;
		
		//increase offset if it's under possible minimum of 1 block
		if(exitOffset < 1)
			exitOffset += pathWidth + wallWidth;
		
		return exitOffset;
	}
	
	//calculate how long the exit has to be to reach the grid of paths
//	private Vec2 getVerticalOffsetToPathGrid(
//			int exitSegmentStart,
//			Directions exitFacing,
//			int pathGridOffset,
//			int pathWidth,
//			int wallWidth) {
//		
//		//start with getting the exit's position relative to the path grid
//		int exitOffset = exitSegmentStart - pathGridOffset;
//		
//		//reduce the relative position to the actual possible offset
//		exitOffset %= pathWidth + wallWidth;
//
//		//invert offset if it is calculated to opposing path in the grid
//		if(exitFacing.isPositive())
//			exitOffset = (pathWidth + wallWidth) - exitOffset;
//		
//		//increase offset if it's under possible minimum of 1 block
//		if(exitOffset < 1)
//			exitOffset += pathWidth + wallWidth;
//
//		return exitFacing.toVec2().mult(exitOffset);
//	}
	
	private void generatePathMap(BuildMap buildMap) {
		
		Maze maze = buildMap.getMaze();
		
		ArrayList<Vec2> pathEnds = new ArrayList<>();
		pathEnds.add(buildMap.getStart());
		
		int wallWidth  = maze.getWallWidth();
		int	pathWidth  = maze.getPathWidth();
		int pathLength = maze.getPathLength();
		
		int maxLinkedPathsCount = 3;
		int linkedPathsCount = 0;
		
		Vec2 currentPathEnd;
		
		boolean lastSegmentWasExpanded = false;
		
		while(!pathEnds.isEmpty()) {
			
			if(linkedPathsCount < maxLinkedPathsCount) {
				currentPathEnd = pathEnds.get(pathEnds.size()-1);
			
			}else {
				currentPathEnd = pathEnds.get(rnd.nextInt(pathEnds.size()));
				linkedPathsCount = 0;
			}
			
			Collections.shuffle(shuffledCardinalDirs);
			PathSegment newPath = createPathSegment(buildMap, currentPathEnd, wallWidth, pathWidth, pathLength);

			if(newPath == null) {
			
				pathEnds.remove(currentPathEnd);
				linkedPathsCount = 0;
				continue;
			
			//if this cardinal direction is the last one in shuffledCardinals the path end cannot have further junctions
			}else if(shuffledCardinalDirs.indexOf(newPath.getFacing()) == 3)
				pathEnds.remove(currentPathEnd);

			if(pathLength > 1 && !lastSegmentWasExpanded)
				lastSegmentWasExpanded = tryExpandSegment(buildMap, newPath, wallWidth, pathWidth, rnd.nextInt(pathLength));
			else
				lastSegmentWasExpanded = false;

			buildMap.mapSegment(newPath, MazeFillType.PATH);
			
			pathEnds.add(newPath.getEnd());
			linkedPathsCount++;
		}
	}

	private PathSegment createPathSegment(
			BuildMap buildMap,
			Vec2 lastPathEnd,
			int wallWidth,
			int pathWidth,
			int pathLength) {
		
		Collections.shuffle(shuffledCardinalDirs);
		
		PathSegment newPath = null;
		
		for(Directions dir : shuffledCardinalDirs) {

			Vec2 facing = dir.toVec2();
			Vec2 start  = new Vec2(
					lastPathEnd.getX() + facing.getX() * pathWidth,
					lastPathEnd.getZ() + facing.getZ() * pathWidth);
		
			PathSegment possiblePath = new PathSegment(
					start,
					pathWidth + wallWidth,
					pathWidth,
					dir,
					false);
			
			if(segmentIsFree(buildMap, possiblePath)) {
				newPath = possiblePath;
				break;
			}
		}
		
		return newPath;
	}
	
	private boolean tryExpandSegment(
			BuildMap buildMap,
			PathSegment segment,
			int wallWidth,
			int pathWidth,
			int maxPathLength) {
		
		Vec2 facing = segment.getFacing().toVec2();
		int extensionLength = pathWidth + wallWidth;

		PathSegment extension = new PathSegment(
				segment.getEnd().add(facing.clone().mult(pathWidth)),
				extensionLength,
				pathWidth,
				segment.getFacing(),
				false);
		
		if(!segmentIsFree(buildMap, extension))
			return false;
		
		segment.expand(extensionLength);
		
		for(int i = 2; i < maxPathLength; i++) {
			
			extension.translate(
					facing.getX() * extensionLength,
					facing.getZ() * extensionLength);

			if(segmentIsFree(buildMap, extension))
				segment.expand(extensionLength);
			else
				break;
		}
			
		return true;
	}
	
	private static Directions getExitFacing(Vec2 exit, BuildMap buildMap) {
		
		for(Directions dir : Directions.cardinalValues()) {
			
			Vec2 neighbor = exit.clone().add(dir.toVec2());
			
			if(!buildMap.contains(neighbor))
				continue;
			
			//check if location next to exit is inside maze
			if(buildMap.getType(neighbor) == MazeFillType.UNDEFINED)
				return dir;
		}
		
		throw new IllegalArgumentException("The passed location cannot be an exit of this maze.");
	}
	
	private boolean segmentIsFree(BuildMap buildMap, PathSegment segment) {
		
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