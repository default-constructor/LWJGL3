package de.dc.lwjgl3.utils.opengl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class ImageIOImageData implements LoadableImageData {

	private static final ColorModel GL_ALPHA_COLOR_MODEL = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, true,
			false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);

	private static final ColorModel GL_COLOR_MODEL = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 0 }, false, false,
			ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

	@Override
	public ByteBuffer loadImage(InputStream is, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
		if (null != transparent) {
			forceAlpha = true;
		}
		BufferedImage image = ImageIO.read(is);
		return imageToByteBuffer(image, flipped, forceAlpha, transparent);
	}

	@Override
	public ByteBuffer loadImage(InputStream is, boolean flipped, int[] transparent) throws IOException {
		return loadImage(is, flipped, false, transparent);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public int getTextureWidth() {
		return textureWidth;
	}

	@Override
	public int getTextureHeight() {
		return textureHeight;
	}

	private int width;
	private int height;
	private int textureWidth;
	private int textureHeight;
	private int depth;

	private boolean edging;

	private void copyArea(BufferedImage image, int x, int y, int width, int height, int dx, int dy) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.drawImage(image.getSubimage(x, y, width, height), x + dx, y + dy, null);
	}

	private ByteBuffer imageToByteBuffer(BufferedImage image, boolean flipped, boolean forceAlpha, int[] transparent) {
		int texWidth = 2;
		int texHeight = 2;

		while (texWidth < image.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < image.getHeight()) {
			texHeight *= 2;
		}

		width = image.getWidth();
		height = image.getHeight();
		textureWidth = texWidth;
		textureHeight = texHeight;

		boolean useAlpha = image.getColorModel().hasAlpha() || forceAlpha;

		WritableRaster raster;
		BufferedImage texImage;
		if (useAlpha) {
			depth = 32;
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
			texImage = new BufferedImage(GL_ALPHA_COLOR_MODEL, raster, false, new Hashtable());
		} else {
			depth = 24;
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
			texImage = new BufferedImage(GL_COLOR_MODEL, raster, false, new Hashtable());
		}

		Graphics2D g = (Graphics2D) texImage.getGraphics();

		if (useAlpha) {
			g.setColor(new Color(0f, 0f, 0f, 0f));
			g.fillRect(0, 0, texWidth, texHeight);
		}

		if (flipped) {
			g.scale(1, -1);
			g.drawImage(image, 0, -height, null);
		} else {
			g.drawImage(image, 0, 0, null);
		}

		if (edging) {
			if (height < texHeight - 1) {
				copyArea(texImage, 0, 0, width, 1, 0, texHeight - 1);
				copyArea(texImage, 0, height - 1, width, 1, 0, 1);
			}
			if (width < texWidth - 1) {
				copyArea(texImage, 0, 0, 1, height, texWidth - 1, 0);
				copyArea(texImage, width - 1, 0, 1, height, 1, 0);
			}
		}

		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		if (null != transparent) {
			for (int i = 0; i < data.length; i += 4) {
				boolean match = true;
				for (int c = 0; c < 3; c++) {
					byte d = data[i + c];
					int value = d < 0 ? 256 + d : d;
					if (value != transparent[c]) {
						match = false;
					}
				}

				if (match) {
					data[i + 3] = 0;
				}
			}
		}

		ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put(data, 0, data.length);
		buffer.flip();
		g.dispose();

		return buffer;
	}
}
