package de.dc.lwjgl3.gameengine.game;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.opengl.GL11;

import de.dc.lwjgl3.gameengine.Window;
import de.dc.lwjgl3.gameengine.core.State;

public class Intro extends GameState {

	private static final float RED = 0.0f;
	private static final float GREEN = 0.0f;
	private static final float BLUE = 0.0f;

	public Intro() {
		super(State.INTRO);
//		System.out.println("Intro::constructor");
	}

	@Override
	public void destroy() {
		//
	}

	@Override
	public void init() {
//		System.out.println("Intro::init");
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);

		stateChanged = false;
	}

	@Override
	public void render() {
//		System.out.println("Intro::render");
	}

	@Override
	public void update() {
//		System.out.println("Intro::update");
		handleInputs();
	}

	private void handleInputs() {
//		System.out.println("Intro::handleInputs");
		int inputState = glfwGetKey(Window.getId(), GLFW_KEY_ESCAPE);
		if (GLFW_PRESS == inputState) {
			nextState = State.MAIN_MENU;
			stateChanged = true;
		}
	}
}
