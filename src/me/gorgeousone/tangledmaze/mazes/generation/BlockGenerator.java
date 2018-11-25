package me.gorgeousone.tangledmaze.mazes.generation;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.Utils;
import me.gorgeousone.tangledmaze.utils.Vec2;
public class BlockGenerator {

	public static void generateBlocks(BuildMap map, ActionListener finishAction) {
		buildBlocksContinuously(getAllMazeBlocks(map), map.getMaze().getWallComposition());
	}
	
	private static void buildBlocksContinuously(ArrayList<Block> placeables, ArrayList<MaterialData> composition) {
		
		Random rnd = new Random();
		
		BukkitRunnable builder = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				
				while(!placeables.isEmpty()) {
					
					BlockState state = placeables.get(0).getState();
					
					MaterialData rndMatData = composition.get(rnd.nextInt(composition.size()));
					state.setType(rndMatData.getItemType());
					state.setRawData(rndMatData.getData());
					state.update(true, false);
					
					placeables.remove(0);
					
					if(System.currentTimeMillis() - timer >= 10)
						return;
				}
				
				this.cancel();
			}
		};
		builder.runTaskTimer(TangledMain.getPlugin(), 0, 1);
	}
	
	private static ArrayList<Block> getAllMazeBlocks(BuildMap map) {
		
		Maze maze = map.getMaze();
		ArrayList<Block> placeables = new ArrayList<>();
		
		int mazeMinX = map.getMinX(),
			mazeMinZ = map.getMinZ();

		int wallHeight = maze.getWallHeight();
		int pointY, maxY;
		
		for(int x = 0; x < map.getDimX(); x++) {
			for(int z = 0; z < map.getDimZ(); z++) {
				
				Vec2 point = new Vec2(x, z);
				
				if(map.getType(point) != MazeFillType.WALL &&
				   map.getType(point) != MazeFillType.UNDEFINED)
					continue;
				
				pointY = map.getHeight(new Vec2(x, z));
				maxY = getMaxY(x, pointY, z, map);
				
				for(int i = pointY+1; i <= maxY + wallHeight; i++) {
					Block b = new Location(maze.getWorld(), x + mazeMinX, i, z + mazeMinZ).getBlock();
					
					if(Utils.canBeReplaced(b.getType()))
						placeables.add(b);
				}
			}
		}
		
		return placeables;
	}
	
	private static int getMaxY(int x, int y, int z, BuildMap map) {
		
		ArrayList<Integer> neighborYs = new ArrayList<>();
		neighborYs.add(y);
		
		for(Directions dir : Directions.values()) {
			int x2 = x + dir.facing().getX(),
				z2 = z + dir.facing().getZ();
			
			if(x2 < 0 || x2 >= map.getDimX() ||
			   z2 < 0 || z2 >= map.getDimZ())
				continue;
			
			neighborYs.add(map.getHeight(
					new Vec2(x + dir.facing().getX(),
							 z + dir.facing().getZ())));
		}
		
		return Utils.getMax(neighborYs);
	}
}