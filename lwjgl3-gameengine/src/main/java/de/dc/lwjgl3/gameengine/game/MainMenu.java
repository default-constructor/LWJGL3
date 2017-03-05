package de.dc.lwjgl3.gameengine.game;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.opengl.GL11;

import de.dc.lwjgl3.gameengine.Window;
import de.dc.lwjgl3.gameengine.core.State;

public class MainMenu extends GameState {

	private static final float RED = 0.25f;
	private static final float GREEN = 0.25f;
	private static final float BLUE = 0.25f;

	public MainMenu() {
		super(State.MAIN_MENU);
//		System.out.println("MainMenu::constructor");
	}

	@Override
	public void destroy() {
		//
	}

	@Override
	public void init() {
//		System.out.println("MainMenu::init");
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);

		stateChanged = false;
	}

	@Override
	public void render() {
//		System.out.println("MainMenu::render");
	}

	@Override
	public void update() {
//		System.out.println("MainMenu::update");
		handleInputs();
	}

	private void handleInputs() {
//		System.out.println("MainMenu::handleInputs");
		int inputState = glfwGetMouseButton(Window.getId(), GLFW_MOUSE_BUTTON_1);
		if (GLFW_PRESS == inputState) {
			nextState = State.GAME_MENU;
			stateChanged = true;
		}
	}
}
