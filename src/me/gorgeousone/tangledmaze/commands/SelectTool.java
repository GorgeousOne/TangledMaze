package me.gorgeousone.tangledmaze.commands;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.selections.*;
import me.gorgeousone.tangledmaze.shapes.*;
import me.gorgeousone.tangledmaze.utils.Constants;

public class SelectTool {

	public void execute(Player p, String selectionType) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		switch (selectionType.toLowerCase()) {
		case "rectangle":
		case "rect":
		case "square":
			
			setShapeSelection(p, Shape.RECT);
			p.sendMessage(Constants.prefix + "Changed selection type to rectangular.");
			break;
			
		case "ellipse":
		case "circle":
			
			if(setShapeSelection(p, Shape.ELLIPSE))
				p.sendMessage(Constants.prefix + "Changed selection type to elliptical.");
			break;
		
		case "brush":

			if(setMazeTool(p, new BrushSelection(p)))
				p.sendMessage(Constants.prefix + "Changed selection type to brush.");
			break;
			
		case "exit":
			
			if(setMazeTool(p, new ExitSetter(p)))
				p.sendMessage(Constants.prefix + "Changed selection type to exit setter.");
			break;
			
		default:
			p.sendMessage("/tangledmaze help 5");
			break;
		}
	}
	
	private boolean setShapeSelection(Player p, Shape type) {
		
		if(!SelectionHandler.hasShapeSel(p)) {
			SelectionHandler.setSelection(p, new ShapeSelection(p, type));
			return true;
		}
		
		ShapeSelection selection = SelectionHandler.getShapeSel(p);
		
		if(!selection.getType().getClass().equals(type.getClass())) {
			selection.setType(type);
			return true;
		}
	
		return false;
	}
	
	private boolean setMazeTool(Player p, Selection type) {

		if(SelectionHandler.getSelection(p).getClass().equals(type.getClass()))
			return false;
		
		if(!MazeHandler.getMaze(p).isStarted()) {
			p.sendMessage(Constants.prefix + "This tool can only be used on a maze's ground plot.");
			p.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(SelectionHandler.hasShapeSel(p))
			SelectionHandler.getShapeSel(p).reset();
			
		SelectionHandler.setSelection(p, type);
		return true;
	}
}