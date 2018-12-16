package me.gorgeousone.tangledmaze.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.shapes.Shape;
import me.gorgeousone.tangledmaze.tools.ToolHandler;
import me.gorgeousone.tangledmaze.tools.ClippingTool;
import me.gorgeousone.tangledmaze.utils.Constants;

public class PlayerListener implements Listener {
	
	public PlayerListener() {
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission(Constants.buildPerm)) {
				ToolHandler.setTool(p, new ClippingTool(p, Shape.RECT));
				MazeHandler.setMaze(p, new Maze(p));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		
		if(p.hasPermission(Constants.buildPerm)) {
			ToolHandler.setTool(p, new ClippingTool(p, Shape.RECT));
			MazeHandler.setMaze(p, new Maze(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		
		if(p.hasPermission(Constants.buildPerm)) {
			ToolHandler.removeTool(p);
			MazeHandler.removeMaze(p);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChangeWorld(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
			
		if(p.hasPermission(Constants.buildPerm)) {
			MazeHandler.getMaze(p).reset();
			ToolHandler.resetToDefaultTool(p);
		}
	}
}