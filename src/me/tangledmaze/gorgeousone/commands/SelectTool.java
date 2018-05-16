package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.shapes.Brush;
import me.tangledmaze.gorgeousone.shapes.Ellipse;
import me.tangledmaze.gorgeousone.shapes.Rectangle;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.shapes.ExitSetter;

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
			if(sHandler.getSelectionType(p) == Rectangle.class)
				break;
			
			sHandler.setSelectionType(p, Rectangle.class);
			p.sendMessage(Constants.prefix + "Changed selection type to rectangular.");

			if(sHandler.hasSelection(p) && !sHandler.getSelection(p).isComplete())
				sHandler.discardSelection(p);
			break;
			
		case "ellipse":
		case "circle":
			if(sHandler.getSelectionType(p) == Ellipse.class)
				break;
			
			sHandler.setSelectionType(p, Ellipse.class);
			p.sendMessage(Constants.prefix + "Changed selection type to elliptical.");

			if(sHandler.hasSelection(p) && !sHandler.getSelection(p).isComplete())
				sHandler.discardSelection(p);
			break;
		
		case "brush":
			if(sHandler.getSelectionType(p) == Brush.class)
				break;
			
			if(mHandler.hasMaze(p)) {
				sHandler.setSelectionType(p, Brush.class);
				p.sendMessage(Constants.prefix + "Changed selection type to brush.");
				
				if(sHandler.hasSelection(p))
					sHandler.discardSelection(p);
			
			}else {
				p.sendMessage(Constants.prefix + "The brush tool can only be used on mazes.");
				p.sendMessage("/tangledmaze start");
			}
			break;
			
		case "exit":
			if(sHandler.getSelectionType(p) == ExitSetter.class)
				break;
			
			if(mHandler.hasMaze(p)) {
				sHandler.setSelectionType(p, ExitSetter.class);
				p.sendMessage(Constants.prefix + "Changed selection type to exit setter.");
				
				if(sHandler.hasSelection(p))
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