package me.tangledmaze.gorgeousone.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.tangledmaze.gorgeousone.main.TangledMain;

public class Deselecter implements Listener {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public Deselecter(TangledMain plugin) {
		sHandler = plugin.getSelectionHandler();
		mHandler = plugin.getMazeHandler();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		//TODO permission check maybe?

		mHandler.removeMaze(e.getPlayer());
		sHandler.removeSelection(p);
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
			
		//TODO sam as above
		
		mHandler.removeMaze(e.getPlayer());
		sHandler.removeSelection(p);
	}
}