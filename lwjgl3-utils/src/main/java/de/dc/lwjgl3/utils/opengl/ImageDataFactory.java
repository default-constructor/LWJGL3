package de.dc.lwjgl3.utils.opengl;

public class ImageDataFactory {

	protected static final String PNG_LOADER = null;

	private static boolean pngLoaderPropertyChecked = false;
	private static boolean usePNGLoader = true;

	public static LoadableImageData getImageData(String reference) {
		// checkProperty();

		reference = reference.toLowerCase();
		if (reference.toLowerCase().endsWith(".tga")) {
			// TODO: return new TGAImageData();
		}
		if (reference.toLowerCase().endsWith(".png")) {
			CompositeImageData data = new CompositeImageData();
			if (usePNGLoader) {
				data.add(new PNGImageData());
			}
			data.add(new ImageIOImageData());
			return data;
		}
		return new ImageIOImageData();
	}

	private static void checkProperty() {
		if (!pngLoaderPropertyChecked) {
			pngLoaderPropertyChecked = true;
			// TODO: 
		}
	}
}
