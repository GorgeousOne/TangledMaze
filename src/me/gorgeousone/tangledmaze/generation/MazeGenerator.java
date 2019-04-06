package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.core.Maze;

public class MazeGenerator {

	private PathGenerator pathGenerator;
	private BlockGenerator blockGenerator;
	
	public MazeGenerator() {
		
		pathGenerator = new PathGenerator();
		blockGenerator = new BlockGenerator();
	}

	public void generateMaze(Maze maze) {

		BuildMap buildMap = new BuildMap(maze);
		
		pathGenerator.generatePaths(buildMap);
		blockGenerator.generateBlocks(buildMap);
	}
}
