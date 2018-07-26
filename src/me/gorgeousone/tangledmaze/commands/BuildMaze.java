package me.gorgeousone.tangledmaze.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import me.gorgeousone.tangledmaze.core.Constants;
import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.mazes.WallComposer;
import me.gorgeousone.tangledmaze.selections.SelectionHandler;
import me.gorgeousone.tangledmaze.selections.ShapeSelection;
import me.gorgeousone.tangledmaze.shapes.Rectangle;


public class BuildMaze {

	public void execute(Player p, ArrayList<String> serializedMaterialData) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
//		if(SelectionHandler.isInQueue(p)) {
//			p.sendMessage(Constants.prefix + "There already is a maze in the queue you built. Please wait until it gets finsihed before submitting a new one.");
//			return;
//		}
		
		if(!MazeHandler.hasMaze(p)) {
			
			if(!SelectionHandler.hasShapeSel(p)) {
				p.sendMessage(ChatColor.RED + "Please select an area with a selection wand first.");
				p.sendMessage("/tangledmaze start");
				return;	
			}
			
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.size() == maze.borderSize()) {
			p.sendMessage(Constants.prefix + "Wth!? This maze only consists of border, it will not be built!");
			return;
		}
		
		if(maze.getExits().isEmpty()) {
			p.sendMessage(Constants.prefix + "This plugin's algorithm needs a start point for the maze " + 
											 "Could you be so nice and mark (at least) one exit at the border?");
			p.sendMessage("/tangledmaze select exit");
			return;
		}
		
		if(serializedMaterialData.isEmpty()) {
			p.sendMessage(ChatColor.RED + "Please specify at least one block this maze should be built of.");
			p.sendMessage("/tangledmaze build <block type 1> ... <block type n>");
			return;
		}
		
		ArrayList<MaterialData> composition = WallComposer.deserializeComposition(p, serializedMaterialData);
		
		if(composition == null)
			return;
		
		MazeHandler.getMaze(p).setWallComposition(composition);
		
		int queuePosition = 0; // SelectionHandler.joinBuildQueue(maze);

		if(queuePosition >= 0) {
			MazeHandler.removeMaze(p);

			if(!(SelectionHandler.getSelection(p) instanceof ShapeSelection))
				SelectionHandler.setSelection(p, new ShapeSelection(p, new Rectangle()));

			if(queuePosition > 0) {
				p.sendMessage(Constants.prefix + "Your maze has been queued. Position in queue: " + queuePosition);
				p.sendMessage(Constants.prefix + "If you leave the server before it gets built your work will be discarded!");
			}
			
		}else
			p.sendMessage(Constants.prefix + "You already queued a maze to be built. Please wait until that one gets finished before queuing an new one.");
	}
}