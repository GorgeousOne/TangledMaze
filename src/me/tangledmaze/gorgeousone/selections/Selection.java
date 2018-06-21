package me.tangledmaze.gorgeousone.selections;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class Selection {

	private World world;
	private Player p;
	
	private boolean isComplete;
	private ArrayList<Location> vertices;
	
	public Selection(Player p) {
		this.p = p;
		world = p.getWorld();
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public World getWorld() {
		return world;
	}

	public boolean isComplete() {
		return isComplete;
	}
	
	public ArrayList<Location> getVertices() {
		return vertices;
	}
	
	public boolean contains(Location point) {
		return false;
	}
	
	
}