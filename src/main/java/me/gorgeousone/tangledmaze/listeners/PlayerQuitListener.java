package me.gorgeousone.tangledmaze.listeners;

import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
	
	private ToolHandler toolHandler;
	private MazeHandler mazeHandler;
	private BuildHandler buildHandler;
	
	public PlayerQuitListener(ToolHandler toolHandler,
	                          MazeHandler mazeHandler,
	                          BuildHandler buildHandler) {
		
		this.toolHandler = toolHandler;
		this.mazeHandler = mazeHandler;
		this.buildHandler = buildHandler;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		Player player = e.getPlayer();
		
		if (player.hasPermission(Constants.BUILD_PERM)) {
			
			if (mazeHandler.hasStartedMaze(player))
				buildHandler.removeMaze(mazeHandler.getMaze(player));
			
			toolHandler.removePlayer(player);
			mazeHandler.removePlayer(player);
			
		}
	}
}