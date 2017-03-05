package de.dc.lwjgl3.gameengine.core;

public abstract class Vector {

	public final float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	public abstract float lengthSquared();

	public final Vector normalise() {
		float length = length();
		if (0.0f != length) {
			float l = 1.0f / length;
			return scale(l);
		} else {
			throw new IllegalStateException("Zero length vector");
		}
	}

	public abstract Vector scale(float scale);
}
