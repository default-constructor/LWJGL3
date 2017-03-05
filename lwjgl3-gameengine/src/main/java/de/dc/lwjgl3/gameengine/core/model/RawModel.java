package de.dc.lwjgl3.gameengine.core.model;

public class RawModel {

	private int vaoId;
	private int vertexCount;

	public RawModel(int vaoId, int vertexCount) {
		this.vaoId = vaoId;
		this.vertexCount = vertexCount;
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
