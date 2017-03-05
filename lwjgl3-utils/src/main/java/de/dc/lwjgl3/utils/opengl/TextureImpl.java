package de.dc.lwjgl3.utils.opengl;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class TextureImpl implements Texture {

	private static Texture lastBind;

	@Override
	public boolean hasAlpha() {
		return alpha;
	}

	@Override
	public String getTextureReference() {
		return reference;
	}

	@Override
	public void bind() {
		if (this != lastBind) {
			lastBind = this;
			glEnable(GL_TEXTURE_2D);
			glBindTexture(target, textureId);
		}
	}

	@Override
	public int getImageHeight() {
		return height;
	}

	@Override
	public int getImageWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return heightRatio;
	}

	@Override
	public float getWidth() {
		return widthRatio;
	}

	@Override
	public int getTextureHeight() {
		return textureHeight;
	}

	@Override
	public int getTextureWidth() {
		return textureWidth;
	}

	@Override
	public void release() {
		// TODO: release
	}

	@Override
	public int getTextureId() {
		return textureId;
	}

	@Override
	public byte[] getTextureData() {
		ByteBuffer buffer = BufferUtils.createByteBuffer((alpha ? 4 : 3) * textureWidth * textureHeight);
		bind();
		glGetTexImage(GL_TEXTURE_2D, 0, alpha ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, buffer);
		byte[] textureData = new byte[buffer.limit()];
		buffer.get(textureData);
		buffer.clear();
		return textureData;
	}

	@Override
	public void setTextureFilter(int textureFilter) {
		// TODO set texture filter

	}

	private String reference;
	private int target;
	private int textureId;

	private int textureWidth;
	private int textureHeight;
	private int width;
	private int height;
	private float widthRatio;
	private float heightRatio;
	private boolean alpha;

	private String cacheName;

	private ReloadData reloadData;

	public TextureImpl(String reference, int target, int textureId) {
		this.reference = reference;
		this.target = target;
		this.textureId = textureId;
		lastBind = this;
	}

	public void setTextureWidth(int textureWidth) {
		this.textureWidth = textureWidth;
	}

	public void setTextureHeight(int textureHeight) {
		this.textureHeight = textureHeight;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setAlpha(boolean alpha) {
		this.alpha = alpha;
	}

	public void setTextureData(int pixelFormat, int componentCount, int minFilter, int magFilter, ByteBuffer textureBuffer) {
		reloadData = new ReloadData();
		reloadData.pixelFormat = pixelFormat;
		reloadData.componentCount = componentCount;
		reloadData.minFilter = minFilter;
		reloadData.magFilter = magFilter;
		reloadData.textureBuffer = textureBuffer;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public class ReloadData {

		public int pixelFormat;
		public int componentCount;
		public int minFilter;
		public int magFilter;
		public ByteBuffer textureBuffer;
	}
}
