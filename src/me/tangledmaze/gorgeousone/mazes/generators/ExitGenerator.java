package me.tangledmaze.gorgeousone.mazes.generators;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.utils.Utils;

public class ExitGenerator {

	public static Vector generateExits(Maze maze, int[][] mazeMap, int mazeMinX, int mazeMinZ) {
		
		Vector pathStart = null;
		
		int pathWidth = maze.getDimensions().getBlockX(),
			wallWidth = maze.getDimensions().getBlockZ();
		
		int pathGridOffsetX = 0,
			pathGridOffsetZ = 0;
		
		for(Location exit : maze.getExits()) {
			
			for(Vector dir : Utils.cardinalDirs()) {
				Location exit2 = exit.clone().add(new Vector(-mazeMinX, 0, -mazeMinZ)).add(dir);
				
				if(exit2.getBlockX() < 0 || exit2.getBlockX() >= mazeMap.length ||
				   exit2.getBlockZ() < 0 || exit2.getBlockZ() >= mazeMap[0].length)
					continue;
				
				if(mazeMap[exit2.getBlockX()][exit2.getBlockZ()] == MazePath.UNDEFINED) {
					
					int facingX = dir.getBlockX(),
						facingZ = dir.getBlockZ();
					
					int exitX = exit.getBlockX() - mazeMinX,
						exitZ = exit.getBlockZ() - mazeMinZ;
					
					MazePath exitRect;
					int fillType = MazePath.EXIT;

					if(pathStart == null) {
						
						exitRect = new MazePath(
							exitX,
							exitZ,
							facingX,
							facingZ,
							2*pathWidth,
							pathWidth,
							true);
						
						//make the main exit out of path so the path generator wont intersect it anyhow
						fillType = MazePath.PATH;
						pathStart = exitRect.getEnd();
						
						pathGridOffsetX = pathStart.getBlockX() % (pathWidth + wallWidth);
						pathGridOffsetZ = pathStart.getBlockZ() % (pathWidth + wallWidth);
						
						pathStart = exitRect.getEnd();
						
					}else {

						exitRect = new MazePath(
							exitX,
							exitZ,
							facingX,
							facingZ,
							pathWidth,
							pathWidth,
							true);
						
						int exitOffset;
						
						if(facingX != 0)
							exitOffset = exitRect.getStart().getBlockX() - pathGridOffsetX;
						else
							exitOffset = exitRect.getStart().getBlockZ() - pathGridOffsetZ;
						
						//get rid off negative values...
						if(exitOffset <= 0)
							exitOffset += pathWidth + wallWidth;
						//... and offsets bigger than one path+wall segment
						else if(exitOffset > pathWidth + wallWidth)
							exitOffset %= pathWidth + wallWidth;
						
						//reverse offset if facing is not "pointing" on the offsets to the grid
						if(facingX == 1 || facingZ == 1) {
							exitOffset = (pathWidth + wallWidth) - exitOffset;
							
							if(exitOffset <= 0)
								exitOffset += pathWidth + wallWidth;
						}
						
						exitRect.expand(exitOffset);
					}
					
					for(Vector point : exitRect.getFill())
						if(point.getBlockX() >= 0 || point.getBlockX() < mazeMap.length ||
						   point.getBlockZ() >= 0 || point.getBlockZ() < mazeMap[0].length)
							mazeMap[point.getBlockX()][point.getBlockZ()] = fillType;
				}
			}
		}
		
		return pathStart;
	}
}