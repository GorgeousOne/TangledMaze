package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;
import java.util.List;

import me.gorgeousone.tangledmaze.generation.MazeGenerator;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import me.gorgeousone.tangledmaze.util.TextException;

public class BuildMaze extends MazeCommand {

	private MazeGenerator generator;

	public BuildMaze() {
		
		super("build", "/tangledmaze build <block> ...", 1, true, null);
		
		generator = new MazeGenerator();
	}

	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return true;
		}
		
		Player player = (Player) sender;
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isStarted()) {
			
			if(!ToolHandler.hasClipboard(player)) {
				
				Messages.ERROR_CLIPBOARD_NOT_STARTED.send(player);
				player.sendMessage("/tangledmaze wand");
				return false;	
			}
			
			Messages.ERROR_CLIPBOARD_NOT_FINISHED.send(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(maze.getExits().isEmpty()) {
			Messages.ERROR_NO_MAZE_EXIT_SET.send(player);
			player.sendMessage("/tangledmaze select exit");
			return false;
		}
		
		List<Material> wallMaterials;
		
		try {
			wallMaterials = getWallMaterials(arguments);
			
		} catch (TextException ex) {
			
			ex.getText().send(player, ex.getPlaceHolder());
			return false;
		}
		
		maze.setWallComposition(wallMaterials);

		MazeHandler.buildMaze(maze, generator);
		Messages.MESSAGE_MAZE_BUILDING_STARTED.send(player);

		ToolHandler.resetToDefaultTool(player);
		MazeHandler.setMaze(player, new Maze(player));
		return true;
	}
	
	private static List<Material> getWallMaterials(String[] serializedMaterials) throws TextException {
		
		List<Material> wallMaterials = new ArrayList<>();
		
		for(String materialString : serializedMaterials) {
			
			Material material = Material.matchMaterial(materialString);
			
			if(material == null || !material.isBlock())
				throw new TextException(Messages.ERROR_NO_MATCHING_BLOCK_TYPE, new PlaceHolder("block", materialString));
			else
				wallMaterials.add(material);
		}
		
		return wallMaterials;
	}
}