package de.dc.lwjgl3.gameengine.core;

import java.nio.FloatBuffer;

import de.dc.lwjgl3.gameengine.utils.BufferUtil;

public class Matrix4D {

	public static final int SIZE = 4 * 4;

	public static Matrix4D identity() {
		Matrix4D matrix = new Matrix4D();
		matrix.elements[0 + 0 * 4] = 1.0f;
		matrix.elements[1 + 1 * 4] = 1.0f;
		matrix.elements[2 + 2 * 4] = 1.0f;
		matrix.elements[3 + 3 * 4] = 1.0f;
		return matrix;
	}

	public static Matrix4D orthographic(float left, float right, float bottom, float top, float near, float far) {
		Matrix4D matrix = identity();
		matrix.elements[0 + 0 * 4] = 2.0f / (right - left);
		matrix.elements[1 + 1 * 4] = 2.0f / (top - bottom);
		matrix.elements[2 + 2 * 4] = 2.0f / (near - far);
		matrix.elements[0 + 3 * 4] = (left + right) / (left - right);
		matrix.elements[1 + 3 * 4] = (bottom + top) / (bottom - top);
		matrix.elements[2 + 3 * 4] = (far + near) / (far - near);
		return matrix;
	}

	public static Matrix4D rotate(float angle, Vector3D axis, Matrix4D src, Matrix4D dest) {
		if (null == dest) {
			dest = new Matrix4D();
		}
		float r = (float) Math.toRadians(angle);
		float cos = (float) Math.cos(r);
		float sin = (float) Math.sin(r);
		float oneMinusCos = 1.0f - cos;
		float x0 = axis.x * axis.x * oneMinusCos + cos;
		float x1 = axis.x * axis.y * oneMinusCos + axis.z * sin;
		float x2 = axis.x * axis.z * oneMinusCos - axis.y * sin;
		dest.elements[0] = src.elements[0] * x0 + src.elements[4] * x1 + src.elements[8] * x2;
		dest.elements[1] = src.elements[1] * x0 + src.elements[5] * x1 + src.elements[9] * x2;
		dest.elements[2] = src.elements[2] * x0 + src.elements[6] * x1 + src.elements[10] * x2;
		dest.elements[3] = src.elements[3] * x0 + src.elements[7] * x1 + src.elements[11] * x2;
		float y0 = axis.x * axis.y * oneMinusCos - axis.z * sin;
		float y1 = axis.y * axis.y * oneMinusCos + cos;
		float y2 = axis.y * axis.z * oneMinusCos - axis.x * sin;
		dest.elements[4] = src.elements[0] * y0 + src.elements[4] * y1 + src.elements[8] * y2;
		dest.elements[5] = src.elements[1] * y0 + src.elements[5] * y1 + src.elements[9] * y2;
		dest.elements[6] = src.elements[2] * y0 + src.elements[6] * y1 + src.elements[10] * y2;
		dest.elements[7] = src.elements[3] * y0 + src.elements[7] * y1 + src.elements[11] * y2;
		float z0 = axis.x * axis.z * oneMinusCos + axis.y * sin;
		float z1 = axis.y * axis.z * oneMinusCos - axis.x * sin;
		float z2 = axis.z * axis.z * oneMinusCos + cos;
		dest.elements[8] = src.elements[0] * z0 + src.elements[4] * z1 + src.elements[8] * z2;
		dest.elements[9] = src.elements[1] * z0 + src.elements[5] * z1 + src.elements[9] * z2;
		dest.elements[10] = src.elements[2] * z0 + src.elements[6] * z1 + src.elements[10] * z2;
		dest.elements[11] = src.elements[3] * z0 + src.elements[7] * z1 + src.elements[11] * z2;
		return dest;
	}

	public static Matrix4D scale(Vector3D vector, Matrix4D src, Matrix4D dest) {
		if (null == dest) {
			dest = new Matrix4D();
		}
		dest.elements[0] = src.elements[0] * vector.x;
		dest.elements[1] = src.elements[1] * vector.x;
		dest.elements[2] = src.elements[2] * vector.x;
		dest.elements[3] = src.elements[3] * vector.x;
		dest.elements[4] = src.elements[4] * vector.y;
		dest.elements[5] = src.elements[5] * vector.y;
		dest.elements[6] = src.elements[6] * vector.y;
		dest.elements[7] = src.elements[7] * vector.y;
		dest.elements[8] = src.elements[8] * vector.z;
		dest.elements[9] = src.elements[9] * vector.z;
		dest.elements[10] = src.elements[10] * vector.z;
		dest.elements[11] = src.elements[11] * vector.z;
		return dest;
	}

	public static Matrix4D translate(Vector3D vector, Matrix4D src, Matrix4D dest) {
		if (null == dest) {
			dest = new Matrix4D();
		}
		dest.elements[12] += src.elements[0] * vector.x + src.elements[4] * vector.y + src.elements[8] * vector.z;
		dest.elements[13] += src.elements[1] * vector.x + src.elements[5] * vector.y + src.elements[9] * vector.z;
		dest.elements[14] += src.elements[2] * vector.x + src.elements[6] * vector.y + src.elements[10] * vector.z;
		dest.elements[15] += src.elements[3] * vector.x + src.elements[7] * vector.y + src.elements[11] * vector.z;
		return dest;
	}

	public float[] elements = new float[SIZE];

	public Matrix4D() {
		//
	}

	public Matrix4D multiply(Matrix4D factor) {
		Matrix4D matrix = new Matrix4D(); // TODO: check if should be matrix = this
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				float sum = 0.0f;
				for (int e = 0; e < 4; e++) {
					sum += elements[x + e * 4] * factor.elements[e + y * 4];
				}
				matrix.elements[x + y * 4] = sum;
			}
		}
		return matrix;
	}

	public FloatBuffer toFloatBuffer() {
		return BufferUtil.createFloatBuffer(elements);
	}
}
