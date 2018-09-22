package me.gorgeousone.tangledmaze.mazes;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.mazes.generators.BlockGenerator;
import me.gorgeousone.tangledmaze.mazes.generators.ExitGenerator;
import me.gorgeousone.tangledmaze.mazes.generators.MazeMap;
import me.gorgeousone.tangledmaze.mazes.generators.PathGenerator;

public abstract class MazeHandler {
	
	private static HashMap<UUID, Maze> mazes = new HashMap<>();
//	private static ArrayList<Maze> queuedMazes = new ArrayList<>();
	
//	private static ActionListener finishCallback = new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			if(mazeUnderConstuction.getPlayer() != null)
//				mazeUnderConstuction.getPlayer().sendMessage(Constants.prefix + "Your maze has been finished!");
//			
//			queuedMazes.remove(mazeUnderConstuction);
//			
//			if(!queuedMazes.isEmpty())
//				buildMaze(queuedMazes.get(0));
//		}
//	};
	
	public static void reload() {
		mazes.clear();
//		queuedMazes.clear();
//		mazeUnderConstuction = null;
	}
	
	public static Maze getMaze(Player p) {
		return mazes.get(p.getUniqueId());
	}
	
//	public static Maze getMazeUnderConstruction() {
//		return mazeUnderConstuction;
//	}
	
	public static void setMaze(Player p, Maze maze) {
		mazes.put(p.getUniqueId(), maze);
		Renderer.registerMaze(maze);
	}
	
	public static void removeMaze(Player p) {
		Renderer.unregisterMaze(getMaze(p));
		mazes.remove(p.getUniqueId());
	}
	
//	public static boolean isMazeEnqueued(Maze maze) {
//		return queuedMazes.contains(maze);
//	}
//	
//	public static int enqueueMaze(Maze maze) {
//		
//		if(queuedMazes.contains(maze))
//			return -1;
//		
//		queuedMazes.add(maze);
//		
//		return queuedMazes.indexOf(maze);
//	}
	
	public static void buildMaze(Maze maze) {
//		if(queuedMazes.isEmpty())
//			return;
		
//		mazeUnderConstuction = maze;
		Renderer.hideMaze(maze);
		
		MazeMap map = new MazeMap(maze);
		map.setStart(ExitGenerator.generateExits(map));
		PathGenerator.generatePaths(map);
		BlockGenerator.generateBlocks(map, null);
	}
}