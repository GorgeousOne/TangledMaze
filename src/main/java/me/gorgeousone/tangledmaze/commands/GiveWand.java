package me.gorgeousone.tangledmaze.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.utils.Utils;

public class GiveWand extends BasicCommand {

	public GiveWand(MazeCommand mazeCommand) {
		super("wand", Constants.WAND_PERM, true, mazeCommand);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		player.getInventory().addItem(Utils.getMazeWand());
		return true;
	}
}