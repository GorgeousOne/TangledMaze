package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.util.Utils;

public class GiveWand extends BasicCommand {

	public GiveWand(MazeCommand mazeCommand) {
		super("wand", Constants.WAND_PERM, mazeCommand);
	}
	
	@Override
	public boolean onExecute(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		player.getInventory().addItem(Utils.getMazeWand());
		return true;
	}
}