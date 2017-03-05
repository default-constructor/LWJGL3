package de.dc.lwjgl3.gameengine.graphics;

import static de.dc.lwjgl3.gameengine.utils.BufferUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import de.dc.lwjgl3.gameengine.core.Vector2D;
import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.core.Vertex;
import de.dc.lwjgl3.gameengine.core.model.EntityData;
import de.dc.lwjgl3.gameengine.core.model.RawModel;
import de.dc.lwjgl3.gameengine.core.model.TerrainData;
import de.dc.lwjgl3.gameengine.game.gameplay.terrains.Terrain;
import de.dc.lwjgl3.gameengine.utils.FileUtil;
import de.dc.lwjgl3.utils.opengl.Texture;
import de.dc.lwjgl3.utils.opengl.TextureLoader;

/**
 * 
 * @author Thomas Reno
 *
 */
public class GraphicManager {

	private List<Integer> vaoIdList = new ArrayList<>();
	private List<Integer> vboIdList = new ArrayList<>();

	private List<Integer> textureIdList = new ArrayList<>();

	public void cleanUp() {
//		System.out.println("GraphicManager::cleanUp");
		for (int vao : vaoIdList) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vboIdList) {
			GL15.glDeleteBuffers(vbo);
		}
	}

	/**
	 * OBJ-File loading
	 */

	public EntityData loadOBJ(String name) {
//		System.out.println("GraphicManager::loadOBJ");
		List<Vertex> vertices = new ArrayList<>();
		List<Vector2D> textures = new ArrayList<>();
		List<Vector3D> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		BufferedReader bufferedReader = FileUtil.loadFile("res/entities/" + name + ".obj");
		String line;
		try {
			while (true) {
				line = bufferedReader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3D vector = new Vector3D(Float.valueOf(currentLine[1]), Float.valueOf(currentLine[2]), Float.valueOf(currentLine[3]));
					Vertex vertex = new Vertex(vertices.size(), vector);
					vertices.add(vertex);
				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2D vector = new Vector2D(Float.valueOf(currentLine[1]), Float.valueOf(currentLine[2]));
					textures.add(vector);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3D vector = new Vector3D(Float.valueOf(currentLine[1]), Float.valueOf(currentLine[2]), Float.valueOf(currentLine[3]));
					normals.add(vector);
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (null != line && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				processVertex(currentLine[1].split("/"), vertices, indices);
				processVertex(currentLine[2].split("/"), vertices, indices);
				processVertex(currentLine[3].split("/"), vertices, indices);
				line = bufferedReader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Error reading the file.");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthestPoint = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray);
		int[] indicesArray = convertIndicesListToArray(indices);

		return new EntityData(verticesArray, texturesArray, normalsArray, indicesArray, furthestPoint);
	}

	private float convertDataToArrays(List<Vertex> vertices, List<Vector2D> textures, List<Vector3D> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray) {
//		System.out.println("GraphicManager::convertDataToArrays");
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex vertex = vertices.get(i);
			if (furthestPoint < vertex.getLength()) {
				furthestPoint = vertex.getLength();
			}
			Vector3D position = vertex.getPosition();
			Vector2D textureCoordinates = textures.get(vertex.getTextureIndex());
			Vector3D normal = normals.get(vertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoordinates.x;
			texturesArray[i * 2 + 1] = 1 - textureCoordinates.y; // Why 1 - y?
			normalsArray[i * 3] = normal.x;
			normalsArray[i * 3 + 1] = normal.y;
			normalsArray[i * 3 + 2] = normal.z;
		}
		return furthestPoint;
	}

	private int[] convertIndicesListToArray(List<Integer> indices) {
//		System.out.println("GraphicManager::convertIndicesListToArray");
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private void handleAlreadyProcessedVertex(Vertex previousVertex, int textureIndex, int normalIndex, List<Integer> indices, List<Vertex> vertices) {
//		System.out.println("GraphicManager::handleAlreadyProcessedVertex");
		if (previousVertex.hasSameTextureAndNormal(textureIndex, normalIndex)) {
			indices.add(previousVertex.getIndex());
			return;
		}
		if (null != previousVertex.getDuplicate()) {
			handleAlreadyProcessedVertex(previousVertex, textureIndex, normalIndex, indices, vertices);
			return;
		}
		Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
		duplicateVertex.setTextureIndex(textureIndex);
		duplicateVertex.setNormalIndex(normalIndex);
		vertices.add(duplicateVertex);
		indices.add(duplicateVertex.getIndex());
	}

	private void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
//		System.out.println("GraphicManager::processVertex");
		int vertexIndex = Integer.valueOf(vertex[0]) - 1;
		int textureIndex = Integer.valueOf(vertex[1]) - 1;
		int normalIndex = Integer.valueOf(vertex[2]) - 1;
		Vertex currentVertex = vertices.get(vertexIndex);
		if (currentVertex.isVertexSet()) {
			handleAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
			return;
		}
		currentVertex.setTextureIndex(textureIndex);
		currentVertex.setNormalIndex(normalIndex);
		indices.add(vertexIndex);
	}

	private void removeUnusedVertices(List<Vertex> vertices) {
//		System.out.println("GraphicManager::removeUnusedVertices");
		for (Vertex vertex : vertices) {
			if (!vertex.isVertexSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

	/**
	 * Raw model loading
	 */

	public RawModel loadRawModel(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
//		System.out.println("GraphicManager::loadRawModel");
		int vaoId = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoordinates);
		storeDataInAttributeList(2, 3, normals);
		glBindVertexArray(0);
		return new RawModel(vaoId, indices.length);
	}

	private void bindIndicesBuffer(int[] indices) {
//		System.out.println("GraphicManager::bindIndicesBuffer");
		int vboId = glGenBuffers();
		vboIdList.add(vboId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, createIntBuffer(indices), GL_STATIC_DRAW);
	}

	private int createVAO() {
		int vaoId = glGenVertexArrays();
		vaoIdList.add(vaoId);
		glBindVertexArray(vaoId);
		return vaoId;
	}

	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
//		System.out.println("GraphicManager::storeDataInAttributeList");
		int vboId = glGenBuffers();
		vboIdList.add(vboId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, createFloatBuffer(data), GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Terrain data loading
	 */

	public TerrainData loadTerrainData(String heightMapName, float size) {
		BufferedImage image = FileUtil.loadImage("res/textures/" + heightMapName + ".png");

		int vertexCount = image.getHeight();
		float[][] heights = new float[vertexCount][vertexCount];
		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoordinates = new float[count * 2];
		int[] indices = new int[6 * (int) (Math.pow(vertexCount - 1, 2))];
		int pointer = 0;
		for (int i = 0; i < vertexCount; i++) {
			for (int j = 0; j < vertexCount; j++) {
				vertices[pointer * 3] = (float) j / ((float) (vertexCount - 1)) * size;
				float height = getHeight(j, i, image);
				heights[j][i] = height;
				vertices[pointer * 3 + 1] = 0; // height;
				vertices[pointer * 3 + 2] = (float) i / ((float) (vertexCount - 1)) * size;
				Vector3D normal = getNormal(j, i, image);
				normals[pointer * 3] = 0; // normal.x;
				normals[pointer * 3 + 1] = 1; // normal.y;
				normals[pointer * 3 + 2] = 0; // normal.z;
				textureCoordinates[pointer * 2] = (float) j / ((float) vertexCount - 1);
				textureCoordinates[pointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
				pointer++;
			}
		}

		pointer = 0;
		for (int gz = 0; gz < vertexCount - 1; gz++) {
			for (int gx = 0; gx < vertexCount - 1; gx++) {
				int topLeft = gz * vertexCount + gx;
				int topRight = topLeft + 1;
				int bottomLeft = (gz + 1) * vertexCount + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		return new TerrainData(vertices, textureCoordinates, normals, indices);
	}

	private float getHeight(int x, int z, BufferedImage image) {
		if (0 > x || image.getHeight() <= x || 0 > z || image.getHeight() <= z) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height += Terrain.MAX_PIXEL_COLOR / 2f;
		height /= Terrain.MAX_PIXEL_COLOR / 2f;
		height *= Terrain.MAX_HEIGHT;
		return 0; // height;
	}

	private Vector3D getNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x - 1, z, image);
		float heightR = getHeight(x + 1, z, image);
		float heightD = getHeight(x, z + 1, image);
		float heightU = getHeight(x, z + 1, image);
		Vector3D normal = new Vector3D(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}

	/**
	 * Texture loading
	 */

	public int loadTexture(String name) {
//		System.out.println("GraphicManager::loadTexture");

		Texture texture = null;
		try (InputStream stream = new FileInputStream("res/textures/" + name + ".png")) {
			texture = TextureLoader.getTexture("PNG", stream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		glGenerateMipmap(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.4f);

		int textureId = texture.getTextureId();
		textureIdList.add(textureId);

		return textureId;
	}

	// /**
	// *
	// * @param fileName
	// * @return
	// */
	// public TextureModel loadTextureModel(String fileName) {
	// System.out.println("GraphicManager::loadTextureModel");
	// int width = 0, height = 0;
	// int[] pixels = null;
	// String path = "res/" + fileName + ".png";
	// try {
	// BufferedImage image = ImageIO.read(new FileInputStream(path));
	// width = image.getWidth();
	// height = image.getHeight();
	// pixels = new int[width * height];
	// image.getRGB(0, 0, width, height, pixels, 0, width);
	// } catch (IOException e) {
	// System.err.println("Could not read image '" + path + "'.");
	// e.printStackTrace();
	// return null; // TODO: check if its sinnvoll
	// }
	//
	// int[] data = new int[width * height];
	// for (int i = 0; i < data.length; i++) {
	// int a = (pixels[i] & 0xff000000) >> 24;
	// int r = (pixels[i] & 0xff0000) >> 16;
	// int g = (pixels[i] & 0xff00) >> 8;
	// int b = (pixels[i] & 0xff);
	//
	// data[i] = a << 24 | b << 16 | g << 8 | r;
	// }
	//
	// int textureId = glGenTextures();
	// glBindTexture(GL_TEXTURE_2D, textureId);
	// glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	// glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	// glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, createIntBuffer(data));
	// glBindTexture(GL_TEXTURE_2D, 0);
	//
	// return new TextureModel(textureId);
	// }
}
