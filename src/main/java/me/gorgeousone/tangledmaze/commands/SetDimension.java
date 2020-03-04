package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.argument.ArgType;
import me.gorgeousone.cmdframework.argument.ArgValue;
import me.gorgeousone.cmdframework.argument.Argument;
import me.gorgeousone.cmdframework.command.ArgCommand;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.messages.PlaceHolder;
import me.gorgeousone.tangledmaze.utils.MathHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetDimension extends ArgCommand {
	
	private MazeHandler mazeHandler;
	
	public SetDimension(MazeCommand mazeCommand, MazeHandler mazeHandler) {
		super("set", null, true, mazeCommand);
		
		addArg(new Argument("dimension", ArgType.STRING, MazeDimension.getCommandNames()));
		addArg(new Argument("integer", ArgType.INTEGER));
		
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	protected boolean onCommand(CommandSender sender, ArgValue[] arguments) {
		
		Player player = (Player) sender;
		MazeDimension dimension = MazeDimension.match(arguments[0].getString());
		
		if (dimension == null) {
			Messages.ERROR_INVALID_DIMENSION.sendTo(player, new PlaceHolder("dimension", arguments[0].getString()));
			return false;
		}
		
		int newDimValue = MathHelper.clamp(arguments[1].getInt(), 1, dimension.getMaxValue());
		
		Maze maze = mazeHandler.getMaze(player);
		
		if (maze.getDimension(dimension) != newDimValue) {
			maze.setDimension(dimension, newDimValue);
			
			Messages.MESSAGE_DIMENSION_CHANGED.sendTo(
					player,
					new PlaceHolder("dimension", dimension.commandName()),
					new PlaceHolder("number", newDimValue));
		}
		
		return true;
	}
}
