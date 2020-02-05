package me.gorgeousone.tangledmaze.util;

import me.gorgeousone.tangledmaze.data.Constants;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public final class BlockUtils {

	public static int nearestSurfaceY(Vec2 loc, int startY, World world) {
		return nearestSurface(new Location(world, loc.getX(), startY, loc.getZ())).getBlockY();
	}

	public static Location nearestSurface(Location loc) {

		Location iter = loc.clone();

		if(isReallySolid(iter.getBlock().getType())) {

			while(iter.getY() <= 255) {
				iter.add(0, 1, 0);

				if(!isReallySolid(iter.getBlock().getType())) {
					iter.add(0, -1, 0);
					return iter;
				}
			}

		}else {

			while(iter.getY() >= 0) {
				iter.add(0, -1, 0);

				if(isReallySolid(iter.getBlock().getType())) {
					return iter;
				}
			}
		}
		return loc;
	}

	public static boolean isReallySolid(Material mat) {
		return mat.isSolid() && !Constants.NOT_SOLIDS.contains(mat);
	}
}
