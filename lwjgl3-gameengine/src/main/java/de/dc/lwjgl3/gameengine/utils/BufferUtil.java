package de.dc.lwjgl3.gameengine.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public final class BufferUtil {

	public static ByteBuffer createByteBuffer(byte[] array) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
		buffer.put(array).flip();
		return buffer;
	}

	public static FloatBuffer createFloatBuffer(float[] array) {
		FloatBuffer buffer = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
		buffer.put(array).flip();
		return buffer;
	}

	public static IntBuffer createIntBuffer(int[] array) {
		IntBuffer buffer = BufferUtils.createIntBuffer(array.length); // ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
		buffer.put(array).flip();
		return buffer;
	}

	private BufferUtil() {
		//
	}
}
