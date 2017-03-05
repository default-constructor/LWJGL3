package de.dc.lwjgl3.gameengine.game.gameplay.entities;

import de.dc.lwjgl3.gameengine.Window;
import de.dc.lwjgl3.gameengine.core.Vector3D;

public class Camera {

	private Vector3D position = new Vector3D();

	private float pitch = 20;
	private float yaw;
	private float roll;

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;

	private Player player;

	public Camera(Player player) {
		this.player = player;
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = getHorizontalDistance();
		float verticalDistance = getVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		yaw = 180 - (player.getRotationY() + angleAroundPlayer);
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = player.getRotationY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance + 4;
	}

	private float getVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private float getHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private void calculateAngleAroundPlayer() {
		if (Window.input.isMouseButtonRightPressed()) {
			float angleChange = Window.input.getMousePositionDeltaX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}

	private void calculatePitch() {
//		if (Window.input.isMouseButtonRightPressed()) {
//			float pitchChange = Window.input.getMousePositionDeltaY() * 0.1f;
//			pitch -= pitchChange;
//		}
	}

	private void calculateZoom() {
		if (Window.input.isMouseScrollsDown() || Window.input.isMouseScrollsUp()) {
			distanceFromPlayer -= Window.input.getMouseWheelY() * 10;
		}
	}

	public Vector3D getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
}
