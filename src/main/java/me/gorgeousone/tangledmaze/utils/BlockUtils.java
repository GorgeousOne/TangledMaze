package me.gorgeousone.tangledmaze.utils;

import me.gorgeousone.tangledmaze.data.Constants;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public final class BlockUtils {
	
	public static int nearestSurfaceY(Vec2 point, int startY, World world) {
		return nearestSurface(new Location(world, point.getX(), startY, point.getZ())).getY();
	}
	
	public static Block nearestSurface(Location point) {
		
		Block iter = point.getBlock();
		
		if (isReallySolid(iter.getType())) {
			
			while (iter.getY() <= 255) {
				iter = iter.getRelative(BlockFace.UP);
				
				if (!isReallySolid(iter.getType()))
					return iter.getRelative(BlockFace.DOWN);
			}
			
		} else {
			
			while (iter.getY() >= 0) {
				iter = iter.getRelative(BlockFace.DOWN);
				
				if (isReallySolid(iter.getType()))
					return iter;
			}
		}
		return point.getBlock();
	}
	
	public static boolean isReallySolid(Material mat) {
		return mat.isSolid() && !Constants.NOT_SOLIDS.contains(mat);
	}
}
