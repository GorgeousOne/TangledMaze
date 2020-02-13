package me.gorgeousone.tangledmaze.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.data.Messages;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Constants;

/**
 * This class stores mazes in relation to players.
 * Listeners, commands and tools can access a maze by it's owner here.
 */
public class MazeHandler {

	private BuildHandler buildHandler;
	private Renderer renderer;

	private HashMap<UUID, Maze> mazes = new HashMap<>();

	public MazeHandler(BuildHandler buildHandler, Renderer renderer) {
		this.buildHandler = buildHandler;
		this.renderer = renderer;
	}

	public Maze getMaze(Player player) {
		
		if(!player.hasPermission(Constants.BUILD_PERM))
			return null;
		
		UUID uuid = player.getUniqueId();
		
		if(!mazes.containsKey(uuid))
			mazes.put(uuid, new Maze(player));
		
		return mazes.get(player.getUniqueId());
	}
	
	public ArrayList<Maze> getMazes() {
		return new ArrayList<>(mazes.values());
	}
	
	public boolean hasMaze(Player player) {
		return mazes.containsKey(player.getUniqueId());
	}
	
	public void setMaze(Player player, Maze newMaze) {

		if(hasMaze(player))
			renderer.hideMaze(getMaze(player));
		
		mazes.put(player.getUniqueId(), newMaze);
		renderer.registerMaze(newMaze);
	}
	
	public void removeMaze(Player player) {

		Maze maze = getMaze(player);

		renderer.unregisterMaze(maze);
		buildHandler.removeMaze(maze);
		mazes.remove(player.getUniqueId());
	}

	public Maze getStartedMaze(Player player, boolean withExits, boolean notConstructed) {

		Maze maze = getMaze(player);

		if(!maze.isStarted()) {
			Messages.ERROR_MAZE_NOT_STARTED.sendTo(player);
			player.sendMessage("/tangledmaze start");
			return null;
		}

		if(withExits && !maze.hasExits()) {
			Messages.ERROR_NO_MAZE_EXIT_SET.sendTo(player);
			player.sendMessage("/tangledmaze select exit");
			return null;
		}

		if(notConstructed && maze.isConstructed()) {
			Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
			return null;
		}

		return maze;
	}

	public void processClipChange(Maze maze, ClipChange action) {
		maze.processAction(action, true);
		renderer.displayMazeAction(maze, action);
	}
}