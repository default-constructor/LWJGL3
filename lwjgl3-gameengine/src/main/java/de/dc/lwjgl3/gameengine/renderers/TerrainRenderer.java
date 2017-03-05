package de.dc.lwjgl3.gameengine.renderers;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.List;

import de.dc.lwjgl3.gameengine.core.Matrix4D;
import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.core.model.ModelTexture;
import de.dc.lwjgl3.gameengine.core.model.RawModel;
import de.dc.lwjgl3.gameengine.game.gameplay.terrains.Terrain;
import de.dc.lwjgl3.gameengine.graphics.Shader;
import de.dc.lwjgl3.gameengine.utils.GraphicUtil;

public class TerrainRenderer {

	private Shader shader;

	public TerrainRenderer(Shader shader, Matrix4D projectionMatrix) {
		this.shader = shader;
		shader.enable();
		shader.setUniform("projectionMatrix", projectionMatrix);
		shader.disable();
	}

	public void render(List<Terrain> terrainList) {
		for (Terrain terrain : terrainList) {
			prepareTerrain(terrain);
			prepareInstance(terrain);
			glDrawElements(GL_TRIANGLES, terrain.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}

	private void bindTextures(Terrain terrain) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, terrain.getBackgroundTexture().getTextureId());
		int i = GL_TEXTURE1;
		for (; i <= GL_TEXTURE1 + terrain.getTextureList().size() - 1; i++) {
			glActiveTexture(i);
			glBindTexture(GL_TEXTURE_2D, terrain.getTextureList().get(i).getTextureId());
		}
		ModelTexture blendMap;
		if (null != (blendMap = terrain.getBlendMap())) {
			glActiveTexture(i++);
			glBindTexture(GL_TEXTURE_2D, blendMap.getTextureId());
		}
	}

	private void prepareInstance(Terrain terrain) {
//		System.out.println("EntityRenderer::prepareInstance");
		Matrix4D transformationMatrix = GraphicUtil.getTransformationMatrix(new Vector3D(terrain.getX(), 0, terrain.getZ()), 0f, 0f, 0f, 1f);
		this.shader.setUniform("transformationMatrix", transformationMatrix);
	}

	private void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getRawModel();
		glBindVertexArray(rawModel.getVaoId());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		bindTextures(terrain);
	}

	private void unbindTexturedModel() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
	}
}
