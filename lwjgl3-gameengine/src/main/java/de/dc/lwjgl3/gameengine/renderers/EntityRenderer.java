package de.dc.lwjgl3.gameengine.renderers;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dc.lwjgl3.gameengine.core.Matrix4D;
import de.dc.lwjgl3.gameengine.core.model.ModelTexture;
import de.dc.lwjgl3.gameengine.core.model.RawModel;
import de.dc.lwjgl3.gameengine.core.model.TexturedModel;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Entity;
import de.dc.lwjgl3.gameengine.graphics.Shader;
import de.dc.lwjgl3.gameengine.utils.GraphicUtil;

public class EntityRenderer extends Renderer {

	private Shader shader;

	private Map<TexturedModel, List<Entity>> entitiesMap = new HashMap<>();

	public EntityRenderer(Shader shader, Matrix4D projectionMatrix) {
		this.shader = shader;
		shader.enable();
		shader.setUniform("projectionMatrix", projectionMatrix);
		shader.disable();
	}

	public void render(Map<TexturedModel, List<Entity>> entitiesMap) {
		for (TexturedModel texturedModel : entitiesMap.keySet()) {
			bindTexturedModel(texturedModel);
			List<Entity> batch = entitiesMap.get(texturedModel);
			for (Entity entity : batch) {
				prepareInstance(entity);
				glDrawElements(GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	private void bindTexturedModel(TexturedModel texturedModel) {
//		System.out.println("EntityRenderer::bindTexturedModel");
		RawModel rawModel = texturedModel.getRawModel();
		glBindVertexArray(rawModel.getVaoId());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		ModelTexture textureModel = texturedModel.getTextureModel();
		if (textureModel.hasTransparency()) {
			disableCulling();
		}
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureModel.getTextureId());
	}

	private void prepareInstance(Entity entity) {
//		System.out.println("EntityRenderer::prepareInstance");
		Matrix4D transformationMatrix = GraphicUtil.getTransformationMatrix(entity.getPosition(), entity.getRotationX(), entity.getRotationY(),
				entity.getRotationZ(), entity.getScale());
		this.shader.setUniform("transformationMatrix", transformationMatrix);
	}

	private void unbindTexturedModel() {
//		System.out.println("EntityRenderer::unbindTexturedModel");
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
	}
}
