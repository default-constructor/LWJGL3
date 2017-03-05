package de.dc.lwjgl3.gameengine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import de.dc.lwjgl3.gameengine.core.State;
import de.dc.lwjgl3.gameengine.utils.OpenGLUtil;

public class Main implements Runnable {

	public static void main(String[] args) {
		// System.out.println("Main::main");
		new Main().start();
	}

	@Override
	public void run() {
		// System.out.println("Main::run");
		BasicConfigurator.configure();
		init();
		loop(State.GAME_PLAY);
		destroy();
	}

	private boolean running;

	private Thread thread;

	private StateMachine stateMachine;

	private void destroy() {
		stateMachine.getState().destroy();
		Window.destroy();
	}

	private double getCurrentTime() {
		// System.out.println("Main::getCurrentTime");
		return (double) System.nanoTime() / (double) 1000000000L;
	}

	private void init() {
		// System.out.println("Main::init");
		Window.create("Game engine", false);

		stateMachine = new StateMachine();

		Logger.getRootLogger().info("Supported OpenGL version: " + OpenGLUtil.getVersionString());
	}

	private void loop(State state) {
		// System.out.println("Main::loop");
		stateMachine.setState(state);
		stateMachine.getState().init();

		double frameCap = 1.0 / 60.0;
		double lastTime = getCurrentTime();
		double unprocessed = 0.0;
		double frameTime = 0;
		int frames = 0;

		while (running) {
			boolean canRender = true;

			update();

			double currentTime = getCurrentTime();
			double passed = currentTime - lastTime;
			unprocessed += passed;
			frameTime += passed;
			lastTime = currentTime;

			while (unprocessed >= frameCap) {
				unprocessed -= frameCap;

				canRender = true;

				if (1.0 <= frameTime) {
					frameTime = 0;
					// System.out.println("FPS: " + frames);
					frames = 0;
				}
			}
			if (canRender) {
				render();
				frames++;
			}

			if (Window.shouldClose()) {
				running = false;
			}
		}
	}

	private void render() {
		// System.out.println("Main::render");
		stateMachine.getState().render();

		int error = glGetError();
		if (GL_NO_ERROR != error) {
			System.err.println(error);
		}

		glfwSwapBuffers(Window.getId());
	}

	private void start() {
		// System.out.println("Main::start");
		running = true;
		thread = new Thread(this, "War game engine");
		thread.start();
	}

	private void update() {
		// System.out.println("Main::update");
		glfwPollEvents();

		if (stateMachine.getState().hasStateChanged()) {
			try {
				Thread.sleep(200); // TODO check
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			loop(stateMachine.getState().getNextState());
		}

		Window.update();
		stateMachine.getState().update();
	}
}
