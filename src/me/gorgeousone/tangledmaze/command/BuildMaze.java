package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.MaterialDeserializer;

public class BuildMaze {

	public void execute(Player player, ArrayList<String> serializedMaterialData) {
		
		if(!player.hasPermission(Constants.buildPerm)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isStarted()) {
			
			if(!ToolHandler.hasClipboard(player)) {
				player.sendMessage(ChatColor.RED + "Please select an area with a selection wand first.");
				player.sendMessage("/tangledmaze wand");
				return;	
			}
			
			player.sendMessage(ChatColor.RED + "Please start a maze first.");
			player.sendMessage("/tangledmaze start");
			return;
		}
		
		if(maze.getClip().size() == maze.getClip().borderSize()) {
			player.sendMessage(Constants.prefix + "What!? This maze only consists of border, it will not be built.");
			return;
		}
		
		if(maze.getExits().isEmpty()) {
			player.sendMessage(Constants.prefix + "Please mark (at least) one exit at the border where the algorithm can start building.");
			player.sendMessage("/tangledmaze select exit");
			return;
		}
		
		if(serializedMaterialData.isEmpty()) {
			player.sendMessage(ChatColor.RED + "Please specify (at least) one block type this maze should be built out of.");
			player.sendMessage("/tangledmaze build <block type 1> ... <block type n>");
			return;
		}
		
		ArrayList<MaterialData> composition;
		
		try {
			composition = getWallComposition(serializedMaterialData);
		} catch (Exception e) {
			player.sendMessage(e.getMessage());
			return;
		}
		
		maze.setWallComposition(composition);
		MazeHandler.buildMaze(maze);
		player.sendMessage(Constants.prefix + "Started building your maze.");
		
		ToolHandler.resetToDefaultTool(player);
		MazeHandler.setMaze(player, new Maze(player));
	}
	
	private static ArrayList<MaterialData> getWallComposition(ArrayList<String> serializedMaterialData) {
		ArrayList<MaterialData> composition = new ArrayList<>();
		
		for(String materialData : serializedMaterialData) {
			composition.add(MaterialDeserializer.deserializeMaterialData(materialData));
		}
		
		return composition;
	}
}