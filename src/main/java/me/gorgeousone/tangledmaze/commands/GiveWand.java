package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.utils.WandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveWand extends BasicCommand {
	
	public GiveWand(MazeCommand mazeCommand) {
		super("wand", Constants.WAND_PERM, true, mazeCommand);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		player.getInventory().addItem(WandUtils.createMazeWand());
		return true;
	}
}