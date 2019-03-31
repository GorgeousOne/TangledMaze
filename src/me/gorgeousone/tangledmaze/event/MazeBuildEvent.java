package me.gorgeousone.tangledmaze.event;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.generation.MazeGenerator;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

public class MazeBuildEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled;

	private Maze maze;
	private MazeGenerator generator;

	public MazeBuildEvent(Maze maze, MazeGenerator generator) {

		this.maze = maze;
		this.generator = generator;

		new BukkitRunnable() {
			@Override
			public void run() {

				if(!isCancelled) {
					System.out.println(MazeBuildEvent.this.maze);
					MazeHandler.buildMaze(MazeBuildEvent.this.maze, generator);
				}
			}
		}.runTask(TangledMain.getInstance());
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}

	public MazeGenerator getMazeGeneraor() {
		return generator;
	}

	public void setExitGenerator(MazeGenerator generator) {
		this.generator = generator;
	}
}
