package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

public class StartMaze extends MazeCommand {
	
	public StartMaze() {
		super("start", "/tangledmaze start", 0, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;
		
		ClippingTool clipboard = getCompletedClipboard(player);
		
		if(clipboard == null)
			return false;
		
		Renderer.hideClipboard(clipboard, false);
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.isConstructed()) {

			maze = new Maze(player).setClip(clipboard.getClip());
			MazeHandler.setMaze(player, maze);
			
		}else {
			Renderer.hideMaze(maze);
			maze.setClip(clipboard.getClip());
		}
		
		Renderer.displayMaze(maze);
		clipboard.reset();
		return true;
	}
}