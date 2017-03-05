package de.dc.lwjgl3.gameengine.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dc.lwjgl3.gameengine.core.Matrix4D;
import de.dc.lwjgl3.gameengine.core.State;
import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.core.model.TexturedModel;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Camera;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Entity;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Light;
import de.dc.lwjgl3.gameengine.game.gameplay.terrains.Terrain;
import de.dc.lwjgl3.gameengine.graphics.EntityShader;
import de.dc.lwjgl3.gameengine.graphics.Shader;
import de.dc.lwjgl3.gameengine.graphics.TerrainShader;
import de.dc.lwjgl3.gameengine.utils.GraphicUtil;

public class LevelRenderer extends Renderer {

	private static final float RED = 0.49f;
	private static final float GREEN = 0.89f;
	private static final float BLUE = 0.98f;

	private static Matrix4D projectionMatrix = GraphicUtil.getProjectionMatrix();

	private Shader entityShader = EntityShader.create(State.GAME_PLAY);
	private Shader terrainShader = TerrainShader.create(State.GAME_PLAY);
	// private Shader skyboxShader = Shader.create(State.GAME_PLAY, "skybox");

	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	private SkyboxRenderer skyboxRenderer;

	private Map<TexturedModel, List<Entity>> entitiesMap = new HashMap<>();
	private List<Terrain> terrainList = new ArrayList<>();

	public LevelRenderer() {
		// System.out.println("EntityRenderer::constructor");
		enableCulling();
		entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		// skyboxRenderer = new SkyboxRenderer(skyboxShader, projectionMatrix);
	}

	public void destroy() {
		terrainShader.destroy();
	}

	public void process(Entity entity) {
		// System.out.println("EntityRenderer::process");
		TexturedModel texturedModel = entity.getTexturedModel();
		List<Entity> batch = entitiesMap.get(texturedModel);
		if (null == batch) {
			batch = new ArrayList<>();
			entitiesMap.put(texturedModel, batch);
		}
		batch.add(entity);
	}

	public void process(Terrain terrain) {
		terrainList.add(terrain);
	}

	public void render(Light light, Camera camera) {
		// System.out.println("EntityRenderer::render");
		prepare(RED, GREEN, BLUE);

		terrainShader.enable();
		loadProperties(terrainShader, light, camera);
		terrainRenderer.render(terrainList);
		terrainShader.disable();
		terrainList.clear();

		entityShader.enable();
		loadProperties(terrainShader, light, camera);
		entityRenderer.render(entitiesMap);
		entityShader.disable();
		entitiesMap.clear();
	}

	private void loadProperties(Shader shader, Light light, Camera camera) {
		shader.setUniform("skyColor", new Vector3D(RED, GREEN, BLUE));
		shader.setUniform("lightPosition", light.getPosition());
		shader.setUniform("lightColor", light.getColor());
		shader.setUniform("lightAttenuation", light.getAttenuation());
		shader.setUniform("viewMatrix", GraphicUtil.getViewMatrix(camera));
	}
}
