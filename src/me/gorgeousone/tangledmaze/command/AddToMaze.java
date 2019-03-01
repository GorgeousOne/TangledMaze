package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.Messages;

public class AddToMaze extends MazeCommand {

	public AddToMaze() {
		super("add", "/maze add", 0, true, null, "merge");
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		if(!MazeHandler.getMaze(player).isStarted()) {
			Messages.ERROR_MAZE_NOT_STARTED.send(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(!ToolHandler.hasClipboard(player) || !ToolHandler.getClipboard(player).isStarted()) {
			Messages.ERROR_CLIPBOARD_NOT_STARTED.send(player);
			player.sendMessage("/tangledmaze select rectangle/ellipse");
			return false;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(player);
		
		if(!clipboard.isComplete()) {
			Messages.ERROR_CLIPBOARD_NOT_FINISHED.send(player);
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		ClipAction action = maze.getAddition(clipboard.getClip());

		if(action == null) {
			return false;
		}
		
		if(action.getAddedFill().size() == clipboard.getClip().size()) {
			Messages.ERROR_CLIPBOARD_NOT_TOUCHING_MAZE.send(player);
			return false;
		}

		clipboard.reset();
		maze.processAction(action, true);
		return true;
	}
	
	public void execute(Player player) {
		
		if(!player.hasPermission(Constants.BUILD_PERM)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!MazeHandler.getMaze(player).isStarted()) {
			Messages.ERROR_MAZE_NOT_STARTED.send(player);
			player.sendMessage("/tangledmaze start");
			return;
		}
		
		if(!ToolHandler.hasClipboard(player) || !ToolHandler.getClipboard(player).isStarted()) {
			Messages.ERROR_CLIPBOARD_NOT_STARTED.send(player);
			player.sendMessage("/tangledmaze select rectangle/ellipse");
			return;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(player);
		
		if(!clipboard.isComplete()) {
			Messages.ERROR_CLIPBOARD_NOT_FINISHED.send(player);
			return;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		ClipAction action = maze.getAddition(clipboard.getClip());

		if(action == null) {
			return;
		}
		
		if(action.getAddedFill().size() == clipboard.getClip().size()) {
			Messages.ERROR_CLIPBOARD_NOT_TOUCHING_MAZE.send(player);
			return;
		}

		clipboard.reset();
		maze.processAction(action, true);
	}
}