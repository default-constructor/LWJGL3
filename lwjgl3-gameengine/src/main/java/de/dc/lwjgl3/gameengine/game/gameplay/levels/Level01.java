package de.dc.lwjgl3.gameengine.game.gameplay.levels;

import java.util.ArrayList;
import java.util.List;

import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.core.model.EntityData;
import de.dc.lwjgl3.gameengine.core.model.ModelTexture;
import de.dc.lwjgl3.gameengine.core.model.RawModel;
import de.dc.lwjgl3.gameengine.core.model.TerrainData;
import de.dc.lwjgl3.gameengine.core.model.TexturedModel;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Camera;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Entity;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Light;
import de.dc.lwjgl3.gameengine.game.gameplay.entities.Player;
import de.dc.lwjgl3.gameengine.game.gameplay.terrains.Terrain;
import de.dc.lwjgl3.gameengine.graphics.GraphicManager;
import de.dc.lwjgl3.gameengine.renderers.LevelRenderer;

public class Level01 extends Level {

	protected LevelRenderer levelRenderer;

	private Light light;
	private Terrain terrain;
	private List<Entity> entities = new ArrayList<>();
	private Camera camera;
	private Player player;

	private GraphicManager graphicManager;

	public Level01() {
		// System.out.println("Level01::constructor");
		levelRenderer = new LevelRenderer();
		graphicManager = new GraphicManager();
	}

	@Override
	public void destroy() {
		levelRenderer.destroy();

	}

	@Override
	public void init() {
		initLights();
		initTerrain();
		initEntities();
		initSky();

		EntityData personData = graphicManager.loadOBJ("person");
		RawModel personModel = graphicManager.loadRawModel(personData.getVertices(), personData.getTextureCoordinates(), personData.getNormals(),
				personData.getIndices());
		TexturedModel person = new TexturedModel(personModel, new ModelTexture(graphicManager.loadTexture("person")));

		player = new Player(person, new Vector3D(Terrain.SIZE / 2.0f, 0, -600f), 0.0f, 0.0f, 0.0f, 1.0f);

		camera = new Camera(player);
	}

	@Override
	public void render() {
		// System.out.println("Level01::render");
		levelRenderer.process(terrain);
		for (Entity entity : entities) {
			levelRenderer.process(entity);
		}
		levelRenderer.render(light, camera);
	}

	@Override
	public void update() {
		// System.out.println("Level01::update");
		camera.move();
	}

	private void initLights() {
		light = new Light(new Vector3D(20000, 20000, 20000), new Vector3D(1, 1, 1));
	}

	private void initEntities() {
		EntityData data = graphicManager.loadOBJ("haus");
		RawModel rawModel = graphicManager.loadRawModel(data.getVertices(), data.getTextureCoordinates(), data.getNormals(), data.getIndices());
		TexturedModel entityModel = new TexturedModel(rawModel, new ModelTexture(graphicManager.loadTexture("haus")));
		Entity entity = new Entity(entityModel, new Vector3D(384f, 3f, -384f), 10);
		entities.add(entity);
	}

	private void initSky() {
		//
	}

	private void initTerrain() {
		TerrainData data = graphicManager.loadTerrainData("gras", Terrain.SIZE);
		RawModel rawModel = graphicManager.loadRawModel(data.getVertices(), data.getTextureCoordinates(), data.getNormals(), data.getIndices());
		terrain = new Terrain(0, -1, rawModel, new ModelTexture(graphicManager.loadTexture("gras")), new ModelTexture(graphicManager.loadTexture("blendmap")));
	}
}
