package de.dc.lwjgl3.utils.opengl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CompositeImageData implements LoadableImageData {

	@Override
	public ByteBuffer loadImage(InputStream is, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
		// TODO: CompositeIOException exception = new CompositeIOException();
		ByteBuffer buffer = null;
		InputStream stream = new BufferedInputStream(is, is.available());
		stream.mark(is.available());

		for (int i = 0; i < sources.size(); i++) {
			stream.reset();
			try {
				LoadableImageData imageData = (LoadableImageData) sources.get(i);
				buffer = imageData.loadImage(stream, flipped, forceAlpha, transparent);
				picked = imageData;
			} catch (Exception e) {
				System.out.println("WARN: " + sources.get(i).getClass() + " failed to read the data");
			}
		}

		return buffer;
	}

	@Override
	public ByteBuffer loadImage(InputStream is, boolean flipped, int[] transparent) throws IOException {
		return loadImage(is, flipped, false, transparent);
	}

	@Override
	public int getWidth() {
		checkPicked();
		return picked.getWidth();
	}

	private void checkPicked() {
		if (null == picked) {
			throw new RuntimeException("Attempt to make use of uninitialised or invalid composite image data");
		}
	}

	@Override
	public int getHeight() {
		checkPicked();
		return picked.getHeight();
	}

	@Override
	public int getDepth() {
		checkPicked();
		return picked.getDepth();
	}

	@Override
	public int getTextureWidth() {
		checkPicked();
		return picked.getTextureWidth();
	}

	@Override
	public int getTextureHeight() {
		checkPicked();
		return picked.getTextureHeight();
	}

	private List sources = new ArrayList();

	private LoadableImageData picked;

	public void add(LoadableImageData imageData) {
		sources.add(imageData);
	}
}
