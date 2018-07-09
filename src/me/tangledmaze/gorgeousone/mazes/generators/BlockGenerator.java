package me.tangledmaze.gorgeousone.mazes.generators;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.utils.Utils;

public class BlockGenerator {

	public static void generateBlocks(MazeMap map, ActionListener finishAction) {
		
		Maze maze = map.getMaze();

		int mazeMinX = map.getMinX(),
			mazeMinZ = map.getMinZ();
		
		int[][] shapeMap   = map.getShapeMap(),
				heightMap = map.getHeightMap();
		
		ArrayList<Vector> directions = Utils.directions();
		ArrayList<Block> placeables = new ArrayList<>();

		int wallHeight = maze.getDimensions().getBlockY();
		int pointY, maxY;
		
		
		for(int x = 0; x < shapeMap.length; x++) {
			for(int z = 0; z < shapeMap[0].length; z++) {
				
				if(shapeMap[x][z] != MazeSegment.WALL && shapeMap[x][z] != MazeSegment.UNDEFINED)
					continue;
				
				pointY = heightMap[x][z];

				ArrayList<Integer> neighborYs = new ArrayList<>();
				neighborYs.add(pointY);
				
				for(Vector dir : directions) {
					int x2 = x + dir.getBlockX(),
						z2 = z + dir.getBlockZ();
					
					if(x2 < 0 || x2 >= shapeMap.length ||
					   z2 < 0 || z2 >= shapeMap[0].length)
						continue;
					
					neighborYs.add(heightMap[x + dir.getBlockX()][z + dir.getBlockZ()]);
				}
				
				maxY = Utils.getMax(neighborYs);
				
				for(int i = pointY+1; i <= maxY + wallHeight; i++) {
					Block b = (new Location(maze.getWorld(), x + mazeMinX, i, z + mazeMinZ)).getBlock();
					
					if(Utils.canBeReplaced(b.getType()))
						placeables.add(b);
				}
			}
		}

		ArrayList<MaterialData> composition = maze.getWallComposition();
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
					
					if(System.currentTimeMillis() - timer >= 40)
						return;
				}
				
				this.cancel();
				finishAction.actionPerformed(null);
			}
		};
		builder.runTaskTimer(TangledMain.getPlugin(), 0, 1);
		
	}
}