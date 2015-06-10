package de.blogspot.wrongtracks.twitter.rest.downloader;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Transformer {

	public static void main(String[] args) throws Exception {
		for (String filename : args) {
			File oldFile = new File(filename);
			File newFile = new File(filename + "New");
			try (Scanner scanner = new Scanner(oldFile);
					FileWriter writer = new FileWriter(newFile)) {
				writer.write("[");
				while (scanner.hasNextLine()) {
					writer.write(scanner.nextLine() + ",\n");
				}
				writer.write("]");
			}
		}
	}
}
