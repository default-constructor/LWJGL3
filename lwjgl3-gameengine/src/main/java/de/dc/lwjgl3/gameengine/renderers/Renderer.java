package de.dc.lwjgl3.gameengine.renderers;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

	protected static void disableCulling() {
//		System.out.println("Renderer::disableCulling");
		glDisable(GL_CULL_FACE);
	}

	protected static void enableCulling() {
//		System.out.println("Renderer::enableCulling");
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}

	protected static void prepare(float red, float green, float blue) {
//		System.out.println("Renderer::prepare");
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(red, green, green, 1.0f);
	}

	protected Renderer() {
//		System.out.println("Renderer::constructor");
	}
}
