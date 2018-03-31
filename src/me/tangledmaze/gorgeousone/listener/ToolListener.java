package me.tangledmaze.gorgeousone.listener;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.tangledmaze.gorgeousone.main.TangledMain_go;
import me.tangledmaze.main.TangledMain;

public class ToolListener implements Listener {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	@SuppressWarnings("unused")
	private HashMap<Player, Long> timer;
	
	public ToolListener(TangledMain_go plugin) {
		sHandler = plugin.getSelectionHandler();
		mHandler = plugin.getMazeHandler();
	}
	
	@EventHandler
	public void onSlotSwitch(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		Inventory i = p.getInventory();
		
		ItemStack previousItem = i.getItem(e.getPreviousSlot()),
				  newItem      = i.getItem(e.getNewSlot());
		
		if(TangledMain.isSelectionWand(newItem)) {
//			p.sendMessage("nice wand");

			if(mHandler.hasMaze(p))
				mHandler.getMaze(p).show();
			if(sHandler.hasSelection(p))
				sHandler.getSelection(p).show();
		
		}else if(TangledMain.isSelectionWand(previousItem)) {
			
			if(mHandler.hasMaze(p))
				mHandler.getMaze(p).hide();
			if(sHandler.hasSelection(p))
				sHandler.getSelection(p).hide();
		}
	}
}