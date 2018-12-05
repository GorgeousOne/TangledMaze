package me.gorgeousone.tangledmaze.mazes.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.Vec2;

public class PathGenerator {

	public static void generatePaths(BuildMap map) {
		
		Maze maze = map.getMaze();
		ArrayList<Vec2> openEnds = new ArrayList<>();
		
		openEnds.add(map.getStart());
		
		int	pathWidth  = maze.getPathWidth(),
			wallWidth  = maze.getWallWidth(),
			linkedPathsLength = 0;
					

		Random rnd = new Random();
		ArrayList<Directions> shuffledCardinals = new ArrayList<>(Arrays.asList(Directions.cardinalValues()));
		while(!openEnds.isEmpty()) {
			
			Vec2 currentEnd;
			
			if(linkedPathsLength < 3)
				currentEnd = openEnds.get(openEnds.size()-1);
			else {
				currentEnd = openEnds.get(rnd.nextInt(openEnds.size()));
				linkedPathsLength = 0;
			}
			
			Collections.shuffle(shuffledCardinals);
			boolean allDirectionsBlocked = true;
			
			for(Directions dir : shuffledCardinals) {
				
				Vec2
					facing = dir.facing(),
					start  = new Vec2(currentEnd.getX() + facing.getX() * pathWidth,
									  currentEnd.getZ() + facing.getZ() * pathWidth);
				
				PathSegment path = new PathSegment(
						start,
						facing,
						pathWidth + wallWidth,
						pathWidth,
						false);
						
				if(!pathIsFree(map, path))
					continue;
				
				for(Vec2 point : path.getFill())
					map.setType(point, MazeFillType.PATH);
				
				openEnds.add(path.getEnd());
				linkedPathsLength++;
				allDirectionsBlocked = false;
				break;
			}
			
			if(!allDirectionsBlocked)
				continue;
			
			openEnds.remove(currentEnd);
			linkedPathsLength = 0;
		}
	}
	
	private static boolean pathIsFree(BuildMap map, PathSegment path) {
		
		for(Vec2 point : path.getFill()) {
			
			if(point.getX() < 0 || point.getX() >= map.getDimX() ||
			   point.getZ() < 0 || point.getZ() >= map.getDimZ()) {
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