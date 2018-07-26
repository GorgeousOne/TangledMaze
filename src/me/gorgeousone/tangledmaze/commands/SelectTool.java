package me.gorgeousone.tangledmaze.commands;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Constants;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.selections.*;
import me.gorgeousone.tangledmaze.shapes.*;

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
			if(SelectionHandler.hasShapeSel(p) && SelectionHandler.getShapeSel(p).getShape() instanceof Rectangle)
				break;
			
			SelectionHandler.resetSelection(p);
			SelectionHandler.setSelection(p, new ShapeSelection(p, new Rectangle()));
			p.sendMessage(Constants.prefix + "Changed selection type to rectangular.");

			break;
			
		case "ellipse":
		case "circle":
			if(SelectionHandler.hasShapeSel(p) && ((ShapeSelection) SelectionHandler.getSelection(p)).getShape().getClass() == Rectangle.class)
				break;
			
			SelectionHandler.resetSelection(p);
			SelectionHandler.setSelection(p, new ShapeSelection(p, new Ellipse()));
			p.sendMessage(Constants.prefix + "Changed selection type to elliptical.");

			break;
		
		case "brush":
//			if(mHandler.getSelectionType(p) == ToolType.BRUSH)	TODO
//				break;
			
			if(MazeHandler.hasMaze(p)) {
				SelectionHandler.setSelection(p, new BrushSelection(p));
				p.sendMessage(Constants.prefix + "Changed selection type to brush.");
			
			}else {
				p.sendMessage(Constants.prefix + "The brush tool can only be used on mazes.");
				p.sendMessage("/tangledmaze start");
			}
			break;
			
		case "exit":
//			if(mHandler.getSelectionType(p) == ToolType.EXIT_SETTER)
//				break;	TODO
			
			if(MazeHandler.hasMaze(p)) {
				SelectionHandler.resetSelection(p);
				SelectionHandler.setSelection(p, new ExitSetterSelection(p));
				p.sendMessage(Constants.prefix + "Changed selection type to brush.");
			
			}else {
				p.sendMessage(Constants.prefix + "The exit setting tool can only be used on mazes.");
				p.sendMessage("/tangledmaze start");
			}
			break;
			
		default:
			p.sendMessage("/tangledmaze help 5");
			break;
		}
	}
}