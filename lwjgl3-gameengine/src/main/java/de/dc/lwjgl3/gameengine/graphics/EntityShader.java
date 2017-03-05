package de.dc.lwjgl3.gameengine.graphics;

import de.dc.lwjgl3.gameengine.core.State;

public class EntityShader extends Shader {

	public static EntityShader create(State state) {
		// System.out.println("EntityShader::create");
		return new EntityShader(VERTEXSHADER_PATH.replace("{state}", state.getName()).replace("{shadername}", "terrain"),
				FRAGMENTSHADER_PATH.replace("{state}", state.getName()).replace("{shadername}", "terrain"));
	}

	public EntityShader(String vertexPath, String fragmentPath) {
		super(vertexPath, fragmentPath);
		createProgram(new String[] { "position", "textureCoordinates", "normal" });

		locationCache.put("projectionMatrix", getUniformLocation("projectionMatrix"));
		locationCache.put("transformationMatrix", getUniformLocation("transformationMatrix"));
		locationCache.put("viewMatrix", getUniformLocation("viewMatrix"));
		locationCache.put("lightPosition", getUniformLocation("lightPosition"));
	}
}
