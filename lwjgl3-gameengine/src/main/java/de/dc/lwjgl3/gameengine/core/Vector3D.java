package de.dc.lwjgl3.gameengine.core;

public class Vector3D extends Vector {

	@Override
	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	@Override
	public Vector scale(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}

	public float x, y, z;

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}

//	public Vector3D normalise(Vector3D dest) {
//		float l = length();
//		if (null == dest) {
//			dest = new Vector3D(x / l, y / l, z / l);
//		} else {
//			dest.set(x / l, y / l, z / l);
//		}
//		return dest;
//	}
//
//	private void set(float x, float y, float z) {
//		this.x = x;
//		this.y = y;
//		this.z = z;
//	}
}
