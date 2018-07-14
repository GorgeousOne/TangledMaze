package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.selections.ToolType;
import me.tangledmaze.gorgeousone.utils.Constants;

public class SelectTool {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public SelectTool() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p, String selectionType) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		switch (selectionType.toLowerCase()) {
		case "rectangle":
		case "rect":
		case "square":
			if(sHandler.getSelectionType(p) == ToolType.RECTANGLE)
				break;
			
			sHandler.setSelectionType(p, ToolType.RECTANGLE);
			p.sendMessage(Constants.prefix + "Changed selection type to rectangular.");

			if(sHandler.hasShapeSelection(p) && !sHandler.getSelection(p).isComplete())
				sHandler.discardSelection(p);
			break;
			
		case "ellipse":
		case "circle":
			if(sHandler.getSelectionType(p) == ToolType.ELLIPSE )
				break;
			
			sHandler.setSelectionType(p, ToolType.ELLIPSE);
			p.sendMessage(Constants.prefix + "Changed selection type to elliptical.");

			if(sHandler.hasShapeSelection(p) && !sHandler.getSelection(p).isComplete())
				sHandler.discardSelection(p);
			break;
		
		case "brush":
			if(sHandler.getSelectionType(p) == ToolType.BRUSH)
				break;
			
			if(mHandler.hasMaze(p)) {
				sHandler.setSelectionType(p, ToolType.BRUSH);
				p.sendMessage(Constants.prefix + "Changed selection type to brush.");
				
				if(sHandler.hasShapeSelection(p))
					sHandler.discardSelection(p);
			
			}else {
				p.sendMessage(Constants.prefix + "The brush tool can only be used on mazes.");
				p.sendMessage("/tangledmaze start");
			}
			break;
			
		case "exit":
			if(sHandler.getSelectionType(p) == ToolType.EXIT_SETTER)
				break;
			
			if(mHandler.hasMaze(p)) {
				sHandler.setSelectionType(p, ToolType.EXIT_SETTER);
				p.sendMessage(Constants.prefix + "Changed selection type to exit setter.");
				
				if(sHandler.hasShapeSelection(p))
					sHandler.discardSelection(p);
			
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