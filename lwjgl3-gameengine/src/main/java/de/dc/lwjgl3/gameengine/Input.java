package de.dc.lwjgl3.gameengine;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.input.Keyboard;

public class Input {

	private class KeyboardListener extends GLFWKeyCallback {

		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			keysPressed[scancode] = GLFW_PRESS == action;
			keysRepeated[scancode] = GLFW_REPEAT == action;
//			keysReleased[scancode] = GLFW_RELEASE == action;
//			if (GLFW_PRESS == action) {
//				keysPressed[scancode] = true;
//				keysReleased[scancode] = false;
//			} else if (GLFW_RELEASE == action) {
//				keysReleased[scancode] = true;
//				keysPressed[scancode] = false;
//			}
		}
	}

	private class MouseButtonListener extends GLFWMouseButtonCallback {

		@Override
		public void invoke(long window, int button, int action, int mods) {
			if (GLFW_PRESS == action) {
				mouseButtonsPressed[button] = true;
			} else if (GLFW_RELEASE == action) {
				mouseButtonsReleased[button] = true;
			}
		}
	}

	private class MouseMotionListener extends GLFWCursorPosCallback {

		private int oldPositionX = 0;
		private int oldPositionY = 0;

		@Override
		public void invoke(long window, double xpos, double ypos) {
			mousePositionX = (int) xpos;
			mousePositionY = (int) ypos;
			mousePositionDeltaX = (int) xpos - oldPositionX;
			mousePositionDeltaY = (int) ypos - oldPositionY;
			oldPositionX = (int) xpos;
			oldPositionY = (int) ypos;
			mouseMoves = true;
		}
	}

	private class MouseScrollListener extends GLFWScrollCallback {

		@Override
		public void invoke(long window, double xoffset, double yoffset) {
			mouseWheelX = (int) xoffset;
			mouseWheelY = (int) yoffset;
			mouseScrollsUp = 0 < mouseWheelY;
			mouseScrollsDown = 0 > mouseWheelY;
		}
	}

	private static long windowId;

	private GLFWKeyCallback keyListener;

	private boolean[] keysPressed = new boolean[349];
	private boolean[] keysReleased = new boolean[349];
	private boolean[] keysRepeated = new boolean[349];

	private GLFWMouseButtonCallback mouseButtonListener;
	private GLFWCursorPosCallback mouseMotionListener;
	private GLFWScrollCallback mouseScrollListener;

	private boolean[] mouseButtonsPressed = new boolean[8];
	private boolean[] mouseButtonsReleased = new boolean[8];

	private boolean mouseButtonLeftPressed = false, mouseButtonMiddlePressed = false, mouseButtonRightPressed = false;
	private boolean mouseButtonLeftReleased = false, mouseButtonMiddleReleased = false, mouseButtonRightReleased = false;

	private double mousePositionX = 0.0, mousePositionY = 0.0;
	private int mousePositionDeltaX = 0, mousePositionDeltaY = 0;
	private boolean mouseMoves = false;

	private int mouseWheelX = 0, mouseWheelY = 0;
	private boolean mouseScrollsUp = false, mouseScrollsDown = false;

	private boolean keyDownPressed;

	public Input(long windowId) {
		Input.windowId = windowId;
		keyListener = new KeyboardListener();
		mouseButtonListener = new MouseButtonListener();
		mouseMotionListener = new MouseMotionListener();
		mouseScrollListener = new MouseScrollListener();
	}

	public void init() {
		glfwSetKeyCallback(windowId, keyListener);
		glfwSetMouseButtonCallback(windowId, mouseButtonListener);
		glfwSetCursorPosCallback(windowId, mouseMotionListener);
		glfwSetScrollCallback(windowId, mouseScrollListener);
	}

	public void update() {
		for (int i = 0; i < mouseButtonsPressed.length; i++) {
			mouseButtonsPressed[i] = false;
			mouseButtonsReleased[i] = false;
		}
		mouseButtonLeftPressed = false;
		mouseButtonLeftReleased = false;
		mouseButtonMiddlePressed = false;
		mouseButtonMiddleReleased = false;
		mouseButtonRightPressed = false;
		mouseButtonRightReleased = false;
		mouseMoves = false;
		mousePositionX = 0.0;
		mousePositionY = 0.0;
		mousePositionDeltaX = 0;
		mousePositionDeltaY = 0;
		mouseScrollsDown = false;
		mouseScrollsUp = false;
		mouseWheelX = 0;
		mouseWheelY = 0;
	}

	public boolean isMouseButtonLeftPressed() {
		return mouseButtonsPressed[GLFW_MOUSE_BUTTON_LEFT];
	}

	public boolean isMouseButtonMiddlePressed() {
		return mouseButtonsPressed[GLFW_MOUSE_BUTTON_MIDDLE];
	}

	public boolean isMouseButtonRightPressed() {
		return mouseButtonsPressed[GLFW_MOUSE_BUTTON_RIGHT];
	}

	public boolean isMouseButtonLeftReleased() {
		return mouseButtonsReleased[GLFW_MOUSE_BUTTON_LEFT];
	}

	public boolean isMouseButtonMiddleReleased() {
		return mouseButtonsReleased[GLFW_MOUSE_BUTTON_MIDDLE];
	}

	public boolean isMouseButtonRightReleased() {
		return mouseButtonsReleased[GLFW_MOUSE_BUTTON_RIGHT];
	}

	public double getMousePositionX() {
		return mousePositionX;
	}

	public double getMousePositionY() {
		return mousePositionY;
	}

	public int getMousePositionDeltaX() {
		return mousePositionDeltaX;
	}

	public int getMousePositionDeltaY() {
		return mousePositionDeltaY;
	}

	public boolean isMouseMoves() {
		return mouseMoves;
	}

	public int getMouseWheelX() {
		return mouseWheelX;
	}

	public int getMouseWheelY() {
		return mouseWheelY;
	}

	public boolean isMouseScrollsUp() {
		return mouseScrollsUp;
	}

	public boolean isMouseScrollsDown() {
		return mouseScrollsDown;
	}

//	public boolean isKeyBackwardPressed() {
//		return keysPressed[Keyboard.KEY_S] || keysRepeated[Keyboard.KEY_S];
//	}
//
//	public boolean isKeyForwardPressed() {
//		return keysPressed[Keyboard.KEY_W] || keysRepeated[Keyboard.KEY_W];
//	}
//
//	public boolean isKeyLeftPressed() {
//		return keysPressed[Keyboard.KEY_A] || keysRepeated[Keyboard.KEY_A];
//	}
//
//	public boolean isKeyRightPressed() {
//		return keysPressed[Keyboard.KEY_D] || keysRepeated[Keyboard.KEY_D];
//	}
//
//	public boolean isKeySpacePressed() {
//		return keysPressed[Keyboard.KEY_SPACE];
//	}

	public boolean isKeyPressed(int key) {
		return keysPressed[key] || keysRepeated[key];
	}
}
