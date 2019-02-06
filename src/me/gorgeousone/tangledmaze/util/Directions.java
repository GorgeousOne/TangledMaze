package me.gorgeousone.tangledmaze.util;

import org.bukkit.util.Vector;

public enum Directions {
	
	//putting opposite values next to each other can help quicken methods like Maze.sealsMaze();
	EAST(      new Vec2( 1,  0)),
	WEST(      new Vec2(-1,  0)),
	SOUTH(     new Vec2( 0,  1)),
	NORTH(     new Vec2( 0, -1)),
	SOUTH_EAST(new Vec2( 1,  1)),
	NORTH_WEST(new Vec2(-1, -1)),
	SOUTH_WEST(new Vec2(-1,  1)),
	NORTH_EAST(new Vec2( 1, -1));

	private Vec2 facing;
	
	Directions(Vec2 facing) {
		this.facing = facing;
	}
	
	public Vec2 toVec2() {
		return facing.clone();
	}
	
	public Vector toVec3() {
		return facing.toVec3();
	}
	
	public static Directions[] cardinalValues() {
		return new Directions[] {EAST, WEST, SOUTH, NORTH};
	}
}