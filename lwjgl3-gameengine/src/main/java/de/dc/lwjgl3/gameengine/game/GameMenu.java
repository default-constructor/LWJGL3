package de.dc.lwjgl3.gameengine.game;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.opengl.GL11;

import de.dc.lwjgl3.gameengine.Window;
import de.dc.lwjgl3.gameengine.core.State;

public class GameMenu extends GameState {

	private static final float RED = 0.75f;
	private static final float GREEN = 0.75f;
	private static final float BLUE =0.75f;

	public GameMenu() {
		super(State.GAME_MENU);
//		System.out.println("GameMenu::constructor");
	}

	@Override
	public void destroy() {
		//
	}

	@Override
	public void init() {
//		System.out.println("GameMenu::init");
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);

		stateChanged = false;
	}

	@Override
	public void render() {
//		System.out.println("GameMenu::render");
	}

	@Override
	public void update() {
//		System.out.println("GameMenu::update");
		handleInputs();
	}

	private void handleInputs() {
//		System.out.println("GameMenu::handleInputs");
		int inputState = glfwGetKey(Window.getId(), GLFW_KEY_ESCAPE);
		if (GLFW_PRESS == inputState) {
			nextState = State.GAME_PLAY;
			stateChanged = true;
		}
	}
}
