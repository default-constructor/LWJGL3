package de.dc.lwjgl3.gameengine.utils;

import de.dc.lwjgl3.gameengine.Window;
import de.dc.lwjgl3.gameengine.core.Matrix4D;
import de.dc.lwjgl3.gameengine.core.Vector2D;
import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Camera;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Entity;
import de.dc.lwjgl3.gameengine.game.gameplay.levels.Level;

public final class GraphicUtil {

	public static float barryCentric(Vector3D p1, Vector3D p2, Vector3D p3, Vector2D pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = (p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z) / det;
		float l2 = (p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4D getProjectionMatrix() {
		float aspectRatio = (float) Window.getDisplayWidth() / (float) Window.getDisplayHeight();
		float scaleY = (float) ((1f / Math.tan(Math.toRadians(Level.FIELD_OF_VIEW / 2f))) * aspectRatio);
		float scaleX = scaleY / aspectRatio;
		float frustumLength = Level.FAR_PLANE - Level.NEAR_PLANE;
		Matrix4D projectionMatrix = new Matrix4D();
		projectionMatrix.elements[0] = scaleX;
		projectionMatrix.elements[5] = scaleY;
		projectionMatrix.elements[10] = -((Level.FAR_PLANE + Level.NEAR_PLANE) / frustumLength);
		projectionMatrix.elements[11] = -1;
		projectionMatrix.elements[14] = -((2 * Level.NEAR_PLANE * Level.FAR_PLANE) / frustumLength);
		projectionMatrix.elements[15] = 0;
		return projectionMatrix;
	}

	public static Matrix4D getTransformationMatrix(Vector3D translation, float rx, float ry, float rz, float scale) {
		Matrix4D transformationMatrix = Matrix4D.identity();
		Matrix4D.translate(translation, transformationMatrix, transformationMatrix);
		Matrix4D.rotate(rx, new Vector3D(1, 0, 0), transformationMatrix, transformationMatrix);
		Matrix4D.rotate(ry, new Vector3D(0, 1, 0), transformationMatrix, transformationMatrix);
		Matrix4D.rotate(rz, new Vector3D(0, 0, 1), transformationMatrix, transformationMatrix);
		Matrix4D.scale(new Vector3D(scale, scale, scale), transformationMatrix, transformationMatrix);
		return transformationMatrix;
	}

	public static Matrix4D getViewMatrix(Camera camera) {
		Matrix4D viewMatrix = Matrix4D.identity();
		Matrix4D.rotate(camera.getPitch(), new Vector3D(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4D.rotate(camera.getYaw(), new Vector3D(0, 1, 0), viewMatrix, viewMatrix);
		Vector3D cameraPosition = camera.getPosition();
		Vector3D negativeCameraPosition = new Vector3D(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
		Matrix4D.translate(negativeCameraPosition, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	public static Entity getEntity(String name) {
		return null;
	}

	private GraphicUtil() {
		//
	}
}
