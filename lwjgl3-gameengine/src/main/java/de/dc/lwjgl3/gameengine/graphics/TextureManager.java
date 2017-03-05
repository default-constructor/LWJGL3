package de.dc.lwjgl3.gameengine.graphics;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.dc.lwjgl3.gameengine.utils.BufferUtil;

public class TextureManager {

	private final int id;

	private int width, height;

	public TextureManager(String name) {
//		System.out.println("Texture::constructor");
		id = load(name);
	}

	public void bind() {
//		System.out.println("Texture::bind");
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void unbind() {
//		System.out.println("Texture::unbind");
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	private int load(String name) {
//		System.out.println("Texture::load");

		int[] pixels = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream("res/textures/" + name + ".png"));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			System.err.println("Could not read image 'res/textures/" + name + "'.png.");
			e.printStackTrace();
		}

		int[] data = new int[width * height];
		for (int i = 0; i < data.length; i++) {
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);

			data[i] = a << 24 | b << 16 | g << 8 | r;
		}

		int textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtil.createIntBuffer(data));
		glBindTexture(GL_TEXTURE_2D, 0);

		return 0;
	}
}
