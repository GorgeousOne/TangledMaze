package me.gorgeousone.tangledmaze.mazes;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public abstract class MazeHandler {
	
	private static HashMap<UUID, Maze> mazes = new HashMap<>();
	
	public static boolean hasMaze(Player p) {
		return mazes.containsKey(p.getUniqueId());
	}
	
	public static Maze getMaze(Player p) {
		return mazes.get(p.getUniqueId());
	}
	
	public static void setMaze(Player p, Maze m) {
		
	}
	
	public static void removeMaze(Player p) {
		mazes.remove(p.getUniqueId());
	}
	
	
	//removes all held data to a player in this object.
//	public void remove(Player p) {
//		mazeVisibilities.remove(getMaze(p));
//		mazeDimensions.remove(p.getUniqueId());
//		mazes.remove(p.getUniqueId());
//	}
}