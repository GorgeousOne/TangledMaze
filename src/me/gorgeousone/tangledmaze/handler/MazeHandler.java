package me.gorgeousone.tangledmaze.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Constants;

/**
 * This class stores mazes in relation to players.
 * Listeners, commands and tools can access a maze by it's owner here.
 */
public final class MazeHandler {
	
	private static HashMap<UUID, Maze> mazes = new HashMap<>();

	private MazeHandler() {}
	
	public static void reload() {
		mazes.clear();
	}
	
	public static Maze getMaze(Player player) {
		
		if(!player.hasPermission(Constants.BUILD_PERM))
			return null;
		
		UUID uuid = player.getUniqueId();
		
		if(!mazes.containsKey(uuid))
			mazes.put(uuid, new Maze(player));
		
		return mazes.get(player.getUniqueId());
	}
	
	public static ArrayList<Maze> getMazes() {
		return new ArrayList<>(mazes.values());
	}
	
	public static boolean hasMaze(Player player) {
		return mazes.containsKey(player.getUniqueId());
	}
	
	public static void setMaze(Player player, Maze newMaze) {
		mazes.put(player.getUniqueId(), newMaze);
		Renderer.registerMaze(newMaze);
	}
	
	public static void removeMaze(Player player) {
		Renderer.unregisterMaze(getMaze(player));
		mazes.remove(player.getUniqueId());
	}
}