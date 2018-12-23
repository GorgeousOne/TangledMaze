package me.gorgeousone.tangledmaze.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.tools.ToolHandler;
import me.gorgeousone.tangledmaze.utils.Constants;
import me.gorgeousone.tangledmaze.utils.MaterialDataSerializer;

public class BuildMaze {

	public void execute(Player p, ArrayList<String> serializedMaterialData) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(!maze.isStarted()) {
			
			if(!ToolHandler.hasClipboard(p)) {
				p.sendMessage(ChatColor.RED + "Please select an area with a selection wand first.");
				p.sendMessage("/tangledmaze wand");
				return;	
			}
			
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		if(maze.getClip().size() == maze.getClip().borderSize()) {
			p.sendMessage(Constants.prefix + "What!? This maze only consists of border, it will not be built.");
			return;
		}
		
		if(maze.getExits().isEmpty()) {
			p.sendMessage(Constants.prefix + "Please mark (at least) one exit at the border where the algorithm can start building.");
			p.sendMessage("/tangledmaze select exit");
			return;
		}
		
		if(serializedMaterialData.isEmpty()) {
			p.sendMessage(ChatColor.RED + "Please specify (at least) one block type this maze should be built out of.");
			p.sendMessage("/tangledmaze build <block type 1> ... <block type n>");
			return;
		}
		
		ArrayList<MaterialData> composition;
		
		try {
			composition = getWallComposition(serializedMaterialData);
		} catch (Exception e) {
			p.sendMessage(e.getMessage());
			return;
		}
		
		maze.setWallComposition(composition);
		MazeHandler.buildMaze(maze);
		p.sendMessage(Constants.prefix + "Started building your maze.");
		
		ToolHandler.resetToDefaultTool(p);
		maze.reset();
	}
	
	private static ArrayList<MaterialData> getWallComposition(ArrayList<String> serializedMaterialData) {
		ArrayList<MaterialData> composition = new ArrayList<>();
		
		for(String materialData : serializedMaterialData) {
			composition.add(MaterialDataSerializer.deserializeMaterialData(materialData));
		}
		
		return composition;
	}
}