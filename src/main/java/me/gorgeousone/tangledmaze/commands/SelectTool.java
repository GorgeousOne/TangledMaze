package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.cmdframework.argument.ArgType;
import me.gorgeousone.cmdframework.argument.ArgValue;
import me.gorgeousone.cmdframework.argument.Argument;
import me.gorgeousone.cmdframework.command.ArgCommand;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import me.gorgeousone.tangledmaze.tools.MazeToolType;
import me.gorgeousone.tangledmaze.utils.PlaceHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectTool extends ArgCommand {

	private ToolHandler toolHandler;
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	private Renderer renderer;

	public SelectTool(MazeCommand parent, ClipToolHandler clipHandler, ToolHandler toolHandler, MazeHandler mazeHandler,
	                  Renderer renderer) {

		super("select", null, true, parent);
		addArg(new Argument("tool", ArgType.STRING, "rect", "circle", "brush", "exit"));

		this.clipHandler = clipHandler;
		this.toolHandler = toolHandler;
		this.mazeHandler = mazeHandler;
		this.renderer = renderer;
	}

	@Override
	protected boolean onCommand(CommandSender sender, ArgValue[] arguments) {

		Player player = (Player) sender;
		String toolType = arguments[0].getString();

		switch (toolType.toLowerCase()) {

			case "rect":
			case "rectangle":
			case "square":

				switchToClipTool(player, ClipShape.RECTANGLE);
				break;

			case "circle":
			case "ellipse":

				switchToClipTool(player, ClipShape.ELLIPSE);
				break;

			case "brush":

				switchToMazeTool(player, MazeToolType.BRUSH_TOOL);
				break;

			case "exit":
			case "entrance":

				switchToMazeTool(player, MazeToolType.EXIT_SETTER);
				break;

			default:
				player.sendMessage("/tangledmaze help 6");
				return false;
		}

		return true;
	}

	private void switchToClipTool(Player player, ClipShape newClipShape) {

		boolean switchedTool = toolHandler.setToolType(player, MazeToolType.CLIP_TOOL);
		boolean switchedClipShape = clipHandler.setClipShape(player, newClipShape);

		if (switchedTool || switchedClipShape)
			Messages.MESSAGE_TOOL_SWITCHED.sendTo(player, new PlaceHolder("tool", newClipShape.getSimpleName()));
	}

	private void switchToMazeTool(Player player, MazeToolType toolType) {
		
		//:p idk just in case
		if (toolType == MazeToolType.CLIP_TOOL)
			throw new IllegalArgumentException("Method is not designed to switch to " + toolType.name() + ".");
		
		if (!mazeHandler.hasStartedMaze(player)) {
			Messages.MESSAGE_TOOL_FOR_MAZE_ONLY.sendTo(player);
			player.sendMessage("/tangledmaze start");
			return;
		}
		
		if(toolHandler.setToolType(player, toolType))
			Messages.MESSAGE_TOOL_SWITCHED.sendTo(player, new PlaceHolder("tool", toolType.getSimpleName()));
	}
}