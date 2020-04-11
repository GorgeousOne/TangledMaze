package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.argument.ArgType;
import me.gorgeousone.cmdframework.argument.ArgValue;
import me.gorgeousone.cmdframework.argument.Argument;
import me.gorgeousone.cmdframework.command.ArgCommand;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.generation.MazePart;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.messages.PlaceHolder;
import org.bukkit.command.CommandSender;

public class UnbuildMaze extends ArgCommand {
	
	private MazeHandler mazeHandler;
	private BuildHandler buildHandler;
	
	public UnbuildMaze(MazeCommand mazeCommand, MazeHandler mazeHandler, BuildHandler buildHandler) {
		super("unbuild", null, false, mazeCommand);
		addArg(new Argument("part", ArgType.STRING, "maze", "floor", "roof").setDefaultTo("maze"));
		
		this.mazeHandler = mazeHandler;
		this.buildHandler = buildHandler;
	}
	
	@Override
	protected void onCommand(CommandSender sender, ArgValue[] arguments) {
		
		Maze maze = mazeHandler.getMaze(sender);
		
		if (!maze.isConstructed()) {
			Messages.MESSAGE_NO_MAZE_TO_UNBUILD.sendTo(sender);
			return;
		}
		
		String mazePart = arguments[0].getString();
		
		switch (mazePart) {
			
			case "floor":
				buildHandler.unbuildMazePart(maze, MazePart.FLOOR, null);
				break;
			
			case "roof":
				buildHandler.unbuildMazePart(maze, MazePart.ROOF, null);
				break;
			
			case "maze":
			case "walls":
				Messages.MESSAGE_MAZE_UNBUILDING_STARTED.sendTo(sender);
				buildHandler.unbuildMazePart(maze, MazePart.FLOOR, null);
				buildHandler.unbuildMazePart(maze, MazePart.ROOF, null);
				buildHandler.unbuildMazePart(maze, MazePart.WALLS, sender);
				break;
			
			default:
				Messages.ERROR_INVALID_MAZE_PART.sendTo(sender, new PlaceHolder("mazepart", mazePart));
				break;
		}
	}
}