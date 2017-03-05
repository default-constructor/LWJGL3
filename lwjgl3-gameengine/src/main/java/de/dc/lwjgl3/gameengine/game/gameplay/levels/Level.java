package de.dc.lwjgl3.gameengine.game.gameplay.levels;

public abstract class Level {

	public static final float FIELD_OF_VIEW = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000f;

	public Level() {
//		System.out.println("Level::constructor");
	}

	public abstract void destroy();
	
	public abstract void init();
	
	public abstract void render();
	
	public abstract void update();
}
