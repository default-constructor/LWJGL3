package de.dc.lwjgl3.gameengine.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class FileUtil {

	public static String loadAsString(String fileName) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			while (null != (line = reader.readLine())) {
				builder.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file '" + fileName + "'.");
			e.printStackTrace();
		}
		return builder.toString();
	}

	public static BufferedReader loadFile(String fileName) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Could not load file " + fileName + ".");
		}
		return new BufferedReader(fileReader);
	}

	public static BufferedImage loadImage(String fileName) {
		try {
			return ImageIO.read(new File(fileName));
		} catch (IOException e) {
			System.err.println("Could not load image.");
			return null;
		}
	}

	private FileUtil() {
		//
	}
}
