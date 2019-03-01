package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.Messages;

public class StartMaze extends MazeCommand {
	
	public StartMaze() {
		super("start", "/tangledmaze start", 0, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		if(!ToolHandler.hasClipboard(player) || ToolHandler.getClipboard(player).getVertices().isEmpty()) {
			Messages.ERROR_CLIPBOARD_NOT_STARTED.send(player);
			return false;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(player);
		
		if(!clipboard.isComplete()) {
			Messages.ERROR_CLIPBOARD_NOT_FINISHED.send(player);
			return false;
		}
		
		Clip clip = clipboard.getClip();
		clipboard.reset();
		
		MazeHandler.getMaze(player).setClip(clip);

		return true;
	}
	
	public void execute(Player player) {
		
		if(!player.hasPermission(Constants.BUILD_PERM)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!ToolHandler.hasClipboard(player)) {
			player.sendMessage(ChatColor.RED + "Please select an area first.");
			return;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(player);
		
		if(!clipboard.isComplete()) {
			player.sendMessage(ChatColor.RED + "Please finish your clipboard first.");
			return;
		}
		
		Clip clip = clipboard.getClip();
		clipboard.reset();
		
		MazeHandler.getMaze(player).setClip(clip);
		//TODO start message needed?
	}
}