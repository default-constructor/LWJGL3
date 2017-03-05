package de.dc.lwjgl3.gameengine.core;

public class Vertex {

	private static final int NO_INDEX = -1;

	private int index;

	private Vector3D position;

	private float length;

	private Vertex duplicate = null;

	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;

	public Vertex(int index, Vector3D position) {
		this.index = index;
		this.position = position;
	}

	public boolean hasSameTextureAndNormal(int otherTextureIndex, int otherNormalIndex) {
		return this.textureIndex == otherTextureIndex && this.normalIndex == otherNormalIndex;
	}

	public boolean isVertexSet() {
		return NO_INDEX < textureIndex && NO_INDEX < normalIndex;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Vector3D getPosition() {
		return position;
	}

	public void setPosition(Vector3D position) {
		this.position = position;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public Vertex getDuplicate() {
		return duplicate;
	}

	public void setDuplicate(Vertex duplicate) {
		this.duplicate = duplicate;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}
}
