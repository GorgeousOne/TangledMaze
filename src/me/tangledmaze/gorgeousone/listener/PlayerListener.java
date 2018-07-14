package me.tangledmaze.gorgeousone.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class PlayerListener implements Listener {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public PlayerListener() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		//TODO create selection
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if(p.hasPermission(Constants.buildPerm)) {
			mHandler.remove(p);
			sHandler.remove(p);
			mHandler.leaveBuildQueue(mHandler.getMaze(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChangeWorld(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
			
		if(p.hasPermission(Constants.buildPerm)) {
			mHandler.discardMaze(e.getPlayer());
			sHandler.remove(p);
		}
	}
}