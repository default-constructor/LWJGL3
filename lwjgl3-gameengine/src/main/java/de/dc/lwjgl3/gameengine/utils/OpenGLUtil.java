package de.dc.lwjgl3.gameengine.utils;

import static org.lwjgl.opengl.GL11.*;

public final class OpenGLUtil {

	public static String getVersionString() {
		return glGetString(GL_VERSION);
	}
}
