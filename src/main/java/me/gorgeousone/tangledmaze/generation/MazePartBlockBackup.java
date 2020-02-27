package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.utils.BlockDataState;

import java.util.HashMap;
import java.util.Set;

public class MazePartBlockBackup {
	
	private HashMap<MazePart, Set<BlockDataState>> partBackupLists;
	
	public MazePartBlockBackup() {
		partBackupLists = new HashMap<>();
	}
	
	public Set<BlockDataState> getPartBackup(MazePart part) {
		return partBackupLists.get(part);
	}
	
	public boolean isEmpty() {
		return partBackupLists.isEmpty();
	}
	
	public void setBackup(MazePart part, Set<BlockDataState> blockBackup) {
		partBackupLists.put(part, blockBackup);
	}
	
	public void deleteBackup(MazePart mazePart) {
		partBackupLists.remove(mazePart);
	}
	
	public boolean hasBackup(MazePart part) {
		return partBackupLists.containsKey(part);
	}
}