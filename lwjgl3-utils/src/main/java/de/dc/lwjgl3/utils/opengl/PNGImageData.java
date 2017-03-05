package de.dc.lwjgl3.utils.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class PNGImageData implements LoadableImageData {

	private static int get2Fold(int fold) {
		int closest = 2;
		while (closest < fold) {
			closest *= 2;
		}
		return closest;
	}

	private ByteBuffer scratch;

	@Override
	public ByteBuffer loadImage(InputStream is, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
		if (null != transparent) {
			forceAlpha = true;
			// throw new IOException("Transparent color not support in custom PNG Decoder");
		}

		PNGDecoder decoder = new PNGDecoder(is);

		if (!decoder.isRGB()) {
			throw new IOException("Only RGB formatted images are supported by the PNGLoader");
		}

		width = decoder.getWidth();
		height = decoder.getHeight();
		textureWidth = get2Fold(width);
		textureHeight = get2Fold(height);

		int bpp = decoder.hasAlpha() ? 4 : 3;
		bitDepth = decoder.hasAlpha() ? 32 : 24;

		scratch = BufferUtils.createByteBuffer(textureWidth * textureHeight * bpp);
		decoder.decode(scratch, textureWidth * bpp, bpp == 4 ? PNGDecoder.RGBA : PNGDecoder.RGB);

		if (height < textureHeight - 1) {
			int topOffset = (textureHeight - 1) * textureWidth * bpp;
			int bottomOffset = (height - 1) * textureWidth * bpp;
			for (int x = 0; x < textureWidth; x++) {
				for (int i = 0; i < bpp; i++) {
					scratch.put(topOffset + x + i, scratch.get(x + i));
					scratch.put(bottomOffset + textureWidth * bpp + x + i, scratch.get(bottomOffset + x + i));
				}
			}
		}

		if (width < textureWidth - 1) {
			for (int y = 0; y < textureHeight; y++) {
				for (int i = 0; i < bpp; i++) {
					scratch.put((y + 1) * textureWidth * bpp - bpp + i, scratch.get(y * textureWidth * bpp + i));
				}
			}
		}

		if (!decoder.hasAlpha() && forceAlpha) {
			ByteBuffer buffer = BufferUtils.createByteBuffer(textureWidth * textureHeight * 4);
			for (int x = 0; x < textureWidth; x++) {
				for (int y = 0; y < textureHeight; y++) {
					int srcOffset = y * 3 + x * textureHeight * 3;
					int dstOffset = y * 4 + x * textureHeight * 4;

					buffer.put(dstOffset, scratch.get(srcOffset));
					buffer.put(dstOffset + 1, scratch.get(srcOffset + 1));
					buffer.put(dstOffset + 2, scratch.get(srcOffset + 2));
					if (x < getHeight() && y < getWidth()) {
						buffer.put(dstOffset + 3, (byte) 255);
					} else {
						buffer.put(dstOffset + 3, (byte) 0);
					}
				}
			}

			bitDepth = 32;
			scratch = buffer;
		}

		if (null != transparent) {
			for (int i = 0; i < textureWidth * textureHeight * 4; i += 4) {
				boolean match = true;
				for (int c = 0; c < 3; c++) {
					if (toInt(scratch.get(i + c)) != transparent[c]) {
						match = false;
					}
				}
				if (match) {
					scratch.put(i + 3, (byte) 0);
				}
			}
			scratch.position(0);
		}

		return scratch;
	}

	private int toInt(byte b) {
		if (b < 0) {
			return 256+b;
		}
		return b;
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
	private int depth;
	private int textureWidth;
	private int textureHeight;

	private int bitDepth;
}
