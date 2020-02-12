package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commands.framework.argument.ArgType;
import me.gorgeousone.tangledmaze.commands.framework.argument.ArgValue;
import me.gorgeousone.tangledmaze.commands.framework.argument.Argument;
import me.gorgeousone.tangledmaze.commands.framework.command.ArgCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import me.gorgeousone.tangledmaze.tools.*;
import me.gorgeousone.tangledmaze.utils.PlaceHolder;

public class SelectTool extends ArgCommand {

	private ToolHandler toolHandler;
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	
	public SelectTool(MazeCommand parent, ClipToolHandler clipHandler, ToolHandler toolHandler, MazeHandler mazeHandler) {

		super("select", null, true, parent);
		addArg(new Argument("tool", ArgType.STRING, "rect", "circle", "brush", "exit"));

		this.clipHandler = clipHandler;
		this.toolHandler = toolHandler;
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	protected boolean onCommand(CommandSender sender, ArgValue[] arguments) {

		Player player = (Player) sender;
		String toolType = arguments[0].getString();

		switch (toolType.toLowerCase()) {

			case "rect":
			case "rectangle":
			case "square":

				if(!clipHandler.setClipShape(player, ClipShape.RECTANGLE))
					return true;
				break;

			case "circle":
			case "ellipse":

				if(!clipHandler.setClipShape(player, ClipShape.ELLIPSE))
					return true;
				break;

			case "brush":

				if(!switchToMazeTool(player, new BrushTool(player, mazeHandler)))
					return true;
				break;

			case "exit":
			case "entrance":

				if(!switchToMazeTool(player, new ExitSettingTool(player, mazeHandler)))
					return true;
				break;

			default:
				player.sendMessage("/tangledmaze help 6");
				return false;
		}

		//TODO circles wont be messaged correctly because the tool is still a rectangle.
		Messages.MESSAGE_TOOL_SWITCHED.sendTo(player, new PlaceHolder("tool", toolHandler.getTool(player).getName()));
		return true;
	}

	private boolean switchToMazeTool(Player player, Tool type) {

		if(toolHandler.getTool(player).getClass().equals(type.getClass()))
			return false;
		
		Maze maze = mazeHandler.getMaze(player);
		
		if(!maze.isStarted()) {
			Messages.MESSAGE_TOOL_FOR_MAZE_ONLY.sendTo(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(maze.isConstructed()) {
			Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
			return false;
		}
		
		if(clipHandler.hasClipTool(player)) {
			//TODO make cliphandler handle rendering of cliptool
			ClipTool clipboard = clipHandler.getClipTool(player);
			Renderer.hideClipboard(clipboard, true);
			clipHandler.removeClipTool(player);
		}
		
		toolHandler.setTool(player, type);
		return true;
	}
}