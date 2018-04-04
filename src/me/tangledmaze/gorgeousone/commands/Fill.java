package me.tangledmaze.gorgeousone.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.listener.MazeHandler;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeFiller;

public class Fill {

	private MazeHandler mHandler;
	private MazeFiller mFiller;
	
	public Fill(TangledMain plugin) {
		mHandler = plugin.getMazeHandler();
		mFiller = plugin.getMazeFiller();
	}
	
	public void execute(Player p, ArrayList<String> args) {
		
		if(!mHandler.hasMaze(p)) {
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}

		mFiller.fillMaze(mHandler.getMaze(p));
//		if(args.isEmpty()) {
//			p.sendMessage(ChatColor.RED + "");
//		}
//		
//		for(String arg : args) {
//			if(!arg.contains("%")) {
//				p.sendMessage("Could not use \"" + arg + "\"");
//				return;
//			}
//		}
	}
}
