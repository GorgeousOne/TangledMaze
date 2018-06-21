package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.generators.ExitGenerator;
import me.tangledmaze.gorgeousone.mazes.generators.MazePath;
import me.tangledmaze.gorgeousone.mazes.generators.PathGenerator;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Utils;

public class MazeBuilder {

	private ArrayList<Maze> mazeQueue;
	private Maze currentMaze;
	
	private int[][] mazeMap, heightMap;
	private int mazeMinX, mazeMinZ;
	private Vector pathStart;
	
	public MazeBuilder() {
		mazeQueue = new ArrayList<>();
	}
	
	public boolean isInQueue(Player p) {
		for(Maze maze : mazeQueue)
			if(p.equals(maze.getPlayer()))
				return true;
		return false;
	}
	
	public int enqueueMaze(Maze maze) {
		
		if(mazeQueue.contains(maze))
			return -1;
		
		mazeQueue.add(maze);
		
		if(mazeQueue.size() == 1)
			prepareNextMaze();
		
		return mazeQueue.indexOf(maze);
	}
	
	public void discard(Maze maze) {
		mazeQueue.remove(maze);
	}
	
	private void prepareNextMaze() {
		
		if(mazeQueue.isEmpty())
			return;
		
		currentMaze = mazeQueue.get(0);
		
		createMazeMaps();
		pathStart = ExitGenerator.generateExits(currentMaze, mazeMap, mazeMinX, mazeMinZ);
		PathGenerator.generatePaths(currentMaze, mazeMap, pathStart);
		buildMaze();
	}
	
	private void createMazeMaps() {
		
		ArrayList<Chunk> chunks = currentMaze.getChunks();
		
		//select a probably random chunk from the maze's chunk list
		Chunk randomChunk = chunks.get(0);
		
		//initialize the maze's minimal and maximal coordinates with the chunk
		mazeMinX = randomChunk.getX();
		mazeMinZ = randomChunk.getZ();
		
		int
			mazeMaxX = mazeMinX,
			mazeMaxZ = mazeMinZ;
		
		//look for chunks with greater/smaller coordinates
		for(Chunk c : chunks) {
			if(c.getX() < mazeMinX)
				mazeMinX = c.getX();
			else if(c.getX() > mazeMaxX)
				mazeMaxX = c.getX();

			if(c.getZ() < mazeMinZ)
				mazeMinZ = c.getZ();
			else if(c.getZ() > mazeMaxZ)
				mazeMaxZ = c.getZ();
		}
		
		//multiply with a chunk's length to get to normal location coordinates
		mazeMinX *= 16;
		mazeMinZ *= 16;
		mazeMaxX *= 16;
		mazeMaxZ *= 16;
		
		//create two 2D-arrays, one for locations occupied by the maze, another one for the y-coordinates there
		mazeMap   = new int[mazeMaxX - mazeMinX + 16][mazeMaxZ - mazeMinZ + 16];
		heightMap = new int[mazeMaxX - mazeMinX + 16][mazeMaxZ - mazeMinZ + 16];
		
		//mark the maze's area in mazeMap as undefined area (open for path or walls), the will stay untouched
		for(ArrayList<Location> chunk : currentMaze.getFill().values())
			for(Location point : chunk) {
				mazeMap [point.getBlockX() - mazeMinX][point.getBlockZ() - mazeMinZ] = MazePath.UNDEFINED;
				heightMap[point.getBlockX() - mazeMinX][point.getBlockZ() - mazeMinZ] = point.getBlockY();
			}
		
		//mark the border in mazeMap as reserved for walls
		for(ArrayList<Location> chunk : currentMaze.getBorder().values())
			for(Location point : chunk)
				mazeMap[point.getBlockX() - mazeMinX][point.getBlockZ() - mazeMinZ] = MazePath.WALL;
	}
	
	private void buildMaze() {
		
		ArrayList<Vector> directions = Utils.directions();
		ArrayList<Block> placeables = new ArrayList<>();

		int wallHeight = currentMaze.getDimensions().getBlockY();
		int pointY, maxY;
		
		for(int x = 0; x < mazeMap.length; x++) {
			for(int z = 0; z < mazeMap[0].length; z++) {
				
				if(mazeMap[x][z] != MazePath.WALL && mazeMap[x][z] != MazePath.UNDEFINED)
					continue;
				
				pointY = heightMap[x][z];

				ArrayList<Integer> neighborYs = new ArrayList<>();
				neighborYs.add(pointY);
				
				for(Vector dir : directions) {
					int x2 = x + dir.getBlockX(),
						z2 = z + dir.getBlockZ();
					
					if(x2 < 0 || x2 >= mazeMap.length ||
					   z2 < 0 || z2 >= mazeMap[0].length)
						continue;
					
					neighborYs.add(heightMap[x + dir.getBlockX()][z + dir.getBlockZ()]);
				}
				
				maxY = Utils.getMax(neighborYs);
				
				for(int i = pointY+1; i <= maxY + wallHeight; i++) {
					Block b = (new Location(currentMaze.getWorld(), x+mazeMinX, i, z+mazeMinZ)).getBlock();
					
					if(Utils.canBeReplaced(b.getType()))
						placeables.add(b);
				}
			}
		}

		ArrayList<MaterialData> composition = currentMaze.getWallComposition();
		Random rnd = new Random();
		
		BukkitRunnable builder = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				
				while(!placeables.isEmpty()) {
					
					BlockState state = placeables.get(0).getState();
					placeables.remove(0);
					
					MaterialData rndMatData = composition.get(rnd.nextInt(composition.size()));
					state.setType(rndMatData.getItemType());
					state.setRawData(rndMatData.getData());
					state.update(true, false);
					
					
					if(System.currentTimeMillis() - timer >= 40)
						return;
				}
				
				this.cancel();
				
				if(currentMaze.getPlayer() != null)
					currentMaze.getPlayer().sendMessage(Constants.prefix + "Your maze has been finished!");
				
				mazeQueue.remove(currentMaze);
				prepareNextMaze();
			}
		};
		builder.runTaskTimer(TangledMain.getPlugin(), 0, 1);
	}
}