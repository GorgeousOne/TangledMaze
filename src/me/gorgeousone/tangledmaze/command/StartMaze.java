package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

public class StartMaze extends MazeCommand {
	
	public StartMaze() {
		super("start", "/tangledmaze start", 0, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		if(!ToolHandler.hasClipboard(player) || ToolHandler.getClipboard(player).getVertices().isEmpty()) {
			Messages.ERROR_CLIPBOARD_NOT_STARTED.send(player);
			player.sendMessage("/tangledmaze wand");
			return false;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(player);
		
		if(!clipboard.isComplete()) {
			Messages.ERROR_CLIPBOARD_NOT_FINISHED.send(player);
			return false;
		}
		
		Renderer.hideClipboard(clipboard, false);

		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.isConstructed()) {
			maze = new Maze(player).setClip(clipboard.getClip());
			MazeHandler.setMaze(player, maze);
			
		}else
			maze.setClip(clipboard.getClip());
		
		clipboard.reset();
		return true;
	}
}