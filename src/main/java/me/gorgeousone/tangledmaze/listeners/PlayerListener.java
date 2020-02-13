package me.gorgeousone.tangledmaze.listeners;

import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.utils.WandUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

	private ToolHandler toolHandler;
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	private Renderer renderer;

	public PlayerListener(ToolHandler toolHandler, ClipToolHandler clipHandler,
	                      MazeHandler mazeHandler, Renderer renderer) {
		this.toolHandler = toolHandler;
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
		this.renderer = renderer;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSlotSwitch(PlayerItemHeldEvent event) {

		Player player = event.getPlayer();

		if (!player.hasPermission(Constants.BUILD_PERM))
			return;

		ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

		if (!WandUtils.isMazeWand(newItem))
			return;

		Maze maze = mazeHandler.getMaze(player);

		if (maze.isStarted() && !maze.isConstructed())
			renderer.displayMaze(mazeHandler.getMaze(player));

		if (clipHandler.hasClipTool(player))
			renderer.displayClipboard(clipHandler.getClipTool(player));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {

		Player player = e.getPlayer();

		if (player.hasPermission(Constants.BUILD_PERM)) {
			toolHandler.removeTool(player);
			mazeHandler.removeMaze(player);
		}
	}
}