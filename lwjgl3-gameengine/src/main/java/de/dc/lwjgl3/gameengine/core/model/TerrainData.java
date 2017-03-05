package de.dc.lwjgl3.gameengine.core.model;

public class TerrainData {

	private float[] vertices;
	private float[] textureCoordinates;
	private float[] normals;

	private int[] indices;

	public TerrainData(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices) {
		this.vertices = vertices;
		this.textureCoordinates = textureCoordinates;
		this.normals = normals;
		this.indices = indices;
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
}
