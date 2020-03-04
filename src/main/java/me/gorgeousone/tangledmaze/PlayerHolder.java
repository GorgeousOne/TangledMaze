package me.gorgeousone.tangledmaze;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerHolder {
	
	private UUID playerUUID;
	
	public PlayerHolder(Player player) {
		this.playerUUID = player.getUniqueId();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(playerUUID);
	}
	
	public void setPlayer(Player player) {
		this.playerUUID = player.getUniqueId();
	}
}
