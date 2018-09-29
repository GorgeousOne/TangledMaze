package me.gorgeousone.tangledmaze.mazes.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.utils.Utils;
import me.gorgeousone.tangledmaze.utils.Vec2;

public class PathGenerator {

	public static void generatePaths(MazeMap map) {
		
		Maze maze = map.getMaze();
		ArrayList<Vec2> openEnds = new ArrayList<>();
		
		openEnds.add(map.getStart());
		
		Vec2 currentEnd;

		int	currentPathLength = 0,
			pathWidth  = maze.getPathWidth(),
			wallWidth  = maze.getWallWidth();

		Random rnd = new Random();

		while(!openEnds.isEmpty()) {
			
			if(currentPathLength < 3)
				currentEnd = openEnds.get(openEnds.size()-1);
			else {
				currentEnd = openEnds.get(rnd.nextInt(openEnds.size()));
				currentPathLength = 0;
			}
			
			Collections.shuffle(Utils.CARDINAL_DIRS);
			boolean allDirectionsBlocked = true;
			
			for(Vector dir : Utils.CARDINAL_DIRS) {
				
				Vec2
					facing = new Vec2(dir),
					start  = new Vec2(currentEnd.getX() + facing.getX() * pathWidth,
									  currentEnd.getZ() + facing.getZ() * pathWidth);
				
				MazeSegment pathSegment = new MazeSegment(
						start,
						facing,
						pathWidth + wallWidth,
						pathWidth,
						false);
						
				boolean pathNotAvailable = false;
				
				for(Vec2 point : pathSegment.getFill()) {
					
					if(point.getX() < 0 || point.getX() >= map.getDimX() ||
					   point.getZ() < 0 || point.getZ() >= map.getDimZ()) {
						pathNotAvailable = true;
						break;
					}
					
					if(map.getType(point) != MazeSegment.UNDEFINED &&
					   map.getType(point) != MazeSegment.EXIT) {
						pathNotAvailable = true;
						break;
					}
				}

				if(pathNotAvailable)
					continue;
				
				for(Vec2 point : pathSegment.getFill())
					map.setType(point, MazeSegment.PATH);
				
				openEnds.add(pathSegment.getEnd());
				currentPathLength++;
				allDirectionsBlocked = false;
				break;
			}
			
			if(!allDirectionsBlocked)
				continue;
			
			openEnds.remove(currentEnd);
			currentPathLength = 0;
		}
	}
}