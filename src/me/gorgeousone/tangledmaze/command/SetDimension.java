package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.MazeDimension;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import me.gorgeousone.tangledmaze.util.Utils;
import net.md_5.bungee.api.ChatColor;

public class SetDimension extends MazeCommand {
	
	public SetDimension() {
		super("set", "/tangledmaze set <dimension> <integer>", 2, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;
		
		MazeDimension dimension = MazeDimension.match(arguments[0]);
		
		if(dimension == null) {
			sender.sendMessage(ChatColor.RED + "\"" + arguments[0] + "\" is not a dimension.");
			return false;
		}
		
		String stringValue = arguments[1];
		int newValue;
		
		try {
			newValue = Utils.limit(Integer.parseInt(stringValue), 1, dimension.getMaxValue());
			
		} catch (NumberFormatException ex) {
			
			Messages.ERROR_INVALID_NUMBER.sendTo(player, new PlaceHolder("number", stringValue));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getDimension(dimension) != newValue) {
			maze.setDimension(dimension, newValue);
		
			Messages.MESSAGE_DIMENSION_CHANGED.sendTo(
					player,
					new PlaceHolder("dimension", dimension.toString()), 
					new PlaceHolder("number", newValue));
		}
		
		return true;
	}
}
