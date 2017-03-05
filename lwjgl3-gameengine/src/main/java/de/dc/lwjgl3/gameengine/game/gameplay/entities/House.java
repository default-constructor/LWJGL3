package de.dc.lwjgl3.gameengine.game.gameplay.entities;

import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.core.model.TexturedModel;

public class House extends Entity {

	private float rotationY;

	public House(TexturedModel texturedModel, Vector3D position, float scale, float rotationY) {
		super(texturedModel, position, scale);
		this.rotationY = rotationY;
	}

	public float getRotationY() {
		return rotationY;
	}

	public void setRotationY(float rotationY) {
		this.rotationY = rotationY;
	}
}
