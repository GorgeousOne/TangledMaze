package me.tangledmaze.gorgeousone.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

@SuppressWarnings("deprecation")
public class ToolListener implements Listener {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	private BlockChangeListener blockListener;
	
	private HashMap<Player, Long> times;
	private BukkitRunnable timer;
	private static final int expiration = 10*1000;
	
	public ToolListener(BlockChangeListener bl) {
		
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		blockListener = bl;
		
		times = new HashMap<>();
		timer = new BukkitRunnable() {
			@Override
			public void run() {
				for(Player p : times.keySet())
					
					if(System.currentTimeMillis() - times.get(p) >= expiration) {
						times.remove(p);
								
						if(sHandler.hasSelection(p))
							sHandler.hide(sHandler.getSelection(p));
						if(mHandler.hasMaze(p))
							mHandler.hide(mHandler.getMaze(p));
					}
			}
		};
		timer.runTaskTimer(TangledMain.getPlugin(), 0, 1*20);
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		if(TangledMain.isSelectionWand(e.getItem()))
			e.setCancelled(true);
	}
	
	//destroys tools in hands of players without permissions and hides selections/mazes when clicked on border
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e) {

		try {
			if(e.getHand() != EquipmentSlot.HAND)
				return;
		} catch (NoSuchMethodError e2) {}

		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		
		if(e.getAction() != Action.LEFT_CLICK_BLOCK &&
		   e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		ItemStack item = e.getItem();
		
		//clicking with selection wand
		if(TangledMain.isSelectionWand(item)) {
			e.setCancelled(true);
	
			if(!p.hasPermission(Constants.buildPerm)) {
				destroyTool(p, item);
				return;
			}
			
			sHandler.handleInteraction(p, b, e.getAction());
			return;
			
		//clicking with inflammable objects on TNT
		}else if(item != null)
			if(item.getType() == Material.FLINT_AND_STEEL || item.getType() == Material.FIREBALL) {
				blockListener.update(b.getLocation(), e);
				return;
			}
		
		//just clicking somehow
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(mHandler.hasMaze(p) && mHandler.getMaze(p).isBorder(b))
				mHandler.hide(mHandler.getMaze(p));
			
			if(sHandler.hasSelection(p) && sHandler.getSelection(p).isHighlighted(b))
				sHandler.hide(sHandler.getSelection(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSlotSwitch(PlayerItemHeldEvent e) {
		
		Player p = e.getPlayer();
		Inventory i = p.getInventory();
		
		ItemStack previousItem = i.getItem(e.getPreviousSlot()),
				  newItem      = i.getItem(e.getNewSlot());
		
		if(TangledMain.isSelectionWand(newItem)) {
			times.remove(p);
				
			if(mHandler.hasMaze(p) && !mHandler.isVisible(mHandler.getMaze(p)))
				mHandler.show(mHandler.getMaze(p));
			if(sHandler.hasSelection(p) && !sHandler.isVisible(sHandler.getSelection(p)))
				sHandler.show(sHandler.getSelection(p));
		
		}else if(TangledMain.isSelectionWand(previousItem))			
			times.put(p, System.currentTimeMillis());
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickUp(PlayerPickupItemEvent e) {

		if(TangledMain.isSelectionWand(e.getItem().getItemStack())) {
			Player p = e.getPlayer();
			
				times.remove(p);
			
			if(mHandler.hasMaze(p) && !mHandler.isVisible(mHandler.getMaze(p)))
				mHandler.show(mHandler.getMaze(p));
			if(sHandler.hasSelection(p) && !sHandler.isVisible(sHandler.getSelection(p)))
				sHandler.show(sHandler.getSelection(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent e) {
		
		if(TangledMain.isSelectionWand(e.getItemDrop().getItemStack())) {
			Player p = e.getPlayer();

			if(sHandler.hasSelection(p) || mHandler.hasMaze(p))
				times.put(p, System.currentTimeMillis());
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		
		Chunk c = e.getChunk();
		
		for(Maze maze : mHandler.getMazes())
			if(mHandler.isVisible(maze) && maze.getBorder().containsKey(c)) {
				Player p = maze.getPlayer();

				if(p == null)
					continue;

				for(Location point : maze.getBorder().get(c))
					p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0); 
			}
		
		for(RectSelection selection : sHandler.getSelections())
			if(sHandler.isVisible(selection) && selection.isComplete() && selection.getShape().getBorder().containsKey(c)) {
				Player p = selection.getPlayer();

				if(p == null)
					continue;
				
				for(Location point : selection.getShape().getBorder().get(c))
					p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
				
				for(Location vertex : selection.getVertices())
					p.sendBlockChange(vertex, Constants.SELECTION_CORNER, (byte) 0);
			}
	}
	
	private void destroyTool(Player p, ItemStack tool) {
		
		p.getInventory().remove(tool);
		p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "It seems like you are unworthy to use such mighty tool... it broke apart.");

		p.damage(0);
		p.getWorld().playEffect(p.getLocation().add(0, 1, 0), Effect.EXPLOSION_HUGE, 0);
		
		if(Bukkit.getVersion().contains("1.8"))
			p.getWorld().playSound(p.getEyeLocation(), Sound.valueOf("ITEM_BREAK"), 1f, 1f);
		else
			p.getWorld().playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1f, 1f);
	}
}