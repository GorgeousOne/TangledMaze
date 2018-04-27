package me.tangledmaze.gorgeousone.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeBuilder;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class PlayerVanishListener implements Listener {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	private MazeBuilder mBuilder;
	
	public PlayerVanishListener() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		mBuilder = TangledMain.getPlugin().getMazeBuilder();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if(p.hasPermission(Constants.buildPerm)) {
			mHandler.remove(p);
			sHandler.remove(p);
			mBuilder.discard(mHandler.getMaze(p));
		}
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
			
		if(p.hasPermission(Constants.buildPerm)) {
			mHandler.deselctMaze(e.getPlayer());
			sHandler.remove(p);
		}
	}
}