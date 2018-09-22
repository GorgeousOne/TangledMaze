package me.gorgeousone.tangledmaze.mazes.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.utils.Utils;

public class PathGenerator {

	public static void generatePaths(MazeMap map) {
		
		Maze maze        = map.getMaze();
		int[][] shapeMap = map.getShapeMap();
		Vector start     = map.getStart();
		
		ArrayList<Vector> directions = Utils.CARDINAL_DIRS;
		ArrayList<Vector> openEnds = new ArrayList<>();

		openEnds.add(start);
		Vector lastEnd;

		int	pathLength = 0,
			pathWidth  = maze.getPathWidth(),
			wallWidth  = maze.getWallWidth();

		Random rnd = new Random();

		while(!openEnds.isEmpty()) {
			
			if(pathLength < 3)
				lastEnd = openEnds.get(openEnds.size()-1);
			else {
				lastEnd = openEnds.get(rnd.nextInt(openEnds.size()));
				pathLength = 0;
			}
			
			Collections.shuffle(directions);
			boolean allDirectionsBlocked = true;

			for(Vector dir : directions) {
				
				int dirX = dir.getBlockX(),
					dirZ = dir.getBlockZ();
				
				int pathX = lastEnd.getBlockX() + dirX * pathWidth,
					pathZ = lastEnd.getBlockZ() + dirZ * pathWidth;
				
				MazeSegment pathSegment = new MazeSegment(
						pathX,
						pathZ,
						dirX,
						dirZ,
						pathWidth + wallWidth,
						pathWidth,
						false);
						
				boolean pathNotAvailable = false;
				
				for(Vector point : pathSegment.getFill()) {
					if(point.getBlockX() < 0 || point.getBlockX() >= shapeMap.length ||
					   point.getBlockZ() < 0 || point.getBlockZ() >= shapeMap[0].length) {
						pathNotAvailable = true;
						break;
					}
					
					if(shapeMap[point.getBlockX()][point.getBlockZ()] != MazeSegment.UNDEFINED &&
					   shapeMap[point.getBlockX()][point.getBlockZ()] != MazeSegment.EXIT) {
						pathNotAvailable = true;
						break;
					}
				}

				if(pathNotAvailable)
					continue;

				for(Vector point : pathSegment.getFill())
					shapeMap[point.getBlockX()][  point.getBlockZ()] = MazeSegment.PATH;
				
				openEnds.add(pathSegment.getEnd());
				pathLength++;
				allDirectionsBlocked = false;
				break;
			}
			
			if(!allDirectionsBlocked)
				continue;
			
			openEnds.remove(lastEnd);
			pathLength = 0;
		}
	}
}