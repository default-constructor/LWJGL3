package de.dc.lwjgl3.gameengine.game.gameplay.entities;

import org.lwjgl.input.Keyboard;

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

	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			currentRunSpeed = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			currentRunSpeed = -RUN_SPEED;
		} else {
			currentRunSpeed = 0;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentTurnSpeed = -TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			currentTurnSpeed = TURN_SPEED;
		} else {
			currentTurnSpeed = 0;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if (!inAir) {
				upwardsSpeed = JUMP_POWER;
				inAir = true;
			}
		}
	}
}
