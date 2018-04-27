package me.tangledmaze.gorgeousone.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
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
				sHandler.deselectSelection(p);
			break;
			
		case "ellipse":
		case "circle":
			if(sHandler.getSelectionType(p) == Ellipse.class)
				break;
			
			sHandler.setSelectionType(p, Ellipse.class);
			p.sendMessage(Constants.prefix + "Changed selection type to elliptical.");

			if(sHandler.hasSelection(p) && !sHandler.getSelection(p).isComplete())
				sHandler.deselectSelection(p);
			break;
		
		case "brush":
			if(sHandler.getSelectionType(p) == Brush.class)
				break;
			
			if(mHandler.hasMaze(p)) {
				sHandler.setSelectionType(p, Brush.class);
				p.sendMessage(Constants.prefix + "Changed selection type to brush.");
				
				if(sHandler.hasSelection(p))
					sHandler.deselectSelection(p);
			
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
					sHandler.deselectSelection(p);
			
			}else {
				p.sendMessage(Constants.prefix + "The exit setting tool can only be used on mazes.");
				p.sendMessage("/tangledmaze start");
			}
			break;
			
		default:
			sendSelectionHelp(p);
			break;
		}
	}
	
	public void sendSelectionHelp(Player p) {
		p.sendMessage(Constants.prefix + "With this command you can only choose between the following tool types:");
		
		p.sendMessage(ChatColor.DARK_GREEN + "rectangle");
		p.sendMessage(ChatColor.GREEN + "Your selections set with a selection wand will form rectangles.");
		
		p.sendMessage(ChatColor.DARK_GREEN + "ellipse");
		p.sendMessage(ChatColor.GREEN + "Your selections will form ellipses.");
		
		p.sendMessage(ChatColor.DARK_GREEN + "brush");
		p.sendMessage(ChatColor.GREEN + "By clicking on a maze's border with this tool you can brush away the border at that block.");
		
		p.sendMessage(ChatColor.DARK_GREEN + "exit");
		p.sendMessage(ChatColor.GREEN + "By clicking on a maze's border you can select exits, where gaps will be left when building the maze.");
		
		p.sendMessage("/tangledmaze select <tool type>");
	}
}
