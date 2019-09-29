package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commandapi.argument.ArgType;
import me.gorgeousone.tangledmaze.commandapi.argument.ArgValue;
import me.gorgeousone.tangledmaze.commandapi.argument.Argument;
import me.gorgeousone.tangledmaze.commandapi.command.ArgCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.MazeDimension;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import me.gorgeousone.tangledmaze.util.Utils;

public class SetDimension extends ArgCommand {
	
	public SetDimension(MazeCommand mazeCommand) {
		super("set", null, mazeCommand);

		addArg(new Argument("dimension", ArgType.STRING, MazeDimension.getCommandNames()));
		addArg(new Argument("integer", ArgType.INTEGER));
	}
	
	@Override
	protected boolean onExecute(CommandSender sender, ArgValue[] args) {
		
		Player player = (Player) sender;
		
		MazeDimension dimension = MazeDimension.match(args[0].getString());
		
		if(dimension == null) {
			Messages.ERROR_INVALID_DIMENSION.sendTo(player, new PlaceHolder("dimension", args[0].toString()));
			return false;
		}
		
		String stringDimValue = args[1].getString();
		int newDimValue;
		
		try {
			newDimValue = Utils.limit(Integer.parseInt(stringDimValue), 1, dimension.getMaxValue());
			
		} catch (NumberFormatException ex) {
			
			Messages.ERROR_INVALID_NUMBER.sendTo(player, new PlaceHolder("number", stringDimValue));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getDimension(dimension) != newDimValue) {
			maze.setDimension(dimension, newDimValue);
		
			Messages.MESSAGE_DIMENSION_CHANGED.sendTo(
					player,
					new PlaceHolder("dimension", dimension.toString()), 
					new PlaceHolder("number", newDimValue));
		}
		
		return true;
	}
}
