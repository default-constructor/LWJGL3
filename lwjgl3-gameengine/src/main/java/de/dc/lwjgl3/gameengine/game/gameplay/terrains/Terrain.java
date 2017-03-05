package de.dc.lwjgl3.gameengine.game.gameplay.terrains;

import java.util.ArrayList;
import java.util.List;

import de.dc.lwjgl3.gameengine.core.Vector2D;
import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.core.model.ModelTexture;
import de.dc.lwjgl3.gameengine.core.model.RawModel;
import de.dc.lwjgl3.gameengine.utils.GraphicUtil;

public class Terrain {

	public static final float MAX_HEIGHT = 40;
	public static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	public static final float SIZE = 768;

	private ModelTexture backgroundTexture;

	private ModelTexture blendMap;

	private List<ModelTexture> textureList = new ArrayList<>();

	private RawModel rawModel;

	private float x;
	private float z;

	private float[][] heights;

	public Terrain(int gridX, int gridZ, RawModel rawModel, ModelTexture backgroundTexture, ModelTexture blendMap, ModelTexture... textures) {
		this(gridX, gridZ, rawModel, backgroundTexture);
		this.blendMap = blendMap;
		for (int i = 0; i < (3 > textures.length ? textures.length : 3); i++) {
			textureList.add(textures[i]);
		}
	}

	public Terrain(int gridX, int gridZ, RawModel rawModel, ModelTexture backgroundTexture) {
		this.rawModel = rawModel;
		this.backgroundTexture = backgroundTexture;
		x = gridX * SIZE;
		z = gridZ * SIZE;
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX - x;
		float terrainZ = worldZ - z;
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float heightOfTerrain;
		if (xCoord <= (1 - zCoord)) {
			heightOfTerrain = GraphicUtil.barryCentric(new Vector3D(0, heights[gridX][gridZ], 0), new Vector3D(1, heights[gridX + 1][gridZ], 0),
					new Vector3D(0, heights[gridX][gridZ + 1], 1), new Vector2D(xCoord, zCoord));
		} else {
			heightOfTerrain = GraphicUtil.barryCentric(new Vector3D(1, heights[gridX + 1][gridZ], 0), new Vector3D(1, heights[gridX + 1][gridZ + 1], 1),
					new Vector3D(0, heights[gridX][gridZ + 1], 1), new Vector2D(xCoord, zCoord));
		}
		return heightOfTerrain;
	}

	public ModelTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public ModelTexture getBlendMap() {
		return blendMap;
	}

	public List<ModelTexture> getTextureList() {
		return textureList;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
}
