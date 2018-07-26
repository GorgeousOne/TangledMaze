package me.gorgeousone.tangledmaze.mazes;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import me.gorgeousone.tangledmaze.selections.ShapeSelection;

public class Maze {
	
	private Player p;
	
	private ActionHistory history;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private ArrayList<Location> exits;
		
	public Maze(ShapeSelection sel, Player builder) {
		
		p = builder;
		
		history = new ActionHistory();
		fillChunks = sel.getFill();
		borderChunks = sel.getBorder();	
		exits = new ArrayList<>();
		
		if(p != null) {}
	}

	public void setWallComposition(ArrayList<MaterialData> composition) {
		
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int borderSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ArrayList<Location> getExits() {
		// TODO Auto-generated method stub
		return exits;
	}

	public MazeAction reduce(Block b) {
		// TODO Auto-generated method stub
		return null;
	}

	public MazeAction enlarge(Block b) {
		// TODO Auto-generated method stub
		return null;
	}

	public ActionHistory getActionHistory() {
		return history;
	}

	public void processAction(MazeAction action, boolean b) {
		// TODO Auto-generated method stub
		
	}

	public boolean contains(Location location) {
		// TODO Auto-generated method stub
		return false;
	}

	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}

	public ArrayList<Chunk> getChunks() {
		return new ArrayList<>(fillChunks.keySet());
	}

	public MazeAction getDeletion(ShapeSelection selection) {
		return null;
	}

	public MazeAction getAddition(ShapeSelection selection) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPathWidth(int pathWidth) {
		// TODO Auto-generated method stub
		
	}

	public void setWallHeight(int wallHeight) {
		// TODO Auto-generated method stub
		
	}

	public void setWallWidth(int wallWidth) {
		// TODO Auto-generated method stub
		
	}

	public boolean isBorder(Block b) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canBeExit(Location loc) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeExit(Location loc) {
		// TODO Auto-generated method stub
		
	}

	public void addExit(Location loc) {
		// TODO Auto-generated method stub
		
	}

	public Player getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		// TODO Auto-generated method stub
		return borderChunks;
	}
}