package me.gorgeousone.tangledmaze.generation;

import java.util.Random;

import org.bukkit.block.BlockState;

public class WallGenerator extends AbstractWallGen {
	
	private Random rnd;
	
	public WallGenerator() {
		rnd = new Random();
	}
		
	@Override
	protected void setBlock(BlockState block) {
		
		block.setType(getWallMaterials().get(rnd.nextInt(getWallMaterials().size())));
		block.update(true, false);
	}
}