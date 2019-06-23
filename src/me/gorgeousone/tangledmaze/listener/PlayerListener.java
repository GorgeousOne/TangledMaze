package me.gorgeousone.tangledmaze.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.shape.Shape;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

public class PlayerListener implements Listener {
	
	public PlayerListener() {
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission(Constants.BUILD_PERM)) {
				ToolHandler.setTool(player, new ClippingTool(player, Shape.RECT));
				MazeHandler.setMaze(player, new Maze(player));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		Player player = e.getPlayer();
		
		if(player.hasPermission(Constants.BUILD_PERM)) {
			ToolHandler.setTool(player, new ClippingTool(player, Shape.RECT));
			MazeHandler.setMaze(player, new Maze(player));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		Player player = e.getPlayer();
		
		if(player.hasPermission(Constants.BUILD_PERM)) {
			ToolHandler.removeTool(player);
			MazeHandler.removeMaze(player);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChangeWorld(PlayerChangedWorldEvent e) {
		Player player = e.getPlayer();
			
		if(player.hasPermission(Constants.BUILD_PERM)) {
			MazeHandler.setMaze(player, new Maze(player));
			ToolHandler.resetToDefaultTool(player);
		}
	}
}