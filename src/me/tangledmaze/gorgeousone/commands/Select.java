package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.listener.SelectionHandler;
import me.tangledmaze.gorgeousone.main.TangledMain_go;
import me.tangledmaze.gorgeousone.shapes.Ellipse;
import me.tangledmaze.gorgeousone.shapes.Rectangle;

public class Select {

	private SelectionHandler sHandler;
	
	public Select(TangledMain_go plugin) {
		sHandler = plugin.getSelectionHandler();
	}
	
	public void execute(Player p, String selectionType) {
		
		switch (selectionType.toLowerCase()) {
		case "rectangle":
		case "rect":
			sHandler.setSelectionType(p, Rectangle.class);
			break;
			
		case "ellipse":
		case "circle":
			sHandler.setSelectionType(p, Ellipse.class);
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
