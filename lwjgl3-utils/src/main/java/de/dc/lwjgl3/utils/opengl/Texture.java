package de.dc.lwjgl3.utils.opengl;

public interface Texture {

	public boolean hasAlpha();

	public String getTextureReference();

	public void bind();

	public int getImageHeight();

	public int getImageWidth();

	public float getHeight();

	public float getWidth();

	public int getTextureHeight();

	public int getTextureWidth();

	public void release();

	public int getTextureId();

	public byte[] getTextureData();

	public void setTextureFilter(int textureFilter);
}
