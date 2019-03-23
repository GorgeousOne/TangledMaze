package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;

public class DiscardMaze extends MazeCommand {
	
	public DiscardMaze() {
		super("discard", "/tangledmaze discard", 0, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {

		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		MazeHandler.getMaze(player).reset();
		ToolHandler.resetToDefaultTool(player);
		return true;
	}
}