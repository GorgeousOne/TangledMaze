package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.core.Maze;

public class MazeGenerator {

	private ExitGenerator exitGenerator;
	private PathGenerator pathGenerator;
	private BlockGenerator blockGenerator;

	public MazeGenerator() {

		exitGenerator = new ExitGenerator();
		pathGenerator = new PathGenerator();
		blockGenerator = new BlockGenerator();
	}

	public void buildMaze(Maze maze) {

		BuildMap map = new BuildMap(maze);
		exitGenerator.generateExits(map);
		pathGenerator.generatePaths(map);
		blockGenerator.generateBlocks(map);
	}
}
