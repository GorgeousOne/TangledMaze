package me.gorgeousone.tangledmaze.listeners;

import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import me.gorgeousone.tangledmaze.utils.WandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerClickListener implements Listener {

	private ToolHandler toolHandler;

	public PlayerClickListener(ToolHandler toolHandler) {
		this.toolHandler = toolHandler;
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {

		if (WandUtils.isMazeWand(e.getItem())) {
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		}
	}

	@EventHandler
	public void onBlockClick(PlayerInteractEvent event) {

		Action action = event.getAction();

		if (action != Action.LEFT_CLICK_BLOCK &&
				action != Action.RIGHT_CLICK_BLOCK ||
				event.getHand() != EquipmentSlot.HAND)
			return;

		Player player = event.getPlayer();
		ItemStack heldItem = event.getItem();

		if (!WandUtils.isMazeWand(heldItem))
			return;

		event.setCancelled(true);

		if (player.hasPermission(Constants.BUILD_PERM)) {
			toolHandler.handleToolInteraction(player, event.getClickedBlock(), action);

		} else
			destroyMazeWand(player, heldItem);
	}

	//TODO reactivate slot switch event listening

	//@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	//public void onSlotSwitch(PlayerItemHeldEvent event) {
	//
	//		Player player = event.getPlayer();
	//
	//		if(!player.hasPermission(Constants.BUILD_PERM))
	//			return;
	//
	//		ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
	//
	//		if(!Utils.isMazeWand(newItem))
	//			return;
	//
	//		Maze maze = MazeHandler.getMaze(player);
	//
	//		if(maze.isStarted() && !maze.isConstructed() && !Renderer.isMazeVisible(maze))
	//			Renderer.displayMaze(MazeHandler.getMaze(player));
	//
	//		if(ToolHandler.hasClipboard(player) && !Renderer.isClipboardVisible(ToolHandler.getClipboard(player)))
	//			Renderer.displayClipboard(ToolHandler.getClipboard(player));
	//	}

	private void destroyMazeWand(Player player, ItemStack wand) {

		player.getInventory().remove(wand);
		player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "It seems like you are unworthy to use such mighty tool, it broke apart.");
		player.damage(0);

		player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
		player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
	}
}