package de.dc.lwjgl3.gameengine.core.model;

public class EntityData {

	private float[] vertices;
	private float[] textureCoordinates;
	private float[] normals;

	private int[] indices;

	private float furthestPoint;

	public EntityData(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices, float furthestPoint) {
		this.vertices = vertices;
		this.textureCoordinates = textureCoordinates;
		this.normals = normals;
		this.indices = indices;
		this.furthestPoint = furthestPoint;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoordinates() {
		return textureCoordinates;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public float getFurthestPoint() {
		return furthestPoint;
	}
}
