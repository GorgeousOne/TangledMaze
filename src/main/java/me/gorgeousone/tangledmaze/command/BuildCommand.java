package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.gorgeousone.tangledmaze.generation.RoofGenerator;
import me.gorgeousone.tangledmaze.generation.WallGenerator;
import me.gorgeousone.tangledmaze.generation.FloorGenerator;
import me.gorgeousone.tangledmaze.generation.PathGenerator;

import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.api.argument.ArgType;
import me.gorgeousone.tangledmaze.command.api.argument.ArgValue;
import me.gorgeousone.tangledmaze.command.api.argument.Argument;
import me.gorgeousone.tangledmaze.command.api.command.ArgCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.mapmaking.TerrainEditor;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import me.gorgeousone.tangledmaze.util.TextException;

public class BuildCommand extends ArgCommand {

	private PathGenerator pathGenerator;
	private TerrainEditor terrainEditor;
	private WallGenerator wallGenerator;
	private FloorGenerator floorGenerator;
	private RoofGenerator roofGenerator;

	public BuildCommand(MazeCommand mazeCommand) {
		super("build", null, mazeCommand);
		
		addArg(new Argument("part", ArgType.STRING, "walls", "floor", "roof"));
		addArg(new Argument("blocks...", ArgType.STRING));

		pathGenerator = new PathGenerator();
		terrainEditor = new TerrainEditor();
		wallGenerator = new WallGenerator();
		floorGenerator = new FloorGenerator();
		roofGenerator = new RoofGenerator();
	}

	@Override
	protected boolean onExecute(CommandSender sender, ArgValue[] args) {
		
		Player player = (Player) sender;
		Maze maze = getStartedMaze(player, true, false);
		
		if(maze == null)
			return false;
		
		String mazePart = args[0].getString();
		List<Material> blockMaterials;

		try {
			blockMaterials = getWallMaterials(Arrays.copyOfRange(args, 1, args.length));

		} catch (TextException ex) {
			ex.sendTextTo(player);
			return false;
		}

		switch (mazePart) {
		case "floor":

			if(!maze.isConstructed()) {
				Messages.ERROR_MAZE_NOT_BUILT.sendTo(player);
				return false;
			}

			if(BuildHandler.getFloorBlocks(maze) != null) {
				Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
				player.sendMessage("/tangledmaze unbuild floor");
				return false;
			}

			floorGenerator.generatePart(BuildHandler.getTerrainMap(maze), blockMaterials, null);
			break;
		
		case "roof":

			if(!maze.isConstructed()) {
				Messages.ERROR_MAZE_NOT_BUILT.sendTo(player);
				return false;
			}

			if(BuildHandler.getRoofBlocks(maze) != null) {
				Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
				player.sendMessage("/tangledmaze unbuild roof");
				return false;
			}

			roofGenerator.generatePart(BuildHandler.getTerrainMap(maze), blockMaterials, null);
			break;
			
		case "walls":
		case "maze":

			if(maze.isConstructed()) {
				Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
				return false;
			}

			Renderer.hideMaze(maze);
			TerrainMap terrainMap = new TerrainMap(maze);

			pathGenerator.generatePaths(terrainMap);
			terrainEditor.editTerrain(terrainMap);
			wallGenerator.generatePart(terrainMap, blockMaterials, null);
			BuildHandler.setTerrainMap(maze, terrainMap);

			ToolHandler.resetToDefaultTool(maze.getPlayer());
			Messages.MESSAGE_MAZE_BUILDING_STARTED.sendTo(maze.getPlayer());

			break;

		default:
			Messages.ERROR_INVALID_MAZE_PART.sendTo(player, new PlaceHolder("mazepart", mazePart));
			break;
		}
		
		return true;
	}

	private List<Material> getWallMaterials(ArgValue[] serializedMaterials) throws TextException {

		List<Material> wallMaterials = new ArrayList<>();

		for(ArgValue materialValue : serializedMaterials) {

			String materialString = materialValue.getString();
			Material material = Material.matchMaterial(materialString);

			if(material == null || !material.isBlock())
				throw new TextException(Messages.ERROR_NO_MATCHING_BLOCK_TYPE, new PlaceHolder("block", materialString));
			else
				wallMaterials.add(material);
		}

		return wallMaterials;
	}
}