package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.command.api.command.BasicCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;

public class UndoCommand extends BasicCommand {

	public UndoCommand(MazeCommand mazeCommand) {
		super("undo", null, mazeCommand);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isStarted() || maze.isConstructed()) {
			
			Messages.ERROR_MAZE_NOT_STARTED.sendTo(player);
			return false;
		}
		
		
		if(maze.getActionHistory().isEmpty()) {
			return false;
		}
		
		ClipAction action = maze.getActionHistory().popLastAction().invert();
		maze.processAction(action, false);
		Renderer.displayMazeAction(maze, action);
		return true;
	}
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.BUILD_PERM)) {
			p.sendMessage(Constants.INSUFFICIENT_PERMS);
			return;
		}

		if(!MazeHandler.getMaze(p).isStarted()) {
			p.sendMessage(Constants.prefix + "You did not start a maze where anything can be undone.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getActionHistory().isEmpty()) {
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "There is nothing left to undone...");
			
		}else {
			ClipAction undo = maze.getActionHistory().popLastAction().invert();
			
			maze.processAction(undo, false);
			Renderer.displayMazeAction(maze, undo);
		}
	}
}