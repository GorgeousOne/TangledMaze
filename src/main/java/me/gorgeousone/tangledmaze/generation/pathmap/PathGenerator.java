package me.gorgeousone.tangledmaze.generation.pathmap;

import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class PathGenerator {
	
	private PathGenerator() {}
	
	public static void createPathsInPathMap(PathMap pathMap, int pathLength) {
		
		List<Vec2> openPathEnds = new ArrayList<>();
		openPathEnds.add(pathMap.getPathStartGridPoint());
		
		int linkedPathSegmentCount = 0;
		int maxLinkedPathSegments = 4;
		
		boolean lastPathWasExpanded = false;
		Random random = new Random();
		
		while (!openPathEnds.isEmpty()) {
			
			Vec2 nextPathEnd;
			
			if (linkedPathSegmentCount < maxLinkedPathSegments) {
				nextPathEnd = openPathEnds.get(0);
				linkedPathSegmentCount++;
				
			} else {
				nextPathEnd = openPathEnds.get(random.nextInt(openPathEnds.size()));
				linkedPathSegmentCount = 0;
			}
			
			Map<Direction, List<Vec2>> getFreePaths = getFreePaths(pathMap, nextPathEnd);
			
			if (getFreePaths.size() <= 1) {
				
				openPathEnds.remove(nextPathEnd);
				linkedPathSegmentCount = 0;
				
				if (getFreePaths.isEmpty())
					continue;
			}
			
			List<Direction> keysAsArray = new ArrayList<>(getFreePaths.keySet());
			Direction rndFacing = keysAsArray.get(random.nextInt(getFreePaths.size()));
			List<Vec2> rndNewPath = getFreePaths.get(rndFacing);
			
			if(pathLength > 1) {
				if (!lastPathWasExpanded)
					lastPathWasExpanded = tryToExpandPath(pathMap, rndNewPath, rndFacing, random.nextInt(pathLength) + 1);
				else
					lastPathWasExpanded = false;
			}
			
			for(Vec2 pathCell : rndNewPath)
				pathMap.setGridCellType(pathCell, PathAreaType.PATH);
			
			openPathEnds.add(0, rndNewPath.get(rndNewPath.size()-1));
		}
	}
	
	private static Map<Direction, List<Vec2>> getFreePaths(PathMap pathMap, Vec2 pathEnd) {
		
		Map<Direction, List<Vec2>> freePaths = new HashMap<>();
		
		for (Direction facing : Direction.fourCardinals()) {
			List<Vec2> newPath = getNewPath(pathEnd, facing);
			
			if(pathMap.arePathGridCellsFree(newPath))
				freePaths.put(facing, newPath);
		}
		
		return freePaths;
	}
	
	private static boolean tryToExpandPath(PathMap pathMap, List<Vec2> path, Direction facing, int maxLength) {
		
		boolean pathWasExpanded = false;
		
		for(int i = 1; i < maxLength; i++) {
			
			Vec2 currentPathEnd = path.get(path.size()-1);
			List<Vec2> expansion = getNewPath(currentPathEnd, facing);

			if(pathMap.arePathGridCellsFree(expansion)) {
				path.addAll(expansion);
				pathWasExpanded = true;
			
			}else
				break;
		}
		
		return pathWasExpanded;
	}
	
	private static List<Vec2> getNewPath(Vec2 pathEnd, Direction facing) {
		
		Vec2 neighborCell1 = pathEnd.clone().add(facing.getVec2());
		Vec2 neighborCell2 = neighborCell1.clone().add(facing.getVec2());
		
		return new ArrayList<>(Arrays.asList(neighborCell1, neighborCell2));
	}
}
