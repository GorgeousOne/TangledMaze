package me.gorgeousone.tangledmaze.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		Player player = e.getPlayer();
		
		if(player.hasPermission(Constants.BUILD_PERM)) {
			
			Maze maze = MazeHandler.getMaze(player);
			Renderer.unregisterMaze(maze);
			BuildHandler.removeMaze(maze);
			ToolHandler.removeTool(player);
			MazeHandler.removeMaze(player);
		}
	}
}