package me.gorgeousone.tangledmaze.command;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.shape.Shape;
import me.gorgeousone.tangledmaze.tool.*;
import me.gorgeousone.tangledmaze.util.Constants;

public class SelectTool {

	public void execute(Player p, String toolType) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		switch (toolType.toLowerCase()) {
		case "rect":
		case "rectangle":
		case "square":
			
			try {
				setClipShape(p, Shape.RECT);
				p.sendMessage(Constants.prefix + "Changed tool rectangle.");
			} catch (Exception e) {}
			
			break;
			
		case "circle":
		case "ellipse":
			
			try {
				setClipShape(p, Shape.CIRCLE);
				p.sendMessage(Constants.prefix + "Changed tool to circle.");
			} catch (Exception e) {}
			
			break;
		
		case "brush":
			
			try {
				setMazeTool(p, new BrushTool(p));
				p.sendMessage(Constants.prefix + "Changed tool to brush.");
			} catch (Exception e) {}
			
			break;
			
		case "exit":
		case "entrance":
			
			try {
				setMazeTool(p, new ExitSettingTool(p));
				p.sendMessage(Constants.prefix + "Changed tool to exit setter.");
			} catch (Exception e) {}
			
			break;
			
		default:
			p.sendMessage("/tangledmaze help 5");
			break;
		}
	}
	
	private void setClipShape(Player p, Shape type) {
		
		if(!ToolHandler.hasClipboard(p)) {
			ToolHandler.setTool(p, new ClippingTool(p, type));
			return;
		}
		
		ClippingTool clip = ToolHandler.getClipboard(p);
		
		if(clip.getType().getClass().equals(type.getClass()))
			throw new IllegalStateException("Tool is already selected.");

		clip.setType(type);
	}
	
	private boolean setMazeTool(Player p, Tool type) {

		if(ToolHandler.getTool(p).getClass().equals(type.getClass()))
			return false;
		
		if(!MazeHandler.getMaze(p).isStarted()) {
			p.sendMessage(Constants.prefix + "This tool can only be used on a maze's ground plot.");
			p.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(ToolHandler.hasClipboard(p))
			ToolHandler.getClipboard(p).reset();
			
		ToolHandler.setTool(p, type);
		return true;
	}
}