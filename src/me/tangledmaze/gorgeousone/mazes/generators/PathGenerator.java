package me.tangledmaze.gorgeousone.mazes.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.utils.Utils;

public class PathGenerator {

	public static void generatePaths(Maze maze, int[][] mazeMap, Vector start) {
		
		Random rnd = new Random();
		ArrayList<Vector> directions = Utils.cardinalDirs();
		
		ArrayList<Vector> openEnds = new ArrayList<>();
		openEnds.add(start);
		
		int	pathLength = 0,
			pathWidth  = maze.getDimensions().getBlockX(),
			wallWidth  = maze.getDimensions().getBlockZ();
		
		Vector lastEnd;

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
				
				MazePath path = new MazePath(
						pathX,
						pathZ,
						dirX,
						dirZ,
						pathWidth + wallWidth,
						pathWidth,
						false);
						
				boolean pathNotAvailable = false;
				
				for(Vector point : path.getFill()) {
					if(point.getBlockX() < 0 || point.getBlockX() >= mazeMap.length ||
					   point.getBlockZ() < 0 || point.getBlockZ() >= mazeMap[0].length) {
						pathNotAvailable = true;
						break;
					}
					
					if(mazeMap[point.getBlockX()][point.getBlockZ()] != MazePath.UNDEFINED &&
					   mazeMap[point.getBlockX()][point.getBlockZ()] != MazePath.EXIT) {
						pathNotAvailable = true;
						break;
					}
				}

				if(pathNotAvailable)
					continue;

				for(Vector point : path.getFill())
					mazeMap[point.getBlockX()][  point.getBlockZ()] = MazePath.PATH;
				
				openEnds.add(path.getEnd());
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