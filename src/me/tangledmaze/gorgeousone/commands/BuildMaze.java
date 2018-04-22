package me.tangledmaze.gorgeousone.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeBuilder;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.shapes.Brush;
import me.tangledmaze.gorgeousone.shapes.ExitSetter;
import me.tangledmaze.gorgeousone.shapes.Rectangle;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Entry;
import me.tangledmaze.gorgeousone.utils.NMSProvider;

public class BuildMaze {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	private MazeBuilder mBuilder;
	
	public BuildMaze() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		mBuilder  = TangledMain.getPlugin().getMazeBuilder();
	}
	
	public void execute(Player p, ArrayList<String> blockTypes) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!mHandler.hasMaze(p)) {
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		Maze maze = mHandler.getMaze(p);
		
		if(maze.getExits().isEmpty()) {
			p.sendMessage(Constants.prefix + "This plugin is not smart enough to choose a start point for the algorithm. " + 
											 "Could you be so nice and mark at least one exit at the border?");
			p.sendMessage("/tangledmaze select exit");
			return;
		}
		
		//----------------------------------------------------------------------------------------------------------------
		
		if(blockTypes.isEmpty()) {
			p.sendMessage(ChatColor.RED + "Please specify at least one block this maze should be built of.");
			p.sendMessage("/tangledmaze build <block type 1> ... <block type n>");
			return;
		}
		
		ArrayList<Entry<Material, Byte>> composition = new ArrayList<>();
		
		for(String blockType : blockTypes) {
			Material material;
			byte data = 0;
			
			if(blockType.contains(":")) {
				material = NMSProvider.getMaterial(blockType.split(":")[0]);
				
				try {
					data = Byte.parseByte(blockType.split(":")[1]);
				} catch (NumberFormatException e) {
					p.sendMessage(ChatColor.RED + "\"" + blockType + "\" is wierd.");
					return;
				}
				
			}else
				material = NMSProvider.getMaterial(blockType);
			
			
			if(material == Material.AIR && !blockType.equalsIgnoreCase("air")) {
				p.sendMessage(ChatColor.RED + "\"" + blockType + "\" does not match any block type.");
				return;
			}
			
			composition.add(new Entry<Material, Byte>(material, data));
		}
		
		//----------------------------------------------------------------------------------------------------------------
		
		mHandler.getMaze(p).setWallComposition(composition);
		mHandler.deselctMaze(p);
		
		int queuePosition = mBuilder.enqueueMaze(maze);

		if(queuePosition != 0) {
			p.sendMessage(Constants.prefix + "Your maze has been queued. Position in queue: " + queuePosition);
			p.sendMessage(Constants.prefix + "If you leave the server before it gets built your work will be discarded!");
		}
		
		if(sHandler.getSelectionType(p) == Brush.class ||
		   sHandler.getSelectionType(p) == ExitSetter.class)
			sHandler.setSelectionType(p, Rectangle.class);
	}
}