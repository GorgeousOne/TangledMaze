package me.gorgeousone.tangledmaze.clip;

import java.util.Stack;

public class ActionHistory {

	private Stack<ClipAction> actions;
	
	public ActionHistory() {
		actions = new Stack<>();
	}
	
	public boolean isEmpty() {
		return actions.isEmpty();
	}
	
	public void pushAction(ClipAction action) {
		actions.push(action);
		
		if(actions.size() > 10)
			actions.remove(0);
	}
	
	public ClipAction popLastAction() {
		return actions.pop();
	}

	public void clear() {
		actions.clear();
	}
}