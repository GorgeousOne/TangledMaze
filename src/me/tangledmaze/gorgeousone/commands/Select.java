package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.shapes.Brush;
import me.tangledmaze.gorgeousone.shapes.Ellipse;
import me.tangledmaze.gorgeousone.shapes.Rectangle;
import me.tangledmaze.gorgeousone.shapes.EntranceSetter;

public class Select {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public Select() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p, String selectionType) {
		
		switch (selectionType.toLowerCase()) {
		case "rectangle":
		case "rect":
		case "square":
			if(sHandler.getSelectionType(p) == Rectangle.class)
				break;
			
			sHandler.setSelectionType(p, Rectangle.class);
			p.sendMessage(Constants.prefix + "Changed selection type to rectangular.");

			if(sHandler.hasSelection(p) && !sHandler.getSelection(p).isComplete())
				sHandler.deselect(p);
			break;
			
		case "ellipse":
		case "circle":
			if(sHandler.getSelectionType(p) == Ellipse.class)
				break;
			
			sHandler.setSelectionType(p, Ellipse.class);
			p.sendMessage(Constants.prefix + "Changed selection type to elliptical.");

			if(sHandler.hasSelection(p) && !sHandler.getSelection(p).isComplete())
				sHandler.deselect(p);
			break;
		
		case "brush":
			if(sHandler.getSelectionType(p) == Brush.class)
				break;
			
			if(mHandler.hasMaze(p)) {
				sHandler.setSelectionType(p, Brush.class);
				p.sendMessage(Constants.prefix + "Changed selection type to brush.");
				
				if(sHandler.hasSelection(p))
					sHandler.deselect(p);
			
			}else {
				p.sendMessage(Constants.prefix + "The brush tool can only be used on mazes.");
				p.sendMessage("/tangledmaze start");
			}
			break;
			
		case "entrance":
			if(sHandler.getSelectionType(p) == EntranceSetter.class)
				break;
			
			if(mHandler.hasMaze(p)) {
				sHandler.setSelectionType(p, EntranceSetter.class);
				p.sendMessage(Constants.prefix + "Changed selection type to start setter.");
				
				if(sHandler.hasSelection(p))
					sHandler.deselect(p);
			
			}else {
				p.sendMessage(Constants.prefix + "The start setting tool can only be used on mazes.");
				p.sendMessage("/tangledmaze start");
			}
			break;
			
		default:
			sendSelectionHelp(p);
			break;
		}
	}
	
	public void sendSelectionHelp(Player p) {
		p.sendMessage("yo mom gay");
	}
}
