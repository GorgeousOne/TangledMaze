package me.gorgeousone.tangledmaze.util;

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
	
	/**
	 * Returns if the diretion's vector is pointing towards positive or negative (with it's x or z coordinate)
	 */
	public boolean isPositive() {
		return facing.getZ() == 0 ? facing.getX() == 1 : facing.getZ() == 1;
	}
	
	/**
	 * Returns if the x coordinate of the direction's vector is not 0
	 */
	public boolean isXAligned() {
		return facing.getX() != 0;
	}
	
	/**
	 * Returns if the z coordinate of the direction's vector is not 0
	 */
//	public boolean isZAligned() {
//		return facing.getZ() != 0;
//	}
	
	public Vec2 getVec2() {
		return facing.clone();
	}
	
	public static Directions[] cardinalValues() {
		return new Directions[] {EAST, WEST, SOUTH, NORTH};
	}
	
//	public static Directions cardinalValueOf(Vec2 vec) {
//
//		if(vec.getX() != 0)
//			return vec.getX() > 0 ? EAST : WEST;
//
//		return vec.getZ() > 0 ? SOUTH : NORTH;
//	}
}