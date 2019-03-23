package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.util.Utils;

public class GiveWand extends MazeCommand {

	public GiveWand() {
		super("wand", "/tangledmaze wand", 0, true, Constants.WAND_PERM);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		//TODO localize message
		Player player = (Player) sender;
		player.getInventory().addItem(Utils.getMazeWand());
		return true;
		
	}
}