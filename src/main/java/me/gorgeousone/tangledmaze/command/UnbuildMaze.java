package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.command.api.argument.ArgType;
import me.gorgeousone.tangledmaze.command.api.argument.ArgValue;
import me.gorgeousone.tangledmaze.command.api.argument.Argument;
import me.gorgeousone.tangledmaze.command.api.command.ArgCommand;
import me.gorgeousone.tangledmaze.generation.AbstractGenerator;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.handler.MazeHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UnbuildMaze extends ArgCommand {
	
	public UnbuildMaze(MazeCommand mazeCommand) {
		super("unbuild", null, mazeCommand);
		addArg(new Argument("part", ArgType.STRING, new ArgValue(ArgType.STRING, "maze"), "maze", "floor", "roof"));
	}

	@Override
	protected boolean onExecute(CommandSender sender, ArgValue[] values) {

		Player player = (Player) sender;
		Maze maze = MazeHandler.getMaze(player);

		if(!maze.isConstructed()) {
			Messages.MESSAGE_NO_MAZE_TO_UNBUILD.sendTo(player);
			return true;
		}

		String mazePart = values[0].getString();

		switch (mazePart) {

			case "floor":

				if(BuildHandler.getFloorBlocks(maze) != null) {
					new AbstractGenerator() {
						@Override
						protected void chooseBlockMaterial(BlockState block, List<Material> blockMaterials) {}

						@Override
						protected List<BlockState> getRelevantBlocks(TerrainMap terrainMap) {
							return BuildHandler.getFloorBlocks(maze);
						}
					}.generatePart(BuildHandler.getTerrainMap(maze), null, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent actionEvent) {
							BuildHandler.removeFloor(maze);
						}
					});
				}
				break;

			case "roof":

				if(BuildHandler.getRoofBlocks(maze) != null) {
					new AbstractGenerator() {
						@Override
						protected void chooseBlockMaterial(BlockState block, List<Material> blockMaterials) {}

						@Override
						protected List<BlockState> getRelevantBlocks(TerrainMap terrainMap) {
							return BuildHandler.getRoofBlocks(maze);
						}
					}.generatePart(BuildHandler.getTerrainMap(maze), null, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent actionEvent) {
							BuildHandler.removeRoof(maze);
						}
					});
				}
				break;

			case "maze":
				BuildHandler.unbuildMaze(maze);
				Messages.MESSAGE_MAZE_UNBUILDING_STARTED.sendTo(player);
				break;

			default:
				Messages.ERROR_INVALID_MAZE_PART.sendTo(player, new PlaceHolder("mazepart", mazePart));
				break;
		}

		return true;
	}
}