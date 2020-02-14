package me.gorgeousone.tangledmaze.clip;

import java.util.Stack;

public class ActionHistory {

	private Stack<ClipChange> actions;

	public ActionHistory() {
		actions = new Stack<>();
	}

	public boolean isEmpty() {
		return actions.isEmpty();
	}

	public void pushAction(ClipChange action) {
		actions.push(action);

		if (actions.size() > 10)
			actions.remove(0);
	}

	public ClipChange popLastAction() {
		return actions.pop();
	}

	public void clear() {
		actions.clear();
	}
}