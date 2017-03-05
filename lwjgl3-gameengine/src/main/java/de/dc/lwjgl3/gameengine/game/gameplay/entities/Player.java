package de.dc.lwjgl3.gameengine.game.gameplay.entities;

import de.dc.lwjgl3.gameengine.Window;
import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.core.model.TexturedModel;
import de.dc.lwjgl3.gameengine.game.gameplay.terrains.Terrain;

public class Player extends Entity {

	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;

	private float currentRunSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	private boolean inAir = false;

	public Player(TexturedModel person, Vector3D position, float rx, float ry, float rz, float scale) {
		super(person, position, rx, ry, rz, scale);
	}

	public void move(Terrain terrain) {
		checkInputs();
		log();
		super.increaseRotation(0, currentTurnSpeed * Window.getFrameTimeSeconds(), 0);
		float distance = currentRunSpeed * Window.getFrameTimeSeconds();
		float deltaX = (float) (distance * Math.sin(Math.toRadians(super.getRotationY())));
		float deltaZ = (float) (distance * Math.cos(Math.toRadians(super.getRotationY())));
		super.increasePosition(deltaX, 0, deltaZ);
		upwardsSpeed += GRAVITY * Window.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * Window.getFrameTimeSeconds(), 0);
		float heightOfTerrain = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (heightOfTerrain > super.getPosition().y) {
			upwardsSpeed = 0;
			inAir = false;
			super.getPosition().y = heightOfTerrain;
		}
	}

	private void log() {
		System.out.println("Current run speed: " + currentRunSpeed);
	}

	private void checkInputs() {
		// System.out.println("Is key space pressed? " + Window.input.isKeySpacePressed());
		if (Window.input.isKeyForwardPressed()) {
			currentRunSpeed = RUN_SPEED;
		} else if (Window.input.isKeyBackwardPressed()) {
			currentRunSpeed = -RUN_SPEED;
		} else {
			currentRunSpeed = 0;
		}
		if (Window.input.isKeyRightPressed()) {
			currentTurnSpeed = -TURN_SPEED;
		} else if (Window.input.isKeyLeftPressed()) {
			currentTurnSpeed = TURN_SPEED;
		} else {
			currentTurnSpeed = 0;
		}
		if (Window.input.isKeySpacePressed()) {
			if (!inAir) {
				upwardsSpeed = JUMP_POWER;
				inAir = true;
			}
		}
	}
}
