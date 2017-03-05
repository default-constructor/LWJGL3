package de.dc.lwjgl3.utils.opengl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PNGDecoder {

	public static class Format {

		final int numComponents;
		final boolean alpha;

		private Format(int numComponents, boolean alpha) {
			this.numComponents = numComponents;
			this.alpha = alpha;
		}

		public int getNumComponents() {
			return numComponents;
		}

		public boolean isAlpha() {
			return alpha;
		}
	}

	public static final Format ABGR = new Format(4, true);
	public static final Format ALPHA = new Format(1, true);
	public static final Format BGRA = new Format(4, true);
	public static final Format LUMINANCE = new Format(1, false);
	public static final Format LUMINANCE_ALPHA = new Format(2, true);
	public static final Format RGB = new Format(3, false);
	public static final Format RGBA = new Format(4, true);

	private static final byte COLOR_GREYALPHA = 4;
	private static final byte COLOR_GREYSCALE = 0;
	private static final byte COLOR_INDEXED = 3;
	private static final byte COLOR_TRUEALPHA = 6;
	private static final byte COLOR_TRUECOLOR = 2;

	private static final byte[] SIGNATURE = { (byte) 137, 80, 78, 71, 13, 10, 26, 10 };

	private static final int IDAT = 0x49444154;
	private static final int IHDR = 0x49484452;
	private static final int PLTE = 0x504C5445;
	private static final int tRNS = 0x74524E53;

	private InputStream inputStream;
	private CRC32 crc;
	private byte[] buffer;

	private int width;
	private int height;
	private int bitDepth;
	private int colorType;
	private int bytesPerPixel;
	private int chunkRemaining;
	private int chunkType;
	private int chunkLength;
	private byte[] palette;
	private byte[] paletteA;
	private byte[] transPixel;

	public PNGDecoder(InputStream is) throws IOException {
		this.inputStream = is;
		this.crc = new CRC32();
		this.buffer = new byte[4096];

		readFully(buffer, 0, SIGNATURE.length);
		if (!checkSignature(buffer)) {
			throw new IOException("Not a valid PNG file");
		}

		openChunk(IHDR);
		readIHDR();
		closeChunk();

		searchIDAT: for (;;) {
			openChunk();
			switch (chunkType) {
			case IDAT:
				break searchIDAT;
			case PLTE:
				readPLTE();
				break;
			case tRNS:
				readtRNS();
				break;
			}
			closeChunk();
		}

		if (colorType == COLOR_INDEXED && palette == null) {
			throw new IOException("Missing PLTE chunk");
		}
	}

	private void readPLTE() throws IOException {
		int paletteEntries = chunkLength / 3;
		if (paletteEntries < 1 || paletteEntries > 256 || (chunkLength % 3) != 0) {
			throw new IOException("PLTE chunk has wrong length");
		}
		palette = new byte[paletteEntries * 3];
		readChunk(palette, 0, palette.length);
	}

	private void readtRNS() throws IOException {
		switch (colorType) {
		case COLOR_GREYSCALE:
			checkChunkLength(2);
			transPixel = new byte[2];
			readChunk(transPixel, 0, 2);
			break;
		case COLOR_TRUECOLOR:
			checkChunkLength(6);
			transPixel = new byte[6];
			readChunk(transPixel, 0, 6);
			break;
		case COLOR_INDEXED:
			if (palette == null) {
				throw new IOException("tRNS chunk without PLTE chunk");
			}
			paletteA = new byte[palette.length / 3];
			Arrays.fill(paletteA, (byte) 0xFF);
			readChunk(paletteA, 0, paletteA.length);
			break;
		default:
			// just ignore it
		}
	}

	private void checkChunkLength(int expected) throws IOException {
		if (chunkLength != expected) {
			throw new IOException("Chunk has wrong size");
		}
	}

	private void readIHDR() throws IOException {
		checkChunkLength(13);
		readChunk(buffer, 0, 13);
		width = readInt(buffer, 0);
		height = readInt(buffer, 4);
		bitDepth = buffer[8] & 255;
		colorType = buffer[9] & 255;

		switch (colorType) {
		case COLOR_GREYSCALE:
			if (bitDepth != 8) {
				throw new IOException("Unsupported bit depth: " + bitDepth);
			}
			bytesPerPixel = 1;
			break;
		case COLOR_GREYALPHA:
			if (bitDepth != 8) {
				throw new IOException("Unsupported bit depth: " + bitDepth);
			}
			bytesPerPixel = 2;
			break;
		case COLOR_TRUECOLOR:
			if (bitDepth != 8) {
				throw new IOException("Unsupported bit depth: " + bitDepth);
			}
			bytesPerPixel = 3;
			break;
		case COLOR_TRUEALPHA:
			if (bitDepth != 8) {
				throw new IOException("Unsupported bit depth: " + bitDepth);
			}
			bytesPerPixel = 4;
			break;
		case COLOR_INDEXED:
			switch (bitDepth) {
			case 8:
			case 4:
			case 2:
			case 1:
				bytesPerPixel = 1;
				break;
			default:
				throw new IOException("Unsupported bit depth: " + bitDepth);
			}
			break;
		default:
			throw new IOException("unsupported color format: " + colorType);
		}

		if (buffer[10] != 0) {
			throw new IOException("unsupported compression method");
		}
		if (buffer[11] != 0) {
			throw new IOException("unsupported filtering method");
		}
		if (buffer[12] != 0) {
			throw new IOException("unsupported interlace method");
		}
	}

	private boolean checkSignature(byte[] buffer2) {
		for (int i = 0; i < SIGNATURE.length; i++) {
			if (buffer[i] != SIGNATURE[i]) {
				return false;
			}
		}
		return true;
	}

	public boolean isRGB() {
		return COLOR_TRUEALPHA == colorType || COLOR_TRUECOLOR == colorType || COLOR_INDEXED == colorType;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean hasAlpha() {
		return COLOR_TRUEALPHA == colorType || null != paletteA || null != transPixel;
	}

	public void decode(ByteBuffer buffer, int stride, Format format) throws IOException {
		final int offset = buffer.position();
		final int lineSize = (width * bitDepth + 7) / 8 * bytesPerPixel;
		byte[] curLine = new byte[lineSize + 1];
		byte[] prevLine = new byte[lineSize + 1];
		byte[] palLine = 8 > bitDepth ? new byte[width + 1] : null;

		Inflater inflater = new Inflater();
		try {
			for (int y = 0; y < height; y++) {
				readChunkUnzip(inflater, curLine, 0, curLine.length);
				unfilter(curLine, prevLine);
				buffer.position(offset + y * stride);
				switch (colorType) {
				case COLOR_TRUECOLOR:
					if (ABGR == format) {
						copyRGBToABGR(buffer, curLine);
					} else if (RGBA == format) {
						copyRGBToRGBA(buffer, curLine);
					} else if (RGB == format) {
						copy(buffer, curLine);
					} else {
						throw new UnsupportedOperationException("Unsupported format for this image.");
					}
					break;
				case COLOR_TRUEALPHA:
					if (ABGR == format) {
						copyRGBAToABGR(buffer, curLine);
					} else if (RGBA == format) {
						copy(buffer, curLine);
					} else if (BGRA == format) {
						copyRGBAToBGRA(buffer, curLine);
					} else if (RGB == format) {
						copyRGBAToRGB(buffer, curLine);
					} else {
						throw new UnsupportedOperationException("Unsupported format for this image.");
					}
					break;
				case COLOR_GREYSCALE:
					if (LUMINANCE == format || ALPHA == format) {
						copy(buffer, curLine);
					} else {
						throw new UnsupportedOperationException("Unsupported format for this image.");
					}
					break;
				case COLOR_GREYALPHA:
					if (LUMINANCE_ALPHA == format) {
						copy(buffer, curLine);
					} else {
						throw new UnsupportedOperationException("Unsupported format for this image.");
					}
					break;
				case COLOR_INDEXED:
					switch (bitDepth) {
					case 8:
						palLine = curLine;
						break;
					case 4:
						expand4(curLine, palLine);
						break;
					case 2:
						expand2(curLine, palLine);
						break;
					case 1:
						expand1(curLine, palLine);
						break;
					default:
						throw new UnsupportedOperationException("Unsupported format for this image.");
					}
					if (ABGR == format) {
						copyPALToABGR(buffer, palLine);
					} else if (RGBA == format) {
						copyPALToRGBA(buffer, palLine);
					} else if (BGRA == format) {
						copyPALToBGRA(buffer, palLine);
					} else {
						throw new UnsupportedOperationException("Unsupported format for this image.");
					}
					break;
				default:
					throw new UnsupportedOperationException("Not yet implemented.");
				}

				byte[] tmp = curLine;
				curLine = prevLine;
				prevLine = tmp;
			}
		} finally {
			inflater.end();
		}
	}

	private void copy(ByteBuffer buffer, byte[] curLine) {
		buffer.put(curLine, 1, curLine.length - 1);
	}

	private void copyPALToBGRA(ByteBuffer buffer, byte[] curLine) {
		if (paletteA != null) {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3 + 0];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = paletteA[idx];
				buffer.put(b).put(g).put(r).put(a);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3 + 0];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = (byte) 0xFF;
				buffer.put(b).put(g).put(r).put(a);
			}
		}
	}

	private void copyPALToRGBA(ByteBuffer buffer, byte[] curLine) {
		if (paletteA != null) {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3 + 0];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = paletteA[idx];
				buffer.put(r).put(g).put(b).put(a);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3 + 0];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = (byte) 0xFF;
				buffer.put(r).put(g).put(b).put(a);
			}
		}
	}

	private void copyPALToABGR(ByteBuffer buffer, byte[] curLine) {
		if (paletteA != null) {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3 + 0];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = paletteA[idx];
				buffer.put(a).put(b).put(g).put(r);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3 + 0];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = (byte) 0xFF;
				buffer.put(a).put(b).put(g).put(r);
			}
		}
	}

	private void copyRGBAToRGB(ByteBuffer buffer, byte[] curLine) {
		for (int i = 1, n = curLine.length; i < n; i += 4) {
			buffer.put(curLine[i]).put(curLine[i + 1]).put(curLine[i + 2]);
		}
	}

	private void copyRGBAToABGR(ByteBuffer buffer, byte[] curLine) {
		if (null != transPixel) {
			byte tr = transPixel[1];
			byte tg = transPixel[3];
			byte tb = transPixel[5];
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				byte r = curLine[i];
				byte g = curLine[i + 1];
				byte b = curLine[i + 2];
				byte a = (byte) 0xFF;
				if (r == tr && g == tg && b == tb) {
					a = 0;
				}
				buffer.put(a).put(b).put(g).put(r);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				buffer.put((byte) 0xFF).put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]);
			}
		}
	}

	private void copyRGBAToBGRA(ByteBuffer buffer, byte[] curLine) {
		if (null != transPixel) {
			byte tr = transPixel[1];
			byte tg = transPixel[3];
			byte tb = transPixel[5];
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				byte r = curLine[i];
				byte g = curLine[i + 1];
				byte b = curLine[i + 2];
				byte a = (byte) 0xFF;
				if (r == tr && g == tg && b == tb) {
					a = 0;
				}
				buffer.put(b).put(g).put(r).put(a);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				buffer.put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]).put((byte) 0xFF);
			}
		}
	}

	private void copyRGBToRGBA(ByteBuffer buffer, byte[] curLine) {
		if (null != transPixel) {
			byte tr = transPixel[1];
			byte tg = transPixel[3];
			byte tb = transPixel[5];
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				byte r = curLine[i];
				byte g = curLine[i + 1];
				byte b = curLine[i + 2];
				byte a = (byte) 0xFF;
				if (r == tr && g == tg && b == tb) {
					a = 0;
				}
				buffer.put(r).put(g).put(b).put(a);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				buffer.put(curLine[i]).put(curLine[i + 1]).put(curLine[i + 2]).put((byte) 0xFF);
			}
		}
	}

	private void copyRGBToABGR(ByteBuffer buffer, byte[] curLine) {
		if (transPixel != null) {
			byte tr = transPixel[1];
			byte tg = transPixel[3];
			byte tb = transPixel[5];
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				byte r = curLine[i];
				byte g = curLine[i + 1];
				byte b = curLine[i + 2];
				byte a = (byte) 0xFF;
				if (r == tr && g == tg && b == tb) {
					a = 0;
				}
				buffer.put(a).put(b).put(g).put(r);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				buffer.put((byte) 0xFF).put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]);
			}
		}
	}

	private void expand1(byte[] src, byte[] dst) {
		for (int i = 1, n = dst.length; i < n; i += 8) {
			int val = src[1 + (i >> 3)] & 255;
			switch (n - i) {
			default:
				dst[i + 7] = (byte) ((val) & 1);
			case 7:
				dst[i + 6] = (byte) ((val >> 1) & 1);
			case 6:
				dst[i + 5] = (byte) ((val >> 2) & 1);
			case 5:
				dst[i + 4] = (byte) ((val >> 3) & 1);
			case 4:
				dst[i + 3] = (byte) ((val >> 4) & 1);
			case 3:
				dst[i + 2] = (byte) ((val >> 5) & 1);
			case 2:
				dst[i + 1] = (byte) ((val >> 6) & 1);
			case 1:
				dst[i] = (byte) ((val >> 7));
			}
		}
	}

	private void expand2(byte[] src, byte[] dst) {
		for (int i = 1, n = dst.length; i < n; i += 4) {
			int val = src[1 + (i >> 2)] & 255;
			switch (n - i) {
			default:
				dst[i + 3] = (byte) ((val) & 3);
			case 3:
				dst[i + 2] = (byte) ((val >> 2) & 3);
			case 2:
				dst[i + 1] = (byte) ((val >> 4) & 3);
			case 1:
				dst[i] = (byte) ((val >> 6));
			}
		}
	}

	private void expand4(byte[] src, byte[] dst) {
		for (int i = 1, n = dst.length; i < n; i += 2) {
			int val = src[1 + (i >> 1)] & 255;
			switch (n - i) {
			default:
				dst[i + 1] = (byte) (val & 15);
			case 1:
				dst[i] = (byte) (val >> 4);
			}
		}
	}

	private void unfilter(byte[] curLine, byte[] prevLine) throws IOException {
		switch (curLine[0]) {
		case 0: // none
			break;
		case 1:
			unfilterSub(curLine);
			break;
		case 2:
			unfilterUp(curLine, prevLine);
			break;
		case 3:
			unfilterAverage(curLine, prevLine);
			break;
		case 4:
			unfilterPaeth(curLine, prevLine);
			break;
		default:
			throw new IOException("invalide filter type in scanline: " + curLine[0]);
		}
	}

	private void unfilterPaeth(byte[] curLine, byte[] prevLine) {
		final int bpp = this.bytesPerPixel;

		int i;
		for (i = 1; i <= bpp; ++i) {
			curLine[i] += prevLine[i];
		}
		for (int n = curLine.length; i < n; ++i) {
			int a = curLine[i - bpp] & 255;
			int b = prevLine[i] & 255;
			int c = prevLine[i - bpp] & 255;
			int p = a + b - c;
			int pa = p - a;
			if (pa < 0)
				pa = -pa;
			int pb = p - b;
			if (pb < 0)
				pb = -pb;
			int pc = p - c;
			if (pc < 0)
				pc = -pc;
			if (pa <= pb && pa <= pc)
				c = a;
			else if (pb <= pc)
				c = b;
			curLine[i] += (byte) c;
		}
	}

	private void unfilterAverage(byte[] curLine, byte[] prevLine) {
		final int bpp = this.bytesPerPixel;

		int i;
		for (i = 1; i <= bpp; ++i) {
			curLine[i] += (byte) ((prevLine[i] & 0xFF) >>> 1);
		}
		for (int n = curLine.length; i < n; ++i) {
			curLine[i] += (byte) (((prevLine[i] & 0xFF) + (curLine[i - bpp] & 0xFF)) >>> 1);
		}
	}

	private void unfilterUp(byte[] curLine, byte[] prevLine) {
		final int bpp = this.bytesPerPixel;
		for (int i = 1, n = curLine.length; i < n; ++i) {
			curLine[i] += prevLine[i];
		}
	}

	private void unfilterSub(byte[] curLine) {
		final int bpp = this.bytesPerPixel;
		for (int i = bpp + 1, n = curLine.length; i < n; ++i) {
			curLine[i] += curLine[i - bpp];
		}
	}

	private void readChunkUnzip(Inflater inflater, byte[] buffer, int offset, int length) throws IOException {
		try {
			do {
				int read = inflater.inflate(buffer, offset, length);
				if (0 >= read) {
					if (inflater.finished()) {
						throw new EOFException();
					}
					if (inflater.needsInput()) {
						refillInflater(inflater);
					} else {
						throw new IOException("Cant't inflate " + length + " bytes.");
					}
				} else {
					offset += read;
					length -= read;
				}
			} while (0 < length);
		} catch (DataFormatException e) {
			// TODO: handle exception
		}
	}

	private void refillInflater(Inflater inflater) throws IOException {
		while (0 == chunkRemaining) {
			closeChunk();
			openChunk(IDAT);
		}
		int read = readChunk(buffer, 0, buffer.length);
		inflater.setInput(buffer, 0, read);
	}

	private int readChunk(byte[] buffer, int offset, int length) throws IOException {
		if (length > chunkRemaining) {
			length = chunkRemaining;
		}
		readFully(buffer, offset, length);
		crc.update(buffer, offset, length);
		chunkRemaining -= length;
		return length;
	}

	private void readFully(byte[] buffer, int offset, int length) throws IOException {
		do {
			int read = inputStream.read(buffer, offset, length);
			if (0 > read) {
				throw new EOFException();
			}
			offset += read;
			length -= read;
		} while (0 < length);
	}

	private void openChunk(int expected) throws IOException {
		openChunk();
		if (chunkType != expected) {
			throw new IOException("Expected chunk: " + Integer.toHexString(expected));
		}
	}

	private void openChunk() throws IOException {
		readFully(buffer, 0, 8);
		chunkLength = readInt(buffer, 0);
		chunkType = readInt(buffer, 4);
		chunkRemaining = chunkLength;
		crc.reset();
		crc.update(buffer, 4, 4);
	}

	private int readInt(byte[] buffer, int offset) {
		return (buffer[offset] << 24) | ((buffer[offset + 1] & 255) << 16) | ((buffer[offset + 2] & 255) << 8) | (buffer[offset + 3] & 255);
	}

	private void closeChunk() throws IOException {
		if (0 < chunkRemaining) {
			ship(chunkRemaining + 4);
		} else {
			readFully(buffer, 0, 4);
			int expectedCrc = readInt(buffer, 0);
			int computedCrc = (int) crc.getValue();
			if (computedCrc != expectedCrc) {
				throw new IOException("Invalid CRC.");
			}
		}
		chunkRemaining = 0;
		chunkLength = 0;
		chunkType = 0;
	}

	private void ship(int amount) throws IOException {
		while (0 < amount) {
			long skipped = inputStream.skip(amount);
			if (0 > skipped) {
				throw new EOFException();
			}
			amount -= skipped;
		}
	}
}
