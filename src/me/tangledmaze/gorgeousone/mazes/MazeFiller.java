package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.main.Utils;

public class MazeFiller {

	@SuppressWarnings("unused")
	private static final int
		UNDEFINED = 1,
		WALL = 2,
		PATH = 3;
	
	public MazeFiller() {
	}
	
	public void fillMaze(Maze maze) {
		
		ArrayList<Chunk> chunks = maze.getChunks();
		Chunk first = chunks.get(0);
		
		int minX = first.getX(),
			minZ = first.getZ(),
			maxX = minX,
			maxZ = minZ;
		
		for(Chunk c : chunks) {
			Bukkit.broadcastMessage(c.toString());
			
			if(c.getX() < minX)
				minX = c.getX();
			else if(c.getX() > maxX)
				maxX = c.getX();

			if(c.getZ() < minZ)
				minZ = c.getZ();
			else if(c.getZ() > maxZ)
				maxZ = c.getZ();
		}
		
		minX *= 16;
		maxX *= 16;
		minZ *= 16;
		maxZ *= 16;
//		Bukkit.broadcastMessage(maxX + ", " + minX);
//		Bukkit.broadcastMessage(maxZ + ", " + minZ);
//		Bukkit.broadcastMessage("" + (maxX - minX));
//		Bukkit.broadcastMessage("" + (maxZ - minZ));

		int[][] map = new int[maxX - minX + 16][maxZ - minZ + 16];
			
		for(Location point : maze.getFill())
			map[point.getBlockX() - minX][point.getBlockZ() - minZ] = UNDEFINED;
		for(Location point : maze.getBorder())
			map[point.getBlockX() - minX][point.getBlockZ() - minZ] = WALL;
		
		//TODO use maze.getStart() function when it is ready
		Vector start = maze.getFill().get(1).toVector().add(new Vector(-minX, 0, -minZ));
		
		generatePaths(maze, map, start, minX, minZ);
		Bukkit.broadcastMessage(map.length + "");
	}
	
	private void generatePaths(Maze maze, int[][] mazeMap, Vector start, int shiftX, int shiftZ) {
		BukkitRunnable pathGenerator = new BukkitRunnable() {
			@Override
			public void run() {
				ArrayList<Vector> openEnds = new ArrayList<>();
				openEnds.add(start);
				
				while(!openEnds.isEmpty()) {
					
					Vector lastEnd = openEnds.get(openEnds.size()-1);
					boolean isClosed = true;
					
					for(Vector dir : Utils.shuffledCardinalDirs()) {
						
						Vector path = lastEnd.clone().add(dir),
							   newEnd = path.clone().add(dir);
						
						if(newEnd.getBlockX() < 0 || newEnd.getBlockX() > mazeMap.length ||
						   newEnd.getBlockZ() < 0 || newEnd.getBlockZ() > mazeMap[0].length)
							continue;

						if(path.getBlockX() < 0 || path.getBlockX() > mazeMap.length ||
						   path.getBlockZ() < 0 || path.getBlockZ() > mazeMap[0].length)
							continue;

						if(mazeMap[  path.getBlockX()][  path.getBlockZ()] != UNDEFINED ||
						   mazeMap[newEnd.getBlockX()][newEnd.getBlockZ()] != UNDEFINED)
							continue;
						
						mazeMap[  path.getBlockX()][  path.getBlockZ()] = PATH;
						mazeMap[newEnd.getBlockX()][newEnd.getBlockZ()] = PATH;
						
						openEnds.add(newEnd);
						isClosed = false;
						break;
					}
					
					if(isClosed)
						openEnds.remove(lastEnd);
				}
				
				MazeFiller.this.shoeMaze(maze, mazeMap, shiftX, shiftZ);
			}
		};
		pathGenerator.runTaskAsynchronously(TangledMain.getPlugin());
	}
	
	@SuppressWarnings("deprecation")
	public void shoeMaze(Maze maze, int[][] mazeMap, int shiftX, int shiftZ) {
		
		Player p = maze.getPlayer();
		p.sendMessage("asdasD");
		
		for(int x = 0; x < mazeMap.length; x++)
			for(int z = 0; z < mazeMap[0].length; z++) {
				
				if(mazeMap[x][z] != WALL && mazeMap[x][z] != UNDEFINED)
					continue;
				
				Location l = Utils.getNearestSurface(new Location(p.getWorld(), x+shiftX, 64, z+shiftZ));
				p.sendBlockChange(l, Material.LEAVES, (byte) (Math.random() * 4));
			}
	}
}