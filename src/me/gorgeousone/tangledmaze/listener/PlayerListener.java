package me.gorgeousone.tangledmaze.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;

public class PlayerListener implements Listener {
	
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		Player player = e.getPlayer();
		
		if(player.hasPermission(Constants.BUILD_PERM)) {
			BuildHandler.removeMaze(MazeHandler.getMaze(player));
			ToolHandler.removeTool(player);
			MazeHandler.removeMaze(player);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		
		Player player = event.getPlayer();
			
		if(!player.hasPermission(Constants.BUILD_PERM))
			return;
					
		ToolHandler.resetToDefaultTool(player);
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.isStarted() && maze.getWorld().equals(player.getWorld()))
			Renderer.displayMaze(maze);
	}
}