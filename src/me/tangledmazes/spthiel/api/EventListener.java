package me.tangledmazes.spthiel.api;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener{
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		UuidAPI.savePlayer(e.getPlayer());
	}
	
}
