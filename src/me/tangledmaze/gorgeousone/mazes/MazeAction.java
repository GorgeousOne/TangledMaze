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

	@SuppressWarnings("unchecked")
	public ArrayList<Location> getAddedFill() {
		return (ArrayList<Location>) addedFill.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Location> getRemovedFill() {
		return (ArrayList<Location>) removedFill.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Location> getAddedBorder() {
		return (ArrayList<Location>) addedBorder.clone();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Location> getRemovedBorder() {
		return (ArrayList<Location>) removedBorder.clone();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Location> getRemovedExits() {
		return (ArrayList<Location>) removedExits.clone();
	}
}