package de.dc.lwjgl3.gameengine.core.model;

public class ModelTexture {

	private int textureId;

	private boolean transparency;

	public ModelTexture(int textureId, boolean transparency) {
		this.textureId = textureId;
		this.transparency = transparency;
	}

	public ModelTexture(int textureId) {
		this(textureId, false);
	}

	public int getTextureId() {
		return textureId;
	}

	public boolean hasTransparency() {
		return transparency;
	}

	public void setTransparency(boolean transparency) {
		this.transparency = transparency;
	}
}
