package me.gorgeousone.tangledmaze.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.mazes.WallComposer;
import me.gorgeousone.tangledmaze.selections.SelectionHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

public class BuildMaze {

	public void execute(Player p, ArrayList<String> serializedMaterialData) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
//		if(BuildFactory.isMazeEnqueued(maze)) {
//			p.sendMessage(Constants.prefix
//					+ "There already is a maze of yours in queue to get built. "
//					+ "Please wait until it gets finished before submitting a new one.");
//			return;
//		}
		
		if(!maze.isStarted()) {
			
			if(!SelectionHandler.hasShapeSel(p)) {
				p.sendMessage(ChatColor.RED + "Please select an area with a selection wand first.");
				p.sendMessage("/tangledmaze start");
				return;	
			}
			
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		if(maze.size() == maze.borderSize()) {
			p.sendMessage(Constants.prefix + "What!? This maze only consists of border, it will not be built!");
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
		
		ArrayList<MaterialData> composition = WallComposer.deserializeComposition(p, serializedMaterialData);
		
		if(composition == null)
			return;
		
		maze.setWallComposition(composition);
		MazeHandler.buildMaze(maze);
		p.sendMessage(Constants.prefix + "Started building your maze.");
		
		SelectionHandler.resetToDefaultSel(p);
		maze.reset();
	}
}