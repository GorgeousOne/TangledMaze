package me.gorgeousone.tangledmaze.command;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.Messages;

public class DiscardMaze {
	
	public void execute(Player player) {
		
		if(!player.hasPermission(Constants.buildPerm)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}

		MazeHandler.getMaze(player).reset();
		ToolHandler.resetToDefaultTool(player);
		Messages.MESSAGE_MAZE_DISCARD.send(player);
	}
}