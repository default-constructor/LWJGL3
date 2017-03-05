package de.dc.lwjgl3.utils.opengl;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

public class InternalTextureLoader {

	public static InternalTextureLoader get() {
		return new InternalTextureLoader();
	}

	private static int createTextureId() {
		IntBuffer textures = createIntBuffer(1);
		glGenTextures(textures);
		return textures.get(0);
	}

	private static IntBuffer createIntBuffer(int size) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(size * 4);
		buffer.order(ByteOrder.nativeOrder());
		return buffer.asIntBuffer();
	}

	private static int get2Fold(int fold) {
		int closest = 2;
		while (closest < fold) {
			closest *= 2;
		}
		return closest;
	}

	private HashMap texturesLinear = new HashMap();
	private HashMap texturesNearest = new HashMap();

	private int pixelFormat = GL_RGBA;

	private boolean deferred = false;
	private boolean holdTextureData = false;

	private InternalTextureLoader() {
	}

	public Texture getTexture(InputStream stream, String resourceName, boolean flipped, int filter, int[] transparent) throws IOException {
		if (deferred) {
			return null; // TODO: new DeferredTexture(stream, resourceName, flipped, filter, transparent);
		}

		HashMap hash = texturesLinear;
		if (GL_NEAREST == filter) {
			hash = texturesNearest;
		}

		StringBuilder builder = new StringBuilder(resourceName);
		if (null != transparent) {
			builder.append(":").append(transparent[0]);
			builder.append(":").append(transparent[1]);
			builder.append(":").append(transparent[2]);
		}
		builder.append(":").append(flipped);
		String resName = builder.toString();

		if (holdTextureData) {
			// TODO: TextureImpl texture = (TextureImpl) hash.get(resName); ...
		} else {
			SoftReference reference = (SoftReference) hash.get(resName);
			if (null != reference) {
				TextureImpl texture = (TextureImpl) reference.get();
				if (null != texture) {
					return texture;
				} else {
					hash.remove(resName);
				}
			}
		}

		try {
			glGetError();
		} catch (NullPointerException e) {
			throw new RuntimeException("Image based resources must be loaded as part of init() or the game loop. They cannot be loaded before initialisation.");
		}

		TextureImpl texture = getTexture(stream, resourceName, GL_TEXTURE_2D, filter, filter, flipped, transparent);
		texture.setCacheName(resName);

		hash.put(resName, holdTextureData ? texture : new SoftReference(texture));

		return texture;
	}

	private TextureImpl getTexture(InputStream stream, String resourceName, int target, int magFilter, int minFilter, boolean flipped, int[] transparent)
			throws IOException {
		LoadableImageData imageData = ImageDataFactory.getImageData(resourceName);
		ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(stream), flipped, transparent);

		int textureId = createTextureId();
		TextureImpl texture = new TextureImpl(resourceName, target, textureId);

		glBindTexture(target, textureId);

		int width, height, texWidth, texHeight;
		boolean hasAlpha;

		width = imageData.getWidth();
		height = imageData.getHeight();
		hasAlpha = 32 == imageData.getDepth();

		texture.setTextureWidth(texWidth = imageData.getTextureWidth());
		texture.setTextureHeight(texHeight = imageData.getTextureHeight());

		IntBuffer buffer = BufferUtils.createIntBuffer(16);

		glGetIntegerv(GL_MAX_TEXTURE_SIZE, buffer); // INFO: LWJGL 2.* => glGetInteger(int, IntBuffer)

		int max = buffer.get(0);
		if (texWidth > max || texHeight > max) {
			throw new IOException("Attempt to allocate a texture to big for the current hardware");
		}

		int pixelFormat = hasAlpha ? GL_RGBA : GL_RGB;
		int componentCount = hasAlpha ? 4 : 3;

		texture.setWidth(width);
		texture.setHeight(height);
		texture.setAlpha(hasAlpha);

		if (holdTextureData) {
			texture.setTextureData(pixelFormat, componentCount, minFilter, magFilter, textureBuffer);
		}

		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
		glTexImage2D(target, 0, this.pixelFormat, get2Fold(width), get2Fold(height), 0, pixelFormat, GL_UNSIGNED_BYTE, textureBuffer);

		return texture;
	}
}
