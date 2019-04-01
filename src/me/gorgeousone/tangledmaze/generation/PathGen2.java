package me.gorgeousone.tangledmaze.generation;

import org.bukkit.Location;

import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Vec2;

public class PathGen2 {
	
	public void constructPaths(BuildMap buildMap) {
		
		generateExitSegments(buildMap);
		generatePathSegments(buildMap);
	}
	
	private void generateExitSegments(BuildMap buildMap) {
	
		//entrance + exits
	}
	
	protected void generatePathSegments(BuildMap buildMap) {
		
		//path stuff againsrc
	}

	protected PathSegment getEntranceSegment(
			MazePoint entrance,
			BuildMap buildMap,
			int pathWidth,
			int wallWidth) {
		
		MazePoint relEntrance = entrance.clone();
		relEntrance.subtract(buildMap.getMinX(), 0, buildMap.getMinZ());
		
		PathSegment entranceSegment = new PathSegment(
			new Vec2(relEntrance.toVector()),
			getExitFacing(entrance, buildMap).toVec2(),
			wallWidth + pathWidth,
			pathWidth,
			true);
		
		return entranceSegment;
	}
	
	protected PathSegment getExitSegment(
			MazePoint entrancePoint,
			BuildMap buildMap,
			int pathWidth,
			int wallWidth) {
		
		
		return null;
	}
	
	protected PathSegment getPathSegment(
			BuildMap map,
			Vec2 currentEnd,
			int pathWidth,
			int wallWidth) {
		
		
		return null;
	}
	
	public static Directions getExitFacing(Location exit, BuildMap buildMap) {
		
		for(Directions dir : Directions.cardinalValues()) {
			
			Vec2 nextToExit = new Vec2(exit.toVector());
			nextToExit.add(dir.toVec2());
			
			if(nextToExit.getIntX() < 0 || nextToExit.getIntX() >= buildMap.getDimX() ||
			   nextToExit.getIntZ() < 0 || nextToExit.getIntZ() >= buildMap.getDimZ())
				continue;
			
			if(buildMap.getType(nextToExit) == MazeFillType.UNDEFINED)
				return dir;
		}
		
		return null;
	}
}