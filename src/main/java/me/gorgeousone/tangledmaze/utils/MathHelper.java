package me.gorgeousone.tangledmaze.utils;

public final class MathHelper {
	
	private MathHelper() {}
	
	public static int clamp(int value, int min, int max) {
		return Math.min(max, Math.max(min, value));
	}
}
