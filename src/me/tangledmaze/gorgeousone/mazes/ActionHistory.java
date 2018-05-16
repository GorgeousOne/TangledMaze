package me.tangledmaze.gorgeousone.mazes;

import java.util.Stack;

public class ActionHistory {

	private Stack<MazeAction> actions;
	
	public ActionHistory() {
		actions = new Stack<>();
	}
	
	public boolean isEmpty() {
		return actions.isEmpty();
	}
	
	public void pushAction(MazeAction action) {
		actions.push(action);
		
		if(actions.size() > 10)
			actions.remove(0);
	}
	
	public MazeAction popLastAction() {
		return actions.pop();
	}

	public void clear() {
		actions.clear();
	}
}