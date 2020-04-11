package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscardMaze extends BasicCommand {
	
	private ToolHandler toolHandler;
	private MazeHandler mazeHandler;
	
	public DiscardMaze(MazeCommand mazeCommand, ToolHandler toolHandler, MazeHandler mazeHandler) {
		super("discard", null, true, mazeCommand);
		
		this.toolHandler = toolHandler;
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		
		mazeHandler.removeMaze(player);
		toolHandler.resetToDefaultTool(player);
		return;
	}
}