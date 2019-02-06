package me.gorgeousone.tangledmaze.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Vec2;

public final class PathGenerator {
	
	private static ArrayList<Directions> shuffledCardinals = new ArrayList<>(Arrays.asList(Directions.cardinalValues()));
	private static Random rnd = new Random();
	
	private PathGenerator() {}
	
	public static void generatePaths(BuildMap map) {
		
		Maze maze = map.getMaze();
		
		ArrayList<Vec2> openEnds = new ArrayList<>();
		openEnds.add(map.getStart());
		
		int	pathWidth  = maze.getPathWidth(),
			wallWidth  = maze.getWallWidth(),
			linkedPathsLength = 0;
					
		while(!openEnds.isEmpty()) {
			
			Vec2 currentEnd;
			
			if(linkedPathsLength < 3) {
				currentEnd = openEnds.get(openEnds.size()-1);
			
			}else {
				currentEnd = openEnds.get(rnd.nextInt(openEnds.size()));
				linkedPathsLength = 0;
			}
			
			PathSegment path = createPathSegment(map, currentEnd, pathWidth, wallWidth);
			
			if(path == null) {
				
				openEnds.remove(currentEnd);
				linkedPathsLength = 0;
			
			}else {
				
				map.drawSegment(path, MazeFillType.PATH);
				openEnds.add(path.getEnd());
				linkedPathsLength++;
			}
		}
	}
	
	private static PathSegment createPathSegment(BuildMap map, Vec2 currentEnd, int pathWidth, int wallWidth) {
		
		Collections.shuffle(shuffledCardinals);
		
		for(Directions dir : shuffledCardinals) {

			Vec2
				facing = dir.toVec2(),
				start  = new Vec2(currentEnd.getIntX() + facing.getIntX() * pathWidth,
								  currentEnd.getIntZ() + facing.getIntZ() * pathWidth);
		
			PathSegment path = new PathSegment(
					start,
					facing,
					pathWidth + wallWidth,
					pathWidth,
					false);
				
			if(pathIsFree(map, path)) {
				return path;
			}
		}
		
		return null;
	}
	
	private static boolean pathIsFree(BuildMap map, PathSegment path) {
		
		for(Vec2 point : path.getFill()) {
			
			if(point.getIntX() < 0 || point.getIntX() >= map.getDimX() ||
			   point.getIntZ() < 0 || point.getIntZ() >= map.getDimZ()) {
				return false;
			}
			
			if(map.getType(point) != MazeFillType.UNDEFINED &&
			   map.getType(point) != MazeFillType.EXIT) {
				return false;
			}
		}
		
		return true;
	}
}