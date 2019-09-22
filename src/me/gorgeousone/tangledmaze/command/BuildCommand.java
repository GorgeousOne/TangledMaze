package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;
import java.util.List;

import me.gorgeousone.tangledmaze.generation.WallGenerator;
import me.gorgeousone.tangledmaze.generation.PathGenerator;
import me.gorgeousone.tangledmaze.generation.TerrainEditor;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import me.gorgeousone.tangledmaze.util.TextException;

public class BuildCommand extends MazeCommand {

	private PathGenerator pathGenerator;
	private TerrainEditor terrainEditor;
	private WallGenerator wallGenerator;

	public BuildCommand() {
		
		super("build", "/tangledmaze build <maze/floor/ceiling> <block> ...", 1, true, null);
		
		pathGenerator = new PathGenerator();
		terrainEditor = new TerrainEditor();
		wallGenerator = new WallGenerator();
	}

	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isStarted()) {
			
			if(!ToolHandler.hasClipboard(player)) {
				
				Messages.ERROR_CLIPBOARD_NOT_STARTED.sendTo(player);
				player.sendMessage("/tangledmaze wand");
				return false;	
			}
			
			Messages.ERROR_CLIPBOARD_NOT_FINISHED.sendTo(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(!maze.hasExits()) {
			Messages.ERROR_NO_MAZE_EXIT_SET.sendTo(player);
			player.sendMessage("/tangledmaze select exit");
			return false;
		}
		
		switch (arguments[0]) {
		
		case "floor":
			
			if(!maze.isConstructed())
			break;
		
		case "ceiling":

			if(!maze.isConstructed())
			break;
			
		case "maze":
			break;

		default:
			break;
		}
		
		if(maze.isConstructed()) {
			Messages.MESSAGE_MAZE_ALREADY_BUILT.sendTo(player);
			return false;
		}
		
		List<Material> wallMaterials;
		
		try {
			wallMaterials = getWallMaterials(arguments);
			
		} catch (TextException ex) {
			
			ex.sendTextTo(player);
			return false;
		}

		Renderer.hideMaze(maze);
		BuildHandler.buildMaze(maze, wallMaterials, pathGenerator, terrainEditor, wallGenerator);
		
		Messages.MESSAGE_MAZE_BUILDING_STARTED.sendTo(player);
		ToolHandler.resetToDefaultTool(player);
		return true;
	}
	
//	private void buildMaze(player, maze, ) {
//		
//	}
	
	private List<Material> getWallMaterials(String[] serializedMaterials) throws TextException {
		
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