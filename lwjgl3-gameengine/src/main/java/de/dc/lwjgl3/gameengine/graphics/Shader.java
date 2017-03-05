package de.dc.lwjgl3.gameengine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import de.dc.lwjgl3.gameengine.core.Matrix4D;
import de.dc.lwjgl3.gameengine.core.Vector3D;
import de.dc.lwjgl3.gameengine.utils.FileUtil;

public abstract class Shader {

	public static final int VERTEX_INDEX = 0;
	public static final int TEXTURE_COORDINATES_INDEX = 1;

	protected static final String VERTEXSHADER_PATH = "src/main/resources/shaders/{state}/{shadername}.vert";
	protected static final String FRAGMENTSHADER_PATH = "src/main/resources/shaders/{state}/{shadername}.frag";

	protected Map<String, Integer> locationCache = new HashMap<>();

	private int programId;
	private int vertexShaderId;
	private int fragmentShaderId;

	private boolean enabled = false;

	public Shader(String vertexPath, String fragmentPath) {
//		System.out.println("Shader::constructor");
		vertexShaderId = createShader(GL_VERTEX_SHADER, vertexPath);
		fragmentShaderId = createShader(GL_FRAGMENT_SHADER, fragmentPath);
	}

	public void destroy() {
//		System.out.println("Shader::destroy");
		disable();
		glDetachShader(programId, vertexShaderId);
		glDetachShader(programId, fragmentShaderId);
		glDeleteShader(vertexShaderId);
		glDeleteShader(fragmentShaderId);
		glDeleteProgram(programId);
	}

	public void disable() {
//		System.out.println("Shader::disable");
		if (!enabled) {
			return;
		}
		glUseProgram(0);
		enabled = false;
	}

	public void enable() {
//		System.out.println("Shader::enable");
		if (enabled) {
			return;
		}
		glUseProgram(programId);
		enabled = true;
	}

	protected void createProgram(String[] attributes) {
//		System.out.println("Shader::createProgram");
		programId = glCreateProgram();
		glAttachShader(programId, vertexShaderId);
		glAttachShader(programId, fragmentShaderId);
		for (int i = 0; i < attributes.length; i++) {
			glBindAttribLocation(programId, i, attributes[i]);
		}
		glLinkProgram(programId);
		glValidateProgram(programId);
	}

	protected int getUniformLocation(String variable) {
//		System.out.println("Shader::getUniformLocation -> " + variable);
		if (locationCache.containsKey(variable)) {
			return locationCache.get(variable);
		}
		int uniformId = glGetUniformLocation(programId, variable);
		if (-1 != uniformId) {
			locationCache.put(variable, uniformId);
		} else {
			throw new IllegalStateException("Could not find uniform variable '" + variable + "'.");
		}
		return uniformId;
	}

	public void setUniform(String variable, float value) {
//		System.out.println("Shader::setUniform");
		// enable();
		glUniform1f(getUniformLocation(variable), value);
		// disable();
	}

	public void setUniform(String variable, float x, float y) {
//		System.out.println("Shader::setUniform");
		// enable();
		glUniform2f(getUniformLocation(variable), x, y);
		// disable();
	}

	public void setUniform(String variable, float x, float y, float z) {
//		System.out.println("Shader::setUniform");
		// enable();
		glUniform3f(getUniformLocation(variable), x, y, z);
		// disable();
	}

	public void setUniform(String variable, int value) {
//		System.out.println("Shader::setUniform");
		// enable();
		glUniform1i(getUniformLocation(variable), value);
		// disable();
	}

	public void setUniform(String variable, Matrix4D matrix) {
//		System.out.println("Shader::setUniform");
		// enable();
		glUniformMatrix4fv(getUniformLocation(variable), false, matrix.toFloatBuffer());
		// disable();
	}

	public void setUniform(String variable, Vector3D vector) {
//		System.out.println("Shader::setUniform");
		// enable();
		glUniform3f(getUniformLocation(variable), vector.x, vector.y, vector.z);
		// disable();
	}

	private int createShader(int type, String path) {
		int shaderId = glCreateShader(type);
//		System.out.println("Shader::createShader -> " + path);
		String source = FileUtil.loadAsString(path);
//		System.out.println("Shader::createShader -> " + source);
		glShaderSource(shaderId, source);
		glCompileShader(shaderId);
		if (GL_TRUE != glGetShaderi(shaderId, GL_COMPILE_STATUS)) {
			glDeleteShader(shaderId);
			throw new RuntimeException("Could not compile shader.\n" + glGetShaderInfoLog(shaderId, 500));
		}
		return shaderId;
	}
}
