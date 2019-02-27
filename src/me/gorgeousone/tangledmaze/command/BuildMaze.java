package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.generation.MazeGenerator;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.MaterialReader;
import me.gorgeousone.tangledmaze.util.Messages;

@SuppressWarnings("deprecation")
public class BuildMaze {

	private MazeGenerator generator;

	public BuildMaze() {
		generator = new MazeGenerator();
	}

	public void execute(Player player, ArrayList<String> serializedMaterialData) {
		
		if(!player.hasPermission(Constants.buildPerm)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isStarted()) {
			
			if(!ToolHandler.hasClipboard(player)) {
				Messages.ERROR_CLIPBOARD_NOT_STARTED.send(player);
				player.sendMessage("/tangledmaze wand");
				return;	
			}
			
			Messages.ERROR_CLIPBOARD_NOT_FINISHED.send(player);
			player.sendMessage("/tangledmaze start");
			return;
		}
		
		if(maze.getExits().isEmpty()) {
			Messages.ERROR_NO_MAZE_EXIT_SET.send(player);
			player.sendMessage("/tangledmaze select exit");
			return;
		}
		
		if(serializedMaterialData.isEmpty()) {
			Messages.ERROR_NO_BUILD_BLOCKS_SPECIFIED.send(player);
			player.sendMessage("/tangledmaze build <block> ...");
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

		MazeHandler.buildMaze(maze, generator);
		Messages.MESSAGE_MAZE_BUILDING.send(player);

		ToolHandler.resetToDefaultTool(player);
		MazeHandler.setMaze(player, new Maze(player));
	}
	
	private static ArrayList<MaterialData> getWallComposition(ArrayList<String> serializedMaterialData) {
		ArrayList<MaterialData> composition = new ArrayList<>();
		
		for(String materialData : serializedMaterialData) {
			composition.add(MaterialReader.readMaterialData(materialData));
		}
		
		return composition;
	}
}