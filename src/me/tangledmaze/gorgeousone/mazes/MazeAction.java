package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;

import org.bukkit.Location;

public class MazeAction {
	
	private ArrayList<Location> addedFill, removedFill, addedBorder, removedBorder, removedExits;
	
	public MazeAction(ArrayList<Location> addedFill,   ArrayList<Location> removedFill,
					  ArrayList<Location> addedBorder, ArrayList<Location> removedBorder,
					  ArrayList<Location> removedExits) {
		
		this.addedFill     = addedFill;
		this.addedBorder   = addedBorder;
		this.removedFill   = removedFill;
		this.removedBorder = removedBorder;
		this.removedExits  = removedExits;
	}

	public ArrayList<Location> getAddedFill() {
		return addedFill;
	}

	public ArrayList<Location> getRemovedFill() {
		return removedFill;
	}

	public ArrayList<Location> getAddedBorder() {
		return addedBorder;
	}
	
	public ArrayList<Location> getRemovedBorder() {
		return removedBorder;
	}
	
	public ArrayList<Location> getRemovedExits() {
		return  removedExits;
	}
	
	public MazeAction reverse() {
		ArrayList<Location> temp = new ArrayList<Location>(addedFill);
		
		addedFill.clear();
		addedFill.addAll(removedFill);
		removedFill.clear();
		removedFill.addAll(temp);
		
		temp = new ArrayList<>(addedBorder);
		
		addedBorder.clear();
		addedBorder.addAll(removedBorder);
		removedBorder.clear();
		removedBorder.addAll(temp);
		
		getRemovedExits().clear();
		
		return this;
	}
}