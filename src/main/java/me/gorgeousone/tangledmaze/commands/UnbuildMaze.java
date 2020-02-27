package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.argument.ArgType;
import me.gorgeousone.cmdframework.argument.ArgValue;
import me.gorgeousone.cmdframework.argument.Argument;
import me.gorgeousone.cmdframework.command.ArgCommand;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.generation.MazePart;
import me.gorgeousone.tangledmaze.utils.PlaceHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnbuildMaze extends ArgCommand {
	
	private MazeHandler mazeHandler;
	private BuildHandler buildHandler;
	
	public UnbuildMaze(MazeCommand mazeCommand, MazeHandler mazeHandler, BuildHandler buildHandler) {
		super("unbuild", null, true, mazeCommand);
		addArg(new Argument("part", ArgType.STRING, "maze", "floor", "roof").setDefaultTo("maze"));
		
		this.mazeHandler = mazeHandler;
		this.buildHandler = buildHandler;
	}
	
	@Override
	protected boolean onCommand(CommandSender sender, ArgValue[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = mazeHandler.getMaze(player);
		
		if (!maze.isConstructed()) {
			Messages.MESSAGE_NO_MAZE_TO_UNBUILD.sendTo(player);
			return true;
		}
		
		String mazePart = arguments[0].getString();
		
		switch (mazePart) {
			
			case "floor":
				buildHandler.unbuildMazePart(maze, MazePart.FLOOR);
				break;
			
			case "roof":
				buildHandler.unbuildMazePart(maze, MazePart.ROOF);
				break;
			
			case "maze":
			case "walls":
				Messages.MESSAGE_MAZE_UNBUILDING_STARTED.sendTo(player);
				buildHandler.unbuildMazePart(maze, MazePart.FLOOR);
				buildHandler.unbuildMazePart(maze, MazePart.ROOF);
				buildHandler.unbuildMazePart(maze, MazePart.WALLS);
				break;
			
			default:
				Messages.ERROR_INVALID_MAZE_PART.sendTo(player, new PlaceHolder("mazepart", mazePart));
				break;
		}
		return true;
	}
}