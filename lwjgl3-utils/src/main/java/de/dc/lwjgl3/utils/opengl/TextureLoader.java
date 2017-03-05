package de.dc.lwjgl3.utils.opengl;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.InputStream;

public class TextureLoader {

	public static Texture getTexture(InputStream stream, String resourceName, boolean flipped, int filter) throws IOException {
		return InternalTextureLoader.get().getTexture(stream, resourceName, flipped, filter, null);
	}

	public static Texture getTexture(String format, InputStream stream) throws IOException {
		return getTexture(stream, stream.toString() + "." + format, false, GL_LINEAR);
	}
}
