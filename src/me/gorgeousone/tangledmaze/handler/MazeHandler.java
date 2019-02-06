package me.gorgeousone.tangledmaze.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.generation.BlockGenerator;
import me.gorgeousone.tangledmaze.generation.BuildMap;
import me.gorgeousone.tangledmaze.generation.ExitGenerator;
import me.gorgeousone.tangledmaze.generation.PathGenerator;

public abstract class MazeHandler {
	
	private static HashMap<UUID, Maze> mazes = new HashMap<>();

	public static void reload() {
		mazes.clear();
	}
	
	public static Maze getMaze(Player player) {
		return mazes.get(player.getUniqueId());
	}
	
	public static ArrayList<Maze> getMazes() {
		return new ArrayList<>(mazes.values());
	}
	
	public static boolean hasMaze(Player player) {
		return mazes.containsKey(player.getUniqueId());
	}
	
	public static void setMaze(Player player, Maze maze) {
		mazes.put(player.getUniqueId(), maze);
		Renderer.registerMaze(maze);
	}
	
	public static void removeMaze(Player player) {
		Renderer.unregisterMaze(getMaze(player));
		mazes.remove(player.getUniqueId());
	}
	
	public static void buildMaze(Maze maze) {
		Renderer.hideMaze(maze);
		
		BukkitRunnable async = new BukkitRunnable() {
			@Override
			public void run() {

				BuildMap map = new BuildMap(maze);
				ExitGenerator.generateExits(map);
				PathGenerator.generatePaths(map);
				BlockGenerator.generateBlocks(map);
			}
		};
		
		async.runTaskAsynchronously(TangledMain.getPlugin());
	}
}