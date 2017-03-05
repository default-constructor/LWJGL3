package de.dc.lwjgl3.gameengine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import de.dc.lwjgl3.gameengine.utils.BufferUtil;

public class VertexArray {

	private int vaoId, vboId, iboId, tboId;

	private int amount;

	public VertexArray(float[] vertices, int[] indices, float[] textureCoordinates) {
//		System.out.println("VertexArray::constructor");
		amount = indices.length;

		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		vboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtil.createFloatBuffer(vertices), GL_STATIC_DRAW);
		
		glVertexAttribPointer(Shader.VERTEX_INDEX, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(Shader.VERTEX_INDEX);

		tboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, tboId);
		glBufferData(GL_ARRAY_BUFFER, BufferUtil.createFloatBuffer(textureCoordinates), GL_STATIC_DRAW);
		glVertexAttribPointer(Shader.TEXTURE_COORDINATES_INDEX, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(Shader.TEXTURE_COORDINATES_INDEX);

		iboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtil.createIntBuffer(indices), GL_STATIC_DRAW);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	public void bind() {
//		System.out.println("VertexArray::bind");
		glBindVertexArray(vaoId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
	}

	public void draw() {
//		System.out.println("VertexArray::draw");
		glDrawElements(GL_TRIANGLES, amount, GL_UNSIGNED_BYTE, 0);
	}

	public void render() {
//		System.out.println("VertexArray::render");
		bind();
		draw();
	}

	public void unbind() {
//		System.out.println("VertexArray::unbind");
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
}
