package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class NewPathGenerator {
	
	private NewPathGenerator() {}
	
	public static void populatePathMap(PathMap pathMap) {
		
		List<Vec2> openPathEnds = new ArrayList<>();
		openPathEnds.add(pathMap.getPathStartInGrid());
		
		int linkedPathSegmentCount = 0;
		int maxLinkedPathSegments = 4;
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
			
			List<Map.Entry<Vec2, Vec2>> freeNeighbors = getFreePathNeighbors(pathMap, nextPathEnd);
			
			if (freeNeighbors.size() < 2) {
				openPathEnds.remove(nextPathEnd);
				linkedPathSegmentCount = 0;
				
				if (freeNeighbors.isEmpty())
					continue;
			}
			
			Map.Entry<Vec2, Vec2> rndNeighbor = freeNeighbors.get(random.nextInt(freeNeighbors.size()));
			pathMap.setPathAreaType(rndNeighbor.getKey(), PathAreaType.PATH);
			pathMap.setPathAreaType(rndNeighbor.getValue(), PathAreaType.PATH);
			
			openPathEnds.add(0, rndNeighbor.getValue());
		}
	}
	
	private static List<Map.Entry<Vec2, Vec2>> getFreePathNeighbors(PathMap pathMap, Vec2 pathEnd) {
		
		List<Map.Entry<Vec2, Vec2>> freeNeighbors = new ArrayList<>();
		
		for (Direction facing : Direction.fourCardinals()) {
			
			Vec2 neighborPoint1 = pathEnd.clone().add(facing.getVec2());
			Vec2 neighborPoint2 = neighborPoint1.clone().add(facing.getVec2());
			
			if (pathMap.isFree(neighborPoint1) && pathMap.isFree(neighborPoint2))
				freeNeighbors.add(new AbstractMap.SimpleEntry<>(neighborPoint1, neighborPoint2));
		}
		
		return freeNeighbors;
	}
}
