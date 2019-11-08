package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.shape.ClipShape;
import me.gorgeousone.tangledmaze.command.framework.argument.ArgType;
import me.gorgeousone.tangledmaze.command.framework.argument.ArgValue;
import me.gorgeousone.tangledmaze.command.framework.argument.Argument;
import me.gorgeousone.tangledmaze.command.framework.command.ArgCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.*;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SelectTool extends ArgCommand {

	public SelectTool(MazeCommand mazeCommand) {
		super("select", null, mazeCommand);
		addArg(new Argument("tool", ArgType.STRING, "rect", "circle", "brush", "exit"));
	}
	
	@Override
	protected boolean onExecute(CommandSender sender, ArgValue[] arguments) {

		Player player = (Player) sender;
		String toolType = arguments[0].getString();

		switch (toolType.toLowerCase()) {

			case "rect":
			case "rectangle":
			case "square":

				if(!switchToClipShape(player, ClipShape.RECT))
					return true;
				break;

			case "circle":
			case "ellipse":

				if(!switchToClipShape(player, ClipShape.CIRCLE))
					return true;
				break;

			case "brush":

				if(!switchToMazeTool(player, new BrushTool(player)))
					return true;
				break;

			case "exit":
			case "entrance":

				if(!switchToMazeTool(player, new ExitSettingTool(player)))
					return true;
				break;

			default:
				player.sendMessage("/tangledmaze help 6");
				return false;
		}

		Messages.MESSAGE_TOOL_SWITCHED.sendTo(player, new PlaceHolder("tool", ToolHandler.getTool(player).getName()));
		return true;
	}

	private boolean switchToClipShape(Player player, ClipShape type) {
		
		if(!ToolHandler.hasClipboard(player)) {
			ToolHandler.setTool(player, new ClippingTool(player, type));
			return true;
		}
		
		ClippingTool clip = ToolHandler.getClipboard(player);
		
		if(clip.getType().getClass().equals(type.getClass()))
			return false;

		clip.setType(type);
		return true;
	}
	
	private boolean switchToMazeTool(Player player, Tool type) {

		if(ToolHandler.getTool(player).getClass().equals(type.getClass()))
			return false;
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isStarted()) {
			Messages.MESSAGE_TOOL_FOR_MAZE_ONLY.sendTo(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(maze.isConstructed()) {
			Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
			return false;
		}
		
		if(ToolHandler.hasClipboard(player)) {
			ClippingTool clipboard = ToolHandler.getClipboard(player);
			Renderer.hideClipboard(clipboard, true);
			clipboard.reset();
		}
		
		ToolHandler.setTool(player, type);
		return true;
	}
}