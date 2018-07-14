package me.tangledmaze.gorgeousone.selections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.events.MazeShapeEvent;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeAction;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;

public class BrushSelection extends Selection {
	
	MazeHandler mHandler;
	int maxMazeSize;
	
	public BrushSelection(Player p) {
		super(p);
		
		mHandler = TangledMain.getPlugin().getMazeHandler();
		maxMazeSize = mHandler.getMaxMazeSize(p);
	}
	
	
	public void interact(Block b, Action a) {

		Maze maze = TangledMain.getPlugin().getMazeHandler().getMaze(p);
		MazeAction brush = null;
		
		//get affected blocks of shaping as a MazeAction
		if(a == Action.RIGHT_CLICK_BLOCK) {
			brush = maze.reduce(b);
		}else
			brush = maze.enlarge(b);
		
		//if the maze was shaped somehow, call an event
		if(brush != null) {
			
			MazeShapeEvent brushing = new MazeShapeEvent(maze, brush);
			Bukkit.getPluginManager().callEvent(brushing);
			
			int newMazeSize =  maze.size() + brush.getAddedFill().size();
			
			if(newMazeSize > maxMazeSize) {
				brushing.setCancelled(true, ChatColor.RED + "Your maze would become " + (newMazeSize - maxMazeSize)
						+ " blocks greater than your rank allows to (" + maxMazeSize + " blocks).");
			}
			
		}else if(Math.random() < 1/3d)
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This is not your maze's outline...");
	}
}