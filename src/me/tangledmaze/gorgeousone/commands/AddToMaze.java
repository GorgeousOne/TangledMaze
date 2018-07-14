package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.ShapeSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;
import net.md_5.bungee.api.ChatColor;

public class AddToMaze {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public AddToMaze() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!mHandler.hasMaze(p)) {
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		if(!sHandler.hasShapeSelection(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area with a selection wand first.");
			p.sendMessage("/tangledmaze wand");
			return;
		}
		
		ShapeSelection selection = sHandler.getSelection(p);
		
		if(!selection.isComplete()) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
			return;
		}
		
		try {
			mHandler.addSelectionToMaze(mHandler.getMaze(p), sHandler.getSelection(p));
		}catch (IllegalArgumentException e) {
			p.sendMessage(ChatColor.RED + "Your selection does not seem to touch your maze directly (outline on outline).");
		}
	}
}