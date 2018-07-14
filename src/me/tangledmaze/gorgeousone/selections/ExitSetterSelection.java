package me.tangledmaze.gorgeousone.selections;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;

public class ExitSetterSelection extends Selection {
	
	private MazeHandler mHandler;
	
	public ExitSetterSelection(Player p) {
		super(p);
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void interact(Block b, Action a) {
		mHandler.addExitToMaze(p, b);
	}
}