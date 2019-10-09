package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.command.api.command.BasicCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;

public class UndoCommand extends BasicCommand {

	public UndoCommand(MazeCommand mazeCommand) {
		super("undo", null, mazeCommand);
	}
	
	@Override
	public boolean onExecute(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = MazeHandler.getMaze(player);

		if(!maze.isStarted() || maze.isConstructed()) {
			
			Messages.ERROR_MAZE_NOT_STARTED.sendTo(player);
			return false;
		}
		
		
		if(maze.getActionHistory().isEmpty())
			return false;

		ClipAction action = maze.getActionHistory().popLastAction().invert();
		maze.processAction(action, false);
		Renderer.displayMazeAction(maze, action);
		return true;
	}
}