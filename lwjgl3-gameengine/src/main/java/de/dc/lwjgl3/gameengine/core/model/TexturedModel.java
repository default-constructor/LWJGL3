package de.dc.lwjgl3.gameengine.core.model;

public class TexturedModel {

	private RawModel rawModel;
	private ModelTexture textureModel;

	public TexturedModel(RawModel rawModel, ModelTexture textureModel) {
		this.rawModel = rawModel;
		this.textureModel = textureModel;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTextureModel() {
		return textureModel;
	}
}
