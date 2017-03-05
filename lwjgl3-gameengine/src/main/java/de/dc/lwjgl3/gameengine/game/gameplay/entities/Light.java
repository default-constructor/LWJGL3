package de.dc.lwjgl3.gameengine.game.gameplay.entities;

import de.dc.lwjgl3.gameengine.core.Vector3D;

public class Light {

	private Vector3D position;
	private Vector3D color;
	private Vector3D attenuation = new Vector3D(1, 0, 0);

	public Light(Vector3D position, Vector3D color, Vector3D attenuation) {
		this(position, color);
		this.attenuation = attenuation;
	}

	public Light(Vector3D position, Vector3D color) {
		this.position = position;
		this.color = color;
	}

	public Vector3D getPosition() {
		return position;
	}

	public Vector3D getColor() {
		return color;
	}

	public Vector3D getAttenuation() {
		return attenuation;
	}
}
