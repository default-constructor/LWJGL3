package de.dc.lwjgl3.gameengine.game.gameplay.entities;

import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.core.model.TexturedModel;

public class Entity {

	private TexturedModel texturedModel;

	private Vector3D position;

	private float rotationX;
	private float rotationY;
	private float rotationZ;

	private float scale;

	public Entity(TexturedModel texturedModel, Vector3D position, float rotationX, float rotationY, float rotationZ, float scale) {
		this.texturedModel = texturedModel;
		this.position = position;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
		this.scale = scale;
	}

	public Entity(TexturedModel texturedModel, Vector3D position, float scale) {
		this.texturedModel = texturedModel;
		this.position = position;
		this.scale = scale;
	}

	public void increasePosition(float deltaX, float deltaY, float deltaZ) {
		this.position.x += deltaX;
		this.position.y += deltaY;
		this.position.z += deltaZ;
	}

	public void increaseRotation(float deltaX, float deltaY, float deltaZ) {
		this.rotationX += deltaX;
		this.rotationY += deltaY;
		this.rotationZ += deltaZ;
	}

	public void render() {

	}

	public TexturedModel getTexturedModel() {
		return texturedModel;
	}

	public Vector3D getPosition() {
		return position;
	}

	public float getRotationX() {
		return rotationX;
	}

	public float getRotationY() {
		return rotationY;
	}

	public float getRotationZ() {
		return rotationZ;
	}

	public float getScale() {
		return scale;
	}
}
