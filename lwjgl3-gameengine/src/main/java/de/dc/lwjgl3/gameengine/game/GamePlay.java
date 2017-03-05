package de.dc.lwjgl3.gameengine.game;

import static org.lwjgl.glfw.GLFW.*;

import de.dc.lwjgl3.gameengine.Window;
import de.dc.lwjgl3.gameengine.core.State;
import de.dc.lwjgl3.gameengine.game.gameplay.levels.Level;
import de.dc.lwjgl3.gameengine.game.gameplay.levels.Level01;

public class GamePlay extends GameState {

	private static final Level[] LEVELS = new Level[10];

	Level currentLevel;

	public GamePlay() {
		super(State.GAME_PLAY);
//		System.out.println("GamePlay::constructor");
		LEVELS[0] = new Level01();
	}

	@Override
	public void destroy() {
		currentLevel.destroy();
	}

	@Override
	public void init() {
//		System.out.println("GamePlay::init");
		currentLevel = LEVELS[0];
		currentLevel.init();
		stateChanged = false;
	}

	@Override
	public void render() {
//		System.out.println("GamePlay::render");
		currentLevel.render();
	}

	@Override
	public void update() {
//		System.out.println("GamePlay::update");
		handleInputs();
		currentLevel.update();
	}

	private void handleInputs() {
//		System.out.println("GamePlay::handleInputs");
		int buttonState = glfwGetKey(Window.getId(), GLFW_KEY_ESCAPE);
		if (GLFW_PRESS == buttonState) {
			nextState = State.GAME_MENU;
			stateChanged = true;
		}
	}
}
