package de.dc.lwjgl3.gameengine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

public class Window {

	private static long id;

	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;

	private static final int OPENGL_MAJOR_VERSION = 4;
	private static final int OPENGL_MINOR_VERSION = 3;

	public static Input input;

	private static GLFWVidMode vidMode;

	private static GLFWErrorCallback errorCallback;

	private static long lastFrameTime;
	private static float delta;

	public static void create(String title, boolean fullscreen) {
		// System.out.println("Window::create");

		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		if (!glfwInit()) {
			throw new IllegalStateException("Failed to init GLFW.");
		}

		glfwDefaultWindowHints();

		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, OPENGL_MAJOR_VERSION);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, OPENGL_MINOR_VERSION);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		long monitorId;
		if (fullscreen) {
			monitorId = glfwGetPrimaryMonitor();
		} else {
			monitorId = NULL;
		}

		id = glfwCreateWindow(WIDTH, HEIGHT, title, monitorId, NULL);
		if (NULL == id) {
			throw new IllegalStateException("Could not create window.");
		}

		if (!fullscreen) {
			vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(id, (vidMode.width() - WIDTH) / 2, (vidMode.height() - HEIGHT) / 2);
		}

		glfwMakeContextCurrent(id);

		glfwShowWindow(id);

		createCapabilities();

		input = new Input(id);
		input.init();
	}

	public static void destroy() {
		// System.out.println("Window::destroy");
		glfwSetWindowShouldClose(id, true);
		glfwDestroyWindow(id);
		glfwTerminate();
		errorCallback.free();
	}

	public static int getDisplayWidth() {
		// System.out.println("Window::getDisplayWidth");
		return vidMode.width();
	}

	public static int getDisplayHeight() {
		// System.out.println("Window::getDisplayHeight");
		return vidMode.height();
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	public static long getId() {
		// System.out.println("Window::getId");
		return id;
	}

	public static boolean shouldClose() {
		// System.out.println("Window::shouldClose");
		return Thread.currentThread().isInterrupted() || glfwWindowShouldClose(Window.getId());
	}

	public static void update() {
		input.update();
		long currentFrameTime = getCurrentTime();
		System.out.println(currentFrameTime);
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}

	private static long getCurrentTime() {
		return (long) (glfwGetTime() * 1000 / glfwGetTimerFrequency());
	}
}
