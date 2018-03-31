package me.tangledmaze.gorgeousone.main;

import me.tangledmaze.gorgeousone.listener.MazeHandler;
import me.tangledmaze.gorgeousone.listener.SelectionHandler;
import me.tangledmaze.gorgeousone.listener.ToolListener;
import me.tangledmaze.main.IMain;
import me.tangledmaze.main.TangledMain;

public class TangledMain_go implements IMain {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	@Override
	public void onLoad(TangledMain plugin) {
	}

	@Override
	public void onEnable(TangledMain plugin) {
		plugin = TangledMain.plugin;
		
		mHandler = new MazeHandler();
		sHandler = new SelectionHandler();
		
		plugin.getServer().getPluginManager().registerEvents(sHandler, plugin);
		plugin.getServer().getPluginManager().registerEvents(new ToolListener(this), plugin);
		plugin.getCommand("tangledmaze").setExecutor(new CommandListener(this));
	}

	@Override
	public void onDisable(TangledMain plugin) {
		sHandler.reload();
		mHandler.reload();
	}
	
	public SelectionHandler getSelectionHandler() {
		return sHandler;
	}
	
	public MazeHandler getMazeHandler() {
		return mHandler;
	}
}