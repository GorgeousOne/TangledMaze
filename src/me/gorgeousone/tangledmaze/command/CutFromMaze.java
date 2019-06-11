package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

public class CutFromMaze extends MazeCommand {

	public CutFromMaze() {
		super("cut", "/tangledmaze cut", 0, true, null, "remove");
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;
		
		if(!MazeHandler.getMaze(player).isStarted()) {
			
			Messages.ERROR_MAZE_NOT_STARTED.send(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(!ToolHandler.hasClipboard(player) || !ToolHandler.getClipboard(player).isStarted()) {
			
			Messages.ERROR_CLIPBOARD_NOT_STARTED.send(player);
			player.sendMessage("/tangledmaze wand");
			return false;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(player);

		if(!clipboard.isComplete()) {
			
			Messages.ERROR_CLIPBOARD_NOT_FINISHED.send(player);
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		ClipAction action = maze.getDeletion(clipboard.getClip());

		Renderer.hideClipboard(clipboard, true);
		clipboard.reset();

		if(action == null)
			return false;

		maze.processAction(action, true);
		return true;
	}
}