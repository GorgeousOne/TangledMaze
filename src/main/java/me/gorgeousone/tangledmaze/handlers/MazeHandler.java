package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.maze.Maze;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class stores mazes in relation to players.
 * Listeners, commands and tools can access a maze by it's owner here.
 */
public class MazeHandler {
	
	private BuildHandler buildHandler;
	private Renderer renderer;
	
	private HashMap<UUID, Maze> playerMazes = new HashMap<>();
	
	public MazeHandler(BuildHandler buildHandler, Renderer renderer) {
		this.buildHandler = buildHandler;
		this.renderer = renderer;
	}
	
	public ArrayList<Maze> getPlayerMazes() {
		return new ArrayList<>(playerMazes.values());
	}
	
	public Maze getMaze(Player player) {
		
		if (!player.hasPermission(Constants.BUILD_PERM))
			return null;
		
		UUID uuid = player.getUniqueId();
		
		if (!playerMazes.containsKey(uuid))
			playerMazes.put(uuid, new Maze(player));
		
		return playerMazes.get(player.getUniqueId());
	}
	
	public boolean hasMaze(Player player) {
		return playerMazes.containsKey(player.getUniqueId());
	}
	
	public boolean hasStartedMaze(Player player) {
		return hasMaze(player) && getMaze(player).hasClip();
	}
	
	public void setMaze(Player player, Maze newMaze) {
		
		if (hasStartedMaze(player))
			renderer.hideMaze(getMaze(player));
		
		playerMazes.put(player.getUniqueId(), newMaze);
		renderer.displayMaze(newMaze);
	}
	
	public void removeMaze(Player player) {
		
		Maze maze = getMaze(player);
		
		renderer.hideMaze(maze);
		renderer.unregisterMaze(maze);
		buildHandler.removeMaze(maze);
		playerMazes.remove(player.getUniqueId());
	}
	
	public Maze getStartedMaze(Player player, boolean withExits, boolean notConstructed) {
		
		Maze maze = getMaze(player);
		
		if (!maze.hasClip()) {
			Messages.ERROR_MAZE_NOT_STARTED.sendTo(player);
			player.sendMessage("/tangledmaze start");
			return null;
		}
		
		if (withExits && !maze.hasExits()) {
			Messages.ERROR_NO_MAZE_EXIT_SET.sendTo(player);
			player.sendMessage("/tangledmaze select exit");
			return null;
		}
		
		if (notConstructed && maze.isConstructed()) {
			Messages.ERROR_MAZE_PART_ALREADY_BUILT.sendTo(player);
			return null;
		}
		
		return maze;
	}
	
	public void processClipChange(Maze maze, ClipChange action) {
		maze.processAction(action, true);
		renderer.displayMazeAction(maze, action);
	}
}