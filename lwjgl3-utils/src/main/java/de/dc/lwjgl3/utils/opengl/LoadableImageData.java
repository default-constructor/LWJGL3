package de.dc.lwjgl3.utils.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface LoadableImageData {

	ByteBuffer loadImage(InputStream is, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException;

	ByteBuffer loadImage(InputStream is, boolean flipped, int[] transparent) throws IOException;

	int getWidth();

	int getHeight();

	int getDepth();

	int getTextureWidth();

	int getTextureHeight();

}
