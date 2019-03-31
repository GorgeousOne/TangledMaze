package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.shape.Shape;
import me.gorgeousone.tangledmaze.tool.*;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SelectTool extends MazeCommand {

	public SelectTool() {
		super("select", "/tangledmaze select <tool>", 1, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
	
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		String toolType = arguments[0];
		
		switch (toolType.toLowerCase()) {
		case "rect":
		case "rectangle":
		case "square":
			
			if(!switchClipShape(player, Shape.RECT))
				return true;
			
			break;
			
		case "circle":
		case "ellipse":
			
			if(!switchClipShape(player, Shape.CIRCLE))
				return true;
			
			break;
		
		case "brush":
			
			if(!switchMazeTool(player, new BrushTool(player)))
				return true;
			
			break;
			
		case "exit":
		case "entrance":
			
			if(!switchMazeTool(player, new ExitSettingTool(player)))
				return true;
			
			break;
			
		default:
			player.sendMessage("/tangledmaze help 6");
			return false;
		}
		
		Messages.MESSAGE_TOOL_SWITCHED.send(player, new PlaceHolder("tool", ToolHandler.getTool(player).getName()));
		return true;
	}
	
	private boolean switchClipShape(Player player, Shape type) {
		
		if(!ToolHandler.hasClipboard(player)) {
			ToolHandler.setTool(player, new ClippingTool(player, type));
			return true;
		}
		
		ClippingTool clip = ToolHandler.getClipboard(player);
		
		if(clip.getType().getClass().equals(type.getClass())) {
			return false;
		}
		
		clip.setType(type);
		return true;
	}
	
	private boolean switchMazeTool(Player player, Tool type) {

		if(ToolHandler.getTool(player).getClass().equals(type.getClass()))
			return false;
		
		if(!MazeHandler.getMaze(player).isStarted()) {
			Messages.MESSAGE_TOOL_FOR_MAZE_ONLY.send(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(ToolHandler.hasClipboard(player)) {
			ToolHandler.getClipboard(player).reset();
		}
		
		ToolHandler.setTool(player, type);
		return true;
	}
}